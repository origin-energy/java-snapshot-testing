package au.com.origin.snapshots.comparators;

import au.com.origin.snapshots.Snapshot;

public interface SnapshotComparator {
  boolean matches(Snapshot previous, Snapshot current);
}
