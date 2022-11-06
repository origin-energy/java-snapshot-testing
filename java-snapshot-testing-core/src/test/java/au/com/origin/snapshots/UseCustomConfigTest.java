package au.com.origin.snapshots;

import au.com.origin.snapshots.annotations.UseSnapshotConfig;
import au.com.origin.snapshots.config.BaseSnapshotConfig;
import au.com.origin.snapshots.config.SnapshotConfig;
import au.com.origin.snapshots.config.ToStringSnapshotConfig;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@UseSnapshotConfig(ToStringSnapshotConfig.class)
@ExtendWith(MockitoExtension.class)
public class UseCustomConfigTest {

  private static final SnapshotConfig DEFAULT_CONFIG = new BaseSnapshotConfig();

  @BeforeAll
  static void beforeAll() {
    SnapshotUtils.copyTestSnapshots();
  }

  @Test
  void canUseSnapshotConfigAnnotationAtClassLevel(TestInfo testInfo) {
    SnapshotVerifier snapshotVerifier = new SnapshotVerifier(DEFAULT_CONFIG, testInfo.getTestClass().get());
    Expect expect = Expect.of(snapshotVerifier, testInfo.getTestMethod().get());
    expect.toMatchSnapshot(new TestObject());
    snapshotVerifier.validateSnapshots();
  }


  private class TestObject {
    @Override
    public String toString() {
      return "This is a snapshot of the toString() method";
    }
  }
}
