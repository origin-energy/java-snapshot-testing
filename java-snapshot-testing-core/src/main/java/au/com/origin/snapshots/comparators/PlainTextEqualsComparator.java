package au.com.origin.snapshots.comparators;

public class PlainTextEqualsComparator implements SnapshotComparator {

    @Override
    public boolean match(String snapshotName, String rawSnapshot, String currentObject) {
        return rawSnapshot.trim().equals(currentObject.trim());
    }
}
