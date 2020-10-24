package au.com.origin.snapshots;

import au.com.origin.snapshots.config.BaseSnapshotConfig;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static au.com.origin.snapshots.SnapshotMatcher.expect;

class PrivateCalledMethodTest {

  @BeforeAll
  static void beforeAll() {
    SnapshotMatcher.start(new BaseSnapshotConfig());
  }

  @AfterAll
  static void afterAll() {
    SnapshotMatcher.validateSnapshots();
  }

  @Test
  void testName() {
    testBasedOnArgs("testContent");
  }

  private void testBasedOnArgs(String arg) {
    expect(arg).toMatchSnapshot();
  }
}
