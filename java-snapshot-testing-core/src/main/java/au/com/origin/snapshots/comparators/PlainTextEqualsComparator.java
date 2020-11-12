package au.com.origin.snapshots.comparators;

import org.assertj.core.util.diff.DiffUtils;
import org.assertj.core.util.diff.Patch;

import java.util.Arrays;

public class PlainTextEqualsComparator implements SnapshotComparator<Patch<String>> {

    @Override
    public CompareResult<Patch<String>> compare(String snapshotName, String rawSnapshot, String currentObject) {
        if (!rawSnapshot.trim().equals(currentObject.trim())) {
            return CompareUtils.failure(getDiff(rawSnapshot, currentObject));
        }
        return CompareUtils.success();
    }

    private static Patch<String> getDiff(String rawSnapshot, String currentObject) {
        return DiffUtils.diff(
                Arrays.asList(rawSnapshot.trim().split("\n")),
                Arrays.asList(currentObject.trim().split("\n")));
    }
}
