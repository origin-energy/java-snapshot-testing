package au.com.origin.snapshots;

import au.com.origin.snapshots.junit4.SnapshotRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(SnapshotRunner.class)
public class SnapshotRuleUsedTest {

  @Test
  public void shouldUseExtension(Expect expect) {
    expect.toMatchSnapshot("Hello World");
  }

  @Test
  public void shouldUseExtensionAgain(Expect expect) {
    expect.toMatchSnapshot("Hello World", "Hello World Again");
  }
}
