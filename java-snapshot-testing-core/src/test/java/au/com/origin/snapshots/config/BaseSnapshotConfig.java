package au.com.origin.snapshots.config;

import au.com.origin.snapshots.SnapshotConfig;
import au.com.origin.snapshots.comparators.PlainTextEqualsComparator;
import au.com.origin.snapshots.comparators.SnapshotComparator;
import au.com.origin.snapshots.reporters.PlainTextSnapshotReporter;
import au.com.origin.snapshots.reporters.SnapshotReporter;
import au.com.origin.snapshots.serializers.SnapshotSerializer;
import au.com.origin.snapshots.serializers.ToStringSnapshotSerializer;

import java.util.Collections;
import java.util.List;

public class BaseSnapshotConfig implements SnapshotConfig {

  @Override
  public String getOutputDir() {
    return "src/test/java";
  }

  @Override
  public String getSnapshotDir() {
    return "__snapshots__";
  }

  @Override
  public SnapshotSerializer getSerializer() {
    return new ToStringSnapshotSerializer();
  }

  @Override
  public SnapshotComparator getComparator() {
    return new PlainTextEqualsComparator();
  }

  @Override
  public List<SnapshotReporter> getReporters() {
    return Collections.singletonList(new PlainTextSnapshotReporter());
  }

  @Override
  public boolean isCI() {
    return false;
  }
}
