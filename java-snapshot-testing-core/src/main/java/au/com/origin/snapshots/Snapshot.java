package au.com.origin.snapshots;

import au.com.origin.snapshots.annotations.SnapshotName;
import au.com.origin.snapshots.comparators.SnapshotComparator;
import au.com.origin.snapshots.exceptions.SnapshotMatchException;
import au.com.origin.snapshots.reporters.SnapshotReporter;
import au.com.origin.snapshots.serializers.SnapshotSerializer;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class Snapshot {

  private final SnapshotConfig snapshotConfig;
  private final SnapshotFile snapshotFile;
  private final Class<?> testClass;
  private final Method testMethod;
  private final Object[] current;
  private final boolean isCI;

  @Setter
  private SnapshotSerializer snapshotSerializer;
  @Setter
  private SnapshotComparator snapshotComparator;
  @Setter
  private List<SnapshotReporter> snapshotReporters;
  @Setter
  private String scenario;
  @Setter
  private String name;

  Snapshot(
      SnapshotConfig snapshotConfig,
      SnapshotFile snapshotFile,
      Class<?> testClass,
      Method testMethod,
      Object... current) {
    this.snapshotConfig = snapshotConfig;
    this.snapshotFile = snapshotFile;
    this.testClass = testClass;
    this.testMethod = testMethod;
    this.current = current;

    this.isCI = snapshotConfig.isCI();
    this.snapshotSerializer = snapshotConfig.getSerializer();
    this.snapshotComparator = snapshotConfig.getComparator();
    this.snapshotReporters = snapshotConfig.getReporters();
    this.scenario = null;
    this.name = defaultName();
  }


  public void toMatchSnapshot() {
    snapshotFile.verifyNoDuplicates(name);

    Set<String> rawSnapshots = snapshotFile.getRawSnapshots();

    String rawSnapshot = getRawSnapshot(rawSnapshots);

    String currentObject = takeSnapshot();

    if (rawSnapshot != null && shouldUpdateSnapshot()) {
      snapshotFile.getRawSnapshots().remove(rawSnapshot);
      rawSnapshot = null;
    }

    if (rawSnapshot != null) {
      // Match existing Snapshot
      if (!snapshotComparator.matches(name, rawSnapshot, currentObject)) {
        snapshotFile.createDebugFile(currentObject.trim());

        List<SnapshotReporter> reporters = snapshotReporters
            .stream()
            .filter(reporter -> reporter.supportsFormat(snapshotSerializer.getOutputFormat()))
            .collect(Collectors.toList());

        if (reporters.isEmpty()) {
          String comparator = snapshotComparator.getClass().getSimpleName();
          throw new IllegalStateException("No compatible reporters found for comparator " + comparator);
        }

        List<Throwable> errors = new ArrayList<>();

        for (SnapshotReporter reporter : reporters) {
          try {
            reporter.report(name, rawSnapshot, currentObject);
          } catch (Throwable t) {
            errors.add(t);
          }
        }

        if (!errors.isEmpty()) {
          throw new SnapshotMatchException("Error(s) matching snapshot(s)", errors);
        }
      }
    } else {
      if (this.isCI) {
        log.error("We detected you are running on a CI Server - if this is incorrect please override the isCI() method in SnapshotConfig");
        throw new SnapshotMatchException("Snapshot [" + name + "] not found. Has this snapshot been committed ?");
      } else {
        log.warn("We detected you are running on a developer machine - if this is incorrect please override the isCI() method in SnapshotConfig");
        // Create New Snapshot
        snapshotFile.push(currentObject);
      }
    }
    snapshotFile.deleteDebugFile();
  }

  private boolean shouldUpdateSnapshot() {
    if (snapshotConfig.updateSnapshot().isPresent()) {
      return name.contains(snapshotConfig.updateSnapshot().get());
    } else {
      return false;
    }
  }

  private String getRawSnapshot(Collection<String> rawSnapshots) {
    for (String rawSnapshot : rawSnapshots) {
      if (rawSnapshot.contains(getQualifiedName())) {
        return rawSnapshot;
      }
    }
    return null;
  }

  private String takeSnapshot() {
    return getQualifiedName() + snapshotSerializer.apply(current);
  }

  private String defaultName() {
    SnapshotName snapshotName = testMethod.getAnnotation(SnapshotName.class);
    return snapshotName == null ?
        testClass.getName() + "." + testMethod.getName() :
        snapshotName.value();
  }

  String getQualifiedName() {
    String scenarioFormat = scenario == null ? "" : "[" + scenario + "]";
    return name + scenarioFormat + "=";
  }
}
