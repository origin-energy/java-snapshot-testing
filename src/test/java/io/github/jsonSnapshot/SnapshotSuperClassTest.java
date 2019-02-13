package io.github.jsonSnapshot;

import static io.github.jsonSnapshot.SnapshotMatcher.expect;

import org.junit.jupiter.api.Test;

public abstract class SnapshotSuperClassTest {

  public abstract String getName();

  @Test
  void shouldMatchSnapshotOne() {
    expect(getName()).toMatchSnapshot();
  }
}
