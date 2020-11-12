package au.com.origin.snapshots.reporters;

import au.com.origin.snapshots.comparators.CompareResult;
import au.com.origin.snapshots.comparators.SnapshotComparator;

public interface SnapshotDiffReporter {

    boolean supportsComparator(SnapshotComparator<?> comparator);

    void reportDiff(CompareResult<?> compareResult, String currentObject);
}
