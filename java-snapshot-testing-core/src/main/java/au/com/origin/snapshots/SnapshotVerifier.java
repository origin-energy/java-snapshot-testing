package au.com.origin.snapshots;

import au.com.origin.snapshots.annotations.SnapshotName;
import au.com.origin.snapshots.annotations.UseSnapshotConfig;
import au.com.origin.snapshots.config.SnapshotConfig;
import au.com.origin.snapshots.exceptions.SnapshotExtensionException;
import au.com.origin.snapshots.exceptions.SnapshotMatchException;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.regex.Matcher;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class SnapshotVerifier {

  private final Class<?> testClass;
  private final SnapshotFile snapshotFile;
  private final SnapshotConfig config;
  private final boolean failOnOrphans;

  private final Collection<SnapshotContext> calledSnapshots =
      Collections.synchronizedCollection(new ArrayList<>());

  public SnapshotVerifier(SnapshotConfig frameworkSnapshotConfig, Class<?> testClass) {
    this(frameworkSnapshotConfig, testClass, false);
  }

  /**
   * Instantiate before any tests have run for a given class
   *
   * @param frameworkSnapshotConfig configuration to use
   * @param failOnOrphans should the test break if snapshots exist with no matching method in the
   *     test class
   * @param testClass reference to class under test
   */
  public SnapshotVerifier(
      SnapshotConfig frameworkSnapshotConfig, Class<?> testClass, boolean failOnOrphans) {
    try {
      verifyNoConflictingSnapshotNames(testClass);

      UseSnapshotConfig customConfig = testClass.getAnnotation(UseSnapshotConfig.class);
      SnapshotConfig snapshotConfig =
          customConfig == null ? frameworkSnapshotConfig : customConfig.value().newInstance();

      // Matcher.quoteReplacement required for Windows
      String testFilename =
          testClass.getName().replaceAll("\\.", Matcher.quoteReplacement(File.separator)) + ".snap";

      File fileUnderTest = new File(testFilename);
      File snapshotDir = new File(fileUnderTest.getParentFile(), snapshotConfig.getSnapshotDir());

      // Support legacy trailing space syntax
      String testSrcDir = snapshotConfig.getOutputDir();
      String testSrcDirNoTrailing =
          testSrcDir.endsWith("/") ? testSrcDir.substring(0, testSrcDir.length() - 1) : testSrcDir;
      SnapshotFile snapshotFile =
          new SnapshotFile(
              testSrcDirNoTrailing,
              snapshotDir.getPath() + File.separator + fileUnderTest.getName(),
              testClass);

      this.testClass = testClass;
      this.snapshotFile = snapshotFile;
      this.config = snapshotConfig;
      this.failOnOrphans = failOnOrphans;

    } catch (IOException | InstantiationException | IllegalAccessException e) {
      throw new SnapshotExtensionException(e.getMessage());
    }
  }

  private void verifyNoConflictingSnapshotNames(Class<?> testClass) {
    Map<String, List<String>> allSnapshotAnnotationNames =
        Arrays.stream(testClass.getDeclaredMethods())
            .filter(it -> it.isAnnotationPresent(SnapshotName.class))
            .map(it -> it.getAnnotation(SnapshotName.class))
            .map(SnapshotName::value)
            .collect(Collectors.groupingBy(String::toString));

    boolean hasDuplicateSnapshotNames =
        allSnapshotAnnotationNames.entrySet().stream()
                .filter(it -> it.getValue().size() > 1)
                .peek(
                    it ->
                        log.error(
                            "Oops, looks like you set the same name of two separate snapshots @SnapshotName(\"{}\") in class {}",
                            it.getKey(),
                            testClass.getName()))
                .count()
            > 0;
    if (hasDuplicateSnapshotNames) {
      throw new SnapshotExtensionException("Duplicate @SnapshotName annotations found!");
    }
  }

  @SneakyThrows
  public SnapshotContext expectCondition(Method testMethod, Object object) {
    SnapshotContext snapshotContext =
        new SnapshotContext(config, snapshotFile, testClass, testMethod, object);
    calledSnapshots.add(snapshotContext);
    return snapshotContext;
  }

  public void validateSnapshots() {
    Set<Snapshot> rawSnapshots = snapshotFile.getSnapshots();
    Set<String> snapshotNames =
        calledSnapshots.stream()
            .map(SnapshotContext::resolveSnapshotIdentifier)
            .collect(Collectors.toSet());
    List<Snapshot> unusedSnapshots = new ArrayList<>();

    for (Snapshot rawSnapshot : rawSnapshots) {
      boolean foundSnapshot = false;
      for (String snapshotName : snapshotNames) {
        if (rawSnapshot.getIdentifier().equals(snapshotName)) {
          foundSnapshot = true;
          break;
        }
      }
      if (!foundSnapshot) {
        unusedSnapshots.add(rawSnapshot);
      }
    }
    if (unusedSnapshots.size() > 0) {
      List<String> unusedRawSnapshots =
          unusedSnapshots.stream().map(Snapshot::raw).collect(Collectors.toList());
      String errorMessage =
          "All unused Snapshots:\n"
              + String.join("\n", unusedRawSnapshots)
              + "\n\nHave you deleted tests? Have you renamed a test method?";
      if (failOnOrphans) {
        log.error(errorMessage);
        throw new SnapshotMatchException("ERROR: Found orphan snapshots");
      } else {
        log.warn(errorMessage);
      }
    }
    snapshotFile.cleanup();
  }
}
