package au.com.origin.snapshots;

import au.com.origin.snapshots.junit5.SnapshotExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(SnapshotExtension.class)
public class SnapshotExtensionUsedTest {

  @Test
  public void shouldUseExtension(Expect expect) {
    expect.toMatchSnapshot("Hello World");
  }

  @Test
  public void shouldUseExtensionAgain(Expect expect) {
    expect.toMatchSnapshot("Hello World", "Hello World Again");
  }
}
