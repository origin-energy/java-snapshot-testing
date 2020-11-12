package au.com.origin.snapshots.reporters;

import au.com.origin.snapshots.comparators.CompareResult;
import au.com.origin.snapshots.comparators.SnapshotComparator;

import java.util.List;
import java.util.function.Supplier;

/*
 * Adapter to make sure implementations of SnapshotDiffReporter are type-safe.
 * Helps to avoid having to suppress the unchecked warning on every implementation.
 */
public abstract class AbstractComparatorAwareSnapshotDiffReporter<T> implements SnapshotDiffReporter {

    private static final Supplier<IllegalStateException> NO_RESULT_EXCEPTION_SUPPLIER =
            () -> new IllegalStateException("No comparison result found for reporting");

    @Override
    public final boolean supportsComparator(SnapshotComparator<?> given) {
        return this.supportedComparators().stream().anyMatch(supported -> supported.isInstance(given));
    }

    @Override
    @SuppressWarnings("unchecked")
    public final void reportDiff(CompareResult<?> compareResult, String currentObject) {
        this.doReport((T) compareResult.getComparisonResult().orElseThrow(NO_RESULT_EXCEPTION_SUPPLIER), currentObject);
    }

    protected abstract List<Class<? extends SnapshotComparator<T>>> supportedComparators();

    protected abstract void doReport(T result, String currentObject);
}
