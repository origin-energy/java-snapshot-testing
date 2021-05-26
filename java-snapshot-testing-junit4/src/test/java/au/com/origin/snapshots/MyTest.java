package au.com.origin.snapshots;

import au.com.origin.snapshots.junit4.SnapshotRunner;
import junit.framework.TestCase;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(SnapshotRunner.class)
public class MyTest {

  @Test
  public void someTest(Expect expect) {
    expect.toMatchSnapshot("Hello World");
  }

  @Test
  public void aNormalTest() {
    TestCase.assertTrue(true);
  }
}
