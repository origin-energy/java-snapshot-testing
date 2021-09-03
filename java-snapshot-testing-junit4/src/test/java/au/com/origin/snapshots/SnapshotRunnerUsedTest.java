package au.com.origin.snapshots;

import au.com.origin.snapshots.annotations.SnapshotName;
import au.com.origin.snapshots.junit4.SnapshotRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(SnapshotRunner.class)
public class SnapshotRunnerUsedTest {

  private Expect expect;

  @Test
  public void shouldUseExtension(Expect expect) {
    expect.toMatchSnapshot("Hello World");
  }

  @Test
  public void shouldUseExtensionAgain(Expect expect) {
    expect.toMatchSnapshot("Hello World", "Hello World Again");
  }

  @Test
  public void shouldUseExtensionViaInstanceVariable() {
    this.expect.toMatchSnapshot("Hello World");
  }

  @Test
  public void shouldUseExtensionAgainViaInstanceVariable() {
    this.expect.toMatchSnapshot("Hello World", "Hello World Again");
  }

  @SnapshotName("hello_world")
  @Test
  public void shouldUseExtensionWithSnapshotName() {
    expect.toMatchSnapshot("Hello World");
  }

  @SnapshotName("hello_world_again")
  @Test
  public void shouldUseExtensionAgainWithSnapshotName(Expect expect) {
    expect.toMatchSnapshot("Hello World", "Hello World Again");
  }
}
