package au.com.origin.snapshots.comparators;

import au.com.origin.snapshots.SnapshotContext;

public class PlainTextEqualsComparator implements SnapshotComparator {

    @Override
    public boolean matches(SnapshotContext context) {
        return context.getExistingSnapshot().trim().equals(context.getIncomingSnapshot().trim());
    }
}
