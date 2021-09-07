package au.com.origin.snapshots;

import au.com.origin.snapshots.annotations.SnapshotName;
import au.com.origin.snapshots.comparators.SnapshotComparator;
import au.com.origin.snapshots.config.SnapshotConfig;
import au.com.origin.snapshots.exceptions.SnapshotMatchException;
import au.com.origin.snapshots.reporters.SnapshotReporter;
import au.com.origin.snapshots.serializers.SnapshotSerializer;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
public class SnapshotContext {

  private final SnapshotConfig snapshotConfig;
  private final SnapshotFile snapshotFile;

  @Getter
  final Class<?> testClass;

  @Getter
  final Method testMethod;

  final Object[] current;
  private final boolean isCI;

  @Setter
  private SnapshotSerializer snapshotSerializer;
  @Setter
  private SnapshotComparator snapshotComparator;
  @Setter
  private List<SnapshotReporter> snapshotReporters;

  @Setter
  @Getter
  String scenario;

  @Getter
  SnapshotHeader header = new SnapshotHeader();

  SnapshotContext(
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

    Set<Snapshot> rawSnapshots = snapshotFile.getSnapshots();
    Snapshot previousSnapshot = getRawSnapshot(rawSnapshots);
    Snapshot currentSnapshot = takeSnapshot();

    if (previousSnapshot != null && shouldUpdateSnapshot()) {
      snapshotFile.getSnapshots().remove(previousSnapshot);
      previousSnapshot = null;
    }

    if (previousSnapshot != null) {
      // Match existing Snapshot
      if (!snapshotComparator.matches(previousSnapshot, currentSnapshot)) {
        snapshotFile.createDebugFile(currentSnapshot);

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
            reporter.report(previousSnapshot, currentSnapshot);
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
        throw new SnapshotMatchException("Snapshot [" + resolveSnapshotIdentifier() + "] not found. Has this snapshot been committed ?");
      } else {
        log.warn("We detected you are running on a developer machine - if this is incorrect please override the isCI() method in SnapshotConfig");
        // Create New Snapshot
        snapshotFile.push(currentSnapshot);
      }
    }
    snapshotFile.deleteDebugFile();
  }

  private boolean shouldUpdateSnapshot() {
    if (snapshotConfig.updateSnapshot().isPresent()) {
      return resolveSnapshotIdentifier().contains(snapshotConfig.updateSnapshot().get());
    } else {
      return false;
    }
  }

  private Snapshot getRawSnapshot(Collection<Snapshot> rawSnapshots) {
    for (Snapshot rawSnapshot : rawSnapshots) {
      if (rawSnapshot.getIdentifier().equals(resolveSnapshotIdentifier())) {
        return rawSnapshot;
      }
    }
    return null;
  }

  private Snapshot takeSnapshot() {
    SnapshotSerializerContext sg = SnapshotSerializerContext.from(this);
    return snapshotSerializer.apply(current, sg);
  }

  String resolveSnapshotIdentifier() {
    String scenarioFormat = scenario == null ? "" : "[" + scenario + "]";
    SnapshotName snapshotName = testMethod.getAnnotation(SnapshotName.class);
    String pathFormat = snapshotName == null ?
        testClass.getName() + "." + testMethod.getName() :
        snapshotName.value();
    return pathFormat + scenarioFormat;
  }
}
