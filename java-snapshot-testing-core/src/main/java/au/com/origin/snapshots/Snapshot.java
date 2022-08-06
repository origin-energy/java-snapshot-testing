package au.com.origin.snapshots;

import au.com.origin.snapshots.annotations.SnapshotName;
import au.com.origin.snapshots.comparators.SnapshotComparator;
import au.com.origin.snapshots.exceptions.SnapshotMatchException;
import au.com.origin.snapshots.reporters.SnapshotReporter;
import au.com.origin.snapshots.serializers.SnapshotSerializer;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
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
  }

  public void toMatchSnapshot() {
    Set<String> rawSnapshots = snapshotFile.getRawSnapshots();

    String rawSnapshot = getRawSnapshot(rawSnapshots);
    String currentObject = takeSnapshot();

    if (rawSnapshot != null && shouldUpdateSnapshot()) {
      snapshotFile.getRawSnapshots().remove(rawSnapshot);
      rawSnapshot = null;
    }

    if (rawSnapshot != null) {
      snapshotFile.pushDebugSnapshot(currentObject.trim());

      // Match existing Snapshot
      if (!snapshotComparator.matches(getSnapshotName(), rawSnapshot, currentObject)) {
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
            reporter.report(getSnapshotName(), rawSnapshot, currentObject);
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
        throw new SnapshotMatchException("Snapshot [" + getSnapshotName() + "] not found. Has this snapshot been committed ?");
      } else {
        log.warn("We detected you are running on a developer machine - if this is incorrect please override the isCI() method in SnapshotConfig");
        // Create New Snapshot
        snapshotFile.pushSnapshot(currentObject);
        snapshotFile.pushDebugSnapshot(currentObject.trim());
      }
    }
  }

  private boolean shouldUpdateSnapshot() {
    if (snapshotConfig.updateSnapshot().isPresent()) {
      return getSnapshotName().contains(snapshotConfig.updateSnapshot().get());
    } else {
      return false;
    }
  }

  private String getRawSnapshot(Collection<String> rawSnapshots) {
    for (String rawSnapshot : rawSnapshots) {
      if (rawSnapshot.contains(getSnapshotName())) {
        return rawSnapshot;
      }
    }
    return null;
  }

  private String takeSnapshot() {
    return getSnapshotName() + snapshotSerializer.apply(current);
  }

  String getSnapshotName() {
    String scenarioFormat = scenario == null ? "" : "[" + scenario + "]";
    SnapshotName snapshotName = testMethod.getAnnotation(SnapshotName.class);
    String pathFormat = snapshotName == null ?
        testClass.getName() + "." + testMethod.getName() :
        snapshotName.value();
    return pathFormat + scenarioFormat + "=";
  }
}
