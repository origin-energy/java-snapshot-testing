package au.com.origin.snapshots.docs;

import au.com.origin.snapshots.junit5.SnapshotExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import au.com.origin.snapshots.Expect;

// Ensure you extend your test class with the SnapshotExtension
@ExtendWith({SnapshotExtension.class})
public class JUnit5Example {

  @Test
  public void myTest1(Expect expect) {
    // Verify your snapshot
    expect.toMatchSnapshot("Hello World");
  }

  @Test
  public void myTest2(Expect expect) {
    expect.toMatchSnapshot("Hello World Again");
  }
}