package au.com.origin.snapshots.reporters;

import au.com.origin.snapshots.SnapshotContext;
import org.assertj.core.util.diff.DiffUtils;
import org.assertj.core.util.diff.Patch;
import org.opentest4j.AssertionFailedError;

import java.util.Arrays;
import java.util.function.Supplier;

public class PlainTextSnapshotReporter implements SnapshotReporter {

    private static final Supplier<IllegalStateException> NO_DIFF_EXCEPTION_SUPPLIER =
            () -> new IllegalStateException("No differences found. Potential mismatch between comparator and reporter");

    @Override
    public boolean supportsFormat(String outputFormat) {
        return true; // always true
    }

    @Override
    public void reportFailure(SnapshotContext context) {
        Patch<String> patch = DiffUtils.diff(
                Arrays.asList(context.getExistingSnapshot().trim().split("\n")),
                Arrays.asList(context.getIncomingSnapshot().trim().split("\n")));

        String message = "Error on: \n" + context.getIncomingSnapshot().trim() + "\n\n" + getDiffString(patch);

        throw new AssertionFailedError(message, context.getExistingSnapshot(), context.getIncomingSnapshot());
    }

    @Override
    public void reportSuccess(SnapshotContext snapshotContext) {

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
