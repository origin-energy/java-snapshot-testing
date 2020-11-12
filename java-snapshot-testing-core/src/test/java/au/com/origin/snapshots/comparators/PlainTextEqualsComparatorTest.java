package au.com.origin.snapshots.comparators;

import au.com.origin.snapshots.reporters.PlainTextSnapshotDiffReporter;
import org.assertj.core.api.Assertions;
import org.assertj.core.util.diff.Patch;
import org.junit.jupiter.api.Test;

class PlainTextEqualsComparatorTest {

    private static final PlainTextEqualsComparator COMPARATOR = new PlainTextEqualsComparator();

    @Test
    void successfulComparison() {
        CompareResult<Patch<String>> result = COMPARATOR.compare("snap1", "blah", "blah");

        Assertions.assertThat(result).isEqualTo(CompareUtils.success());
    }

    @Test
    void failingComparison() {
        CompareResult<Patch<String>> result = COMPARATOR.compare("snap1", "blah", "blahblah");

        Assertions.assertThat(result.isSnapshotsMatch()).isFalse();
        Assertions.assertThat(result.getComparisonResult()).isNotEmpty();

        String diffString = PlainTextSnapshotDiffReporter.getDiffString(result.getComparisonResult().get());
        String trimmed = diffString.replace("\n", " ").replaceAll(" +", " ");

        Assertions.assertThat(trimmed).contains("expecting: [\"blah\"] but was: [\"blahblah\"]");
    }
}
