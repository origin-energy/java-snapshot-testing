package au.com.origin.snapshots.reporters;

import au.com.origin.snapshots.comparators.PlainTextEqualsComparator;
import au.com.origin.snapshots.comparators.SnapshotComparator;
import au.com.origin.snapshots.exceptions.SnapshotMatchException;
import org.assertj.core.util.diff.Patch;

import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

public class PlainTextSnapshotDiffReporter extends AbstractComparatorAwareSnapshotDiffReporter<Patch<String>> {

    private static final Supplier<IllegalStateException> NO_DIFF_EXCEPTION_SUPPLIER =
            () -> new IllegalStateException("No differences found. Potential mismatch between comparator and reporter");

    @Override
    protected List<Class<? extends SnapshotComparator<Patch<String>>>> supportedComparators() {
        return Collections.singletonList(PlainTextEqualsComparator.class);
    }

    @Override
    protected void doReport(Patch<String> patch, String currentObject) {
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
