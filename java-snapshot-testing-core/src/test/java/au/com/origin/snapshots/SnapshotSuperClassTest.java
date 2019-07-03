package au.com.origin.snapshots;

import static au.com.origin.snapshots.SnapshotMatcher.expect;

import org.junit.jupiter.api.Test;

public abstract class SnapshotSuperClassTest {

  public abstract String getName();

  @Test
  void shouldMatchSnapshotOne() {
    expect(getName()).toMatchSnapshot();
  }
}
