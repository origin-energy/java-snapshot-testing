package au.com.origin.snapshots.reporters;

import au.com.origin.snapshots.SnapshotContext;

public interface SnapshotReporter {
    boolean supportsFormat(String outputFormat);
    void reportFailure(SnapshotContext context);
    void reportSuccess(SnapshotContext snapshotContext);
}
