package au.com.origin.snapshots.reporters;

import au.com.origin.snapshots.Snapshot;

public interface SnapshotReporter {

  boolean supportsFormat(String outputFormat);

  void report(Snapshot previous, Snapshot current);
}
