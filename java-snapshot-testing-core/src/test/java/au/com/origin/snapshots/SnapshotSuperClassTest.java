package au.com.origin.snapshots;

import org.junit.jupiter.api.Test;

import static au.com.origin.snapshots.SnapshotMatcher.expect;

public abstract class SnapshotSuperClassTest {

  public abstract String getName();

  @Test
  void shouldMatchSnapshotOne() {
    expect(getName()).toMatchSnapshot();
  }
}
