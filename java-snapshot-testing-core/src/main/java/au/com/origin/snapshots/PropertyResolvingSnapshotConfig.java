package au.com.origin.snapshots;

import au.com.origin.snapshots.comparators.SnapshotComparator;
import au.com.origin.snapshots.reporters.SnapshotReporter;
import au.com.origin.snapshots.serializers.SnapshotSerializer;

import java.util.List;

public class PropertyResolvingSnapshotConfig implements SnapshotConfig {

  @Override
  public String getOutputDir() {
    return SnapshotProperties.getOrThrow("output-dir");
  }

  @Override
  public String getSnapshotDir() {
    return SnapshotProperties.getOrThrow("snapshot-dir");
  }

  @Override
  public SnapshotSerializer getSerializer() {
    return SnapshotProperties.getInstance("serializer");
  }

  @Override
  public SnapshotComparator getComparator() {
    return SnapshotProperties.getInstance("comparator");
  }

  @Override
  public List<SnapshotReporter> getReporters() {
    return SnapshotProperties.getInstances("reporters");
  }

  @Override
  public boolean isCI() {
    String envVariable = SnapshotProperties.getOrThrow("ci-env-var");
    return System.getenv(envVariable) != null;
  }
}
