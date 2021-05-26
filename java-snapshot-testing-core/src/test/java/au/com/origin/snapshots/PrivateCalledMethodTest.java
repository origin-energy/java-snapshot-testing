package au.com.origin.snapshots;

import au.com.origin.snapshots.config.BaseSnapshotConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

class PrivateCalledMethodTest {

  @Test
  void testName(TestInfo testInfo) {
    SnapshotVerifier snapshotVerifier = new SnapshotVerifier(new BaseSnapshotConfig(), testInfo.getTestClass().get());
    testBasedOnArgs("testContent", testInfo);
    snapshotVerifier.validateSnapshots();
  }

  private void testBasedOnArgs(String arg, TestInfo testInfo) {
    SnapshotVerifier snapshotVerifier = new SnapshotVerifier(new BaseSnapshotConfig(), testInfo.getTestClass().get());
    Expect expect = Expect.of(snapshotVerifier, testInfo.getTestMethod().get());
    expect.toMatchSnapshot(arg);
    snapshotVerifier.validateSnapshots();
  }
}
