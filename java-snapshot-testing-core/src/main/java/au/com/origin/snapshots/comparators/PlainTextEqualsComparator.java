package au.com.origin.snapshots.comparators;

public class PlainTextEqualsComparator implements SnapshotComparator {

    @Override
    public boolean matches(String snapshotName, String rawSnapshot, String currentObject) {
        return rawSnapshot.trim().equals(currentObject.trim());
    }
}
