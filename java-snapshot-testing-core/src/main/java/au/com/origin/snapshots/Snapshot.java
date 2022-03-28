package au.com.origin.snapshots;

import au.com.origin.snapshots.comparators.SnapshotComparator;
import au.com.origin.snapshots.exceptions.SnapshotMatchException;
import au.com.origin.snapshots.reporters.SnapshotReporter;
import au.com.origin.snapshots.serializers.SnapshotSerializer;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

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
  private final String snapshotName;
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
      String snapshotName,
      Object... current) {
    this.snapshotConfig = snapshotConfig;
    this.snapshotFile = snapshotFile;
    this.testClass = testClass;
    this.snapshotName = snapshotName;
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
      // Match existing Snapshot
      if (!snapshotComparator.matches(getSnapshotPath(), rawSnapshot, currentObject)) {
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
            reporter.report(getSnapshotPath(), rawSnapshot, currentObject);
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
        throw new SnapshotMatchException("Snapshot [" + getSnapshotPath() + "] not found. Has this snapshot been committed ?");
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
      return getSnapshotPath().contains(snapshotConfig.updateSnapshot().get());
    } else {
      return false;
    }
  }

  private String getRawSnapshot(Collection<String> rawSnapshots) {
    for (String rawSnapshot : rawSnapshots) {
      if (rawSnapshot.contains(getSnapshotPath())) {
        return rawSnapshot;
      }
    }
    return null;
  }

  private String takeSnapshot() {
    return getSnapshotPath() + snapshotSerializer.apply(current);
  }

  String getSnapshotPath() {
    String scenarioFormat = scenario == null ? "" : "[" + scenario + "]";
    return snapshotName + scenarioFormat + "=";
  }
}
