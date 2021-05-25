package au.com.origin.snapshots.comparators;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class PlainTextEqualsComparatorTest {

  private static final PlainTextEqualsComparator COMPARATOR = new PlainTextEqualsComparator();

  @Test
  void successfulComparison() {
    Assertions.assertThat(COMPARATOR.matches("snap1", "blah", "blah")).isTrue();
  }

  @Test
  void failingComparison() {
    Assertions.assertThat(COMPARATOR.matches("snap1", "blah", "blahblah")).isFalse();
  }
}
