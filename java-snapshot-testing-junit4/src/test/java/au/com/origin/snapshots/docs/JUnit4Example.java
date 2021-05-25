package au.com.origin.snapshots.docs;

import au.com.origin.snapshots.junit4.SnapshotRunner;
import au.com.origin.snapshots.Expect;
import org.junit.Test;
import org.junit.runner.RunWith;

// Ensure you RunWith the SnapshotRunner
@RunWith(SnapshotRunner.class)
public class JUnit4Example {

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
