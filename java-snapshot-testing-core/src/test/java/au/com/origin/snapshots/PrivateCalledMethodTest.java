package au.com.origin.snapshots;

import static au.com.origin.snapshots.SnapshotMatcher.expect;

import au.com.origin.snapshots.config.TestSnapshotConfig;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class PrivateCalledMethodTest {

  @BeforeAll
  static void beforeAll() {
    SnapshotMatcher.start(new TestSnapshotConfig());
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
