package au.com.origin.snapshots;

import au.com.origin.snapshots.junit5.SnapshotExtension;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith({SnapshotExtension.class})
public class BaseClassTest {

  class TestBase {
    Expect expect;
  }

  @Nested
  @ExtendWith(SnapshotExtension.class)
  class NestedClass extends TestBase {

    @Test
    public void helloWorldTest() {
      expect.toMatchSnapshot("Hello World");
    }
  }
}
