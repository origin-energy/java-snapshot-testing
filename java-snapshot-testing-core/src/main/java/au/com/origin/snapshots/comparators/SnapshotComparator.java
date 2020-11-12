package au.com.origin.snapshots.comparators;

public interface SnapshotComparator<T> {
    CompareResult<T> compare(String snapshotName, String rawSnapshot, String currentObject);
}
