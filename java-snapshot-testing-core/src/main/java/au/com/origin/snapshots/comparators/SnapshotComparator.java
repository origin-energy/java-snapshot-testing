package au.com.origin.snapshots.comparators;

public interface SnapshotComparator {
    boolean match(String snapshotName, String rawSnapshot, String currentObject);
}
