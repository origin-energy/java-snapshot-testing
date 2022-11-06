package au.com.origin.snapshots.comparators;

import au.com.origin.snapshots.Snapshot;

public class PlainTextEqualsComparator implements SnapshotComparator {

  @Override
  public boolean matches(Snapshot previous, Snapshot current) {
    return previous.getBody().equals(current.getBody());
  }
}
