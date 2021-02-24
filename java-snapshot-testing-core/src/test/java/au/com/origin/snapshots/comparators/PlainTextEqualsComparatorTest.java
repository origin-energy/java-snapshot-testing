package au.com.origin.snapshots.comparators;

import au.com.origin.snapshots.SnapshotContext;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PlainTextEqualsComparatorTest {

    private static final PlainTextEqualsComparator COMPARATOR = new PlainTextEqualsComparator();

    @Test
    void successfulComparison() {
        SnapshotContext context = SnapshotContext.builder()
                .existingSnapshot("foo")
                .incomingSnapshot("foo")
                .build();
        assertThat(COMPARATOR.matches(context)).isTrue();
    }

    @Test
    void failingComparison() {
        SnapshotContext context = SnapshotContext.builder()
                .existingSnapshot("foo")
                .incomingSnapshot("bar")
                .build();
        assertThat(COMPARATOR.matches(context)).isFalse();
    }
}
