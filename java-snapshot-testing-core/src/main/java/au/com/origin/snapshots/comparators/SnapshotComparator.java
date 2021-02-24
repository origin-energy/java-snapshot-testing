package au.com.origin.snapshots.comparators;

import au.com.origin.snapshots.SnapshotContext;

public interface SnapshotComparator {
    boolean matches(SnapshotContext context);
}
