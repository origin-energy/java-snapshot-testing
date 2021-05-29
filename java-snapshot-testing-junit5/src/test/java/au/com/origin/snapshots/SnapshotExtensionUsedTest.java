package au.com.origin.snapshots;

import au.com.origin.snapshots.annotations.SnapshotName;
import au.com.origin.snapshots.junit5.SnapshotExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(SnapshotExtension.class)
public class SnapshotExtensionUsedTest {

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
    expect.toMatchSnapshot("Hello World");
  }

  @Test
  public void shouldUseExtensionAgainViaInstanceVariable() {
    expect.toMatchSnapshot("Hello World", "Hello World Again");
  }

  @SnapshotName("hello_world")
  @Test
  public void snapshotWithName() {
    expect.toMatchSnapshot("Hello World", "Hello World");
  }

  @SnapshotName("hello_world_2")
  @Test
  public void snapshotWithNameAgain() {
    expect.toMatchSnapshot("Hello World", "Hello World Again");
  }

}
