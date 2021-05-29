package au.com.origin.snapshots;

import au.com.origin.snapshots.annotations.SnapshotName;
import au.com.origin.snapshots.config.BaseSnapshotConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

public class SnapshotNameAnnotationTest {

  @BeforeEach
  void beforeEach() {
    SnapshotUtils.copyTestSnapshots();
  }

  @SnapshotName("can_use_snapshot_name")
  @Test
  void canUseSnapshotNameAnnotation(TestInfo testInfo) {
    SnapshotVerifier snapshotVerifier = new SnapshotVerifier(new BaseSnapshotConfig(), testInfo.getTestClass().get());
    Expect expect = Expect.of(snapshotVerifier, testInfo.getTestMethod().get());
    expect.toMatchSnapshot("Hello World");
    snapshotVerifier.validateSnapshots();
  }

  @SnapshotName("can use snapshot name with spaces")
  @Test
  void canUseSnapshotNameAnnotationWithSpaces(TestInfo testInfo) {
    SnapshotVerifier snapshotVerifier = new SnapshotVerifier(new BaseSnapshotConfig(), testInfo.getTestClass().get());
    Expect expect = Expect.of(snapshotVerifier, testInfo.getTestMethod().get());
    expect.toMatchSnapshot("Hello World");
    snapshotVerifier.validateSnapshots();
  }

}
