package au.com.origin.snapshots;

import au.com.origin.snapshots.annotations.UseSnapshotConfig;
import au.com.origin.snapshots.exceptions.SnapshotExtensionException;
import au.com.origin.snapshots.exceptions.SnapshotMatchException;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.regex.Matcher;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.util.Arrays.isNullOrEmpty;

@Slf4j
@RequiredArgsConstructor
public class SnapshotVerifier {

  private final Class<?> testClass;
  private final SnapshotFile snapshotFile;
  private final SnapshotConfig config;
  private final boolean failOnOrphans;

  private final Collection<Snapshot> calledSnapshots = Collections.synchronizedCollection(new ArrayList<>());

  public SnapshotVerifier(SnapshotConfig frameworkSnapshotConfig, Class<?> testClass) {
    this(frameworkSnapshotConfig, testClass, false);
  }

  /**
   * Instantiate before any tests have run for a given class
   *
   * @param frameworkSnapshotConfig configuration to use
   * @param failOnOrphans           should the test break if snapshots exist with no matching method in the test class
   * @param testClass               reference to class under test
   */
  public SnapshotVerifier(SnapshotConfig frameworkSnapshotConfig, Class<?> testClass, boolean failOnOrphans) {
    try {
      UseSnapshotConfig customConfig = testClass.getAnnotation(UseSnapshotConfig.class);
      SnapshotConfig snapshotConfig = customConfig == null ? frameworkSnapshotConfig : customConfig.value().newInstance();

      // Matcher.quoteReplacement required for Windows
      String testFilename = testClass.getName().replaceAll("\\.", Matcher.quoteReplacement(File.separator)) + ".snap";

      File fileUnderTest = new File(testFilename);
      File snapshotDir = new File(fileUnderTest.getParentFile(), snapshotConfig.getSnapshotDir());

      // Support legacy trailing space syntax
      String testSrcDir = snapshotConfig.getOutputDir();
      String testSrcDirNoTrailing = testSrcDir.endsWith("/") ? testSrcDir.substring(0, testSrcDir.length() - 1) : testSrcDir;
      SnapshotFile snapshotFile = new SnapshotFile(
          testSrcDirNoTrailing,
          snapshotDir.getPath() + File.separator + fileUnderTest.getName(),
          testClass,
          snapshotConfig::onSaveSnapshotFile
      );

      this.testClass = testClass;
      this.snapshotFile = snapshotFile;
      this.config = snapshotConfig;
      this.failOnOrphans = failOnOrphans;

    } catch (IOException | InstantiationException | IllegalAccessException e) {
      throw new SnapshotExtensionException(e.getMessage());
    }
  }

  @SneakyThrows
  public Snapshot expectCondition(Method testMethod, Object firstObject, Object... others) {
    Object[] objects = mergeObjects(firstObject, others);
    Snapshot snapshot =
        new Snapshot(config, snapshotFile, testClass, testMethod, objects);
    calledSnapshots.add(snapshot);
    return snapshot;
  }

  public void validateSnapshots() {
    Set<String> rawSnapshots = snapshotFile.getRawSnapshots();
    Set<String> snapshotNames =
        calledSnapshots.stream().map(Snapshot::getSnapshotName).collect(Collectors.toSet());
    List<String> unusedRawSnapshots = new ArrayList<>();

    for (String rawSnapshot : rawSnapshots) {
      boolean foundSnapshot = false;
      for (String snapshotName : snapshotNames) {
        if (rawSnapshot.contains(snapshotName)) {
          foundSnapshot = true;
          break;
        }
      }
      if (!foundSnapshot) {
        unusedRawSnapshots.add(rawSnapshot);
      }
    }
    if (unusedRawSnapshots.size() > 0) {
      String errorMessage = "All unused Snapshots:\n"
          + String.join("\n", unusedRawSnapshots)
          + "\n\nHave you deleted tests? Have you renamed a test method?";
      if (failOnOrphans) {
        log.warn(errorMessage);
        throw new SnapshotMatchException("ERROR: Found orphan snapshots");
      } else {
        log.warn(errorMessage);
      }
    }
    snapshotFile.cleanup();
  }

  private Object[] mergeObjects(Object firstObject, Object[] others) {
    Object[] objects = new Object[1];
    objects[0] = firstObject;
    if (!isNullOrEmpty(others)) {
      objects = Stream.concat(Arrays.stream(objects), Arrays.stream(others))
          .toArray(Object[]::new);
    }
    return objects;
  }
}
