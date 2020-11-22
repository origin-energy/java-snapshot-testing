package au.com.origin.snapshots.comparators;

public interface SnapshotComparator {
    boolean matches(String snapshotName, String rawSnapshot, String currentObject);
}
