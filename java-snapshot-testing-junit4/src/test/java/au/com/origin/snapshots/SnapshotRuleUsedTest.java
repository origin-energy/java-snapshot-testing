package au.com.origin.snapshots;

import au.com.origin.snapshots.annotations.SnapshotName;
import au.com.origin.snapshots.junit4.SnapshotClassRule;
import au.com.origin.snapshots.junit4.SnapshotRule;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

public class SnapshotRuleUsedTest {

  @ClassRule public static SnapshotClassRule snapshotClassRule = new SnapshotClassRule();

  @Rule public SnapshotRule snapshotRule = new SnapshotRule(snapshotClassRule);

  private Expect expect;

  @Test
  public void shouldUseExtensionViaInstanceVariable() {
    this.expect.toMatchSnapshot("Hello World");
  }

  @Test
  public void shouldUseExtensionAgainViaInstanceVariable() {
    this.expect.toMatchSnapshot("Hello World");
  }

  @SnapshotName("hello_world")
  @Test
  public void shouldUseExtensionWithSnapshotName() {
    expect.toMatchSnapshot("Hello World");
  }
}
