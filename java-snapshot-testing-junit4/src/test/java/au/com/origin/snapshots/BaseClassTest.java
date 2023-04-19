package au.com.origin.snapshots;

import au.com.origin.snapshots.junit4.SnapshotRunner;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

@RunWith(Enclosed.class)
public class BaseClassTest {

  static class TestBase {
    Expect expect;
  }

  @RunWith(SnapshotRunner.class)
  public static class NestedClass extends TestBase {

    @Test
    public void helloWorldTest() {
      expect.toMatchSnapshot("Hello World");
    }
  }
}
