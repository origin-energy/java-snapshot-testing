package au.com.origin.snapshots.comparators;

import lombok.Value;

import java.util.Optional;

@Value
public class CompareResult<T> {

    private final boolean snapshotsMatch;
    private final T comparisonResult;

    public Optional<T> getComparisonResult() {
        return Optional.ofNullable(this.comparisonResult);
    }
}
