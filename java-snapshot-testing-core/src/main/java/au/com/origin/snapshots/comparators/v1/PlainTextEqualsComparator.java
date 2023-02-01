package au.com.origin.snapshots.comparators.v1;

import au.com.origin.snapshots.Snapshot;
import au.com.origin.snapshots.comparators.SnapshotComparator;

public class PlainTextEqualsComparator implements SnapshotComparator {

  @Override
  public boolean matches(Snapshot previous, Snapshot current) {
    return previous.getBody().equals(current.getBody());
  }
}
