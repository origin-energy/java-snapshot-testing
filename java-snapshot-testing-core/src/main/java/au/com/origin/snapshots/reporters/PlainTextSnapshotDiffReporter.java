package au.com.origin.snapshots.reporters;

import au.com.origin.snapshots.exceptions.SnapshotMatchException;
import org.assertj.core.util.diff.DiffUtils;
import org.assertj.core.util.diff.Patch;

import java.util.Arrays;
import java.util.function.Supplier;

public class PlainTextSnapshotDiffReporter implements SnapshotDiffReporter {

    private static final Supplier<IllegalStateException> NO_DIFF_EXCEPTION_SUPPLIER =
            () -> new IllegalStateException("No differences found. Potential mismatch between comparator and reporter");

    @Override
    public boolean supportsFormat(String outputFormat) {
        return true; // always true
    }

    @Override
    public void reportDiff(String snapshotName, String rawSnapshot, String currentObject) {
        Patch<String> patch = DiffUtils.diff(
                Arrays.asList(rawSnapshot.trim().split("\n")),
                Arrays.asList(currentObject.trim().split("\n")));
        throw new SnapshotMatchException("Error on: \n" + currentObject.trim() + "\n\n" + getDiffString(patch));
    }

    public static String getDiffString(Patch<String> patch) {
        return patch
                .getDeltas()
                .stream()
                .map(delta -> delta.toString() + "\n")
                .reduce(String::concat)
                .orElseThrow(NO_DIFF_EXCEPTION_SUPPLIER);
    }
}
