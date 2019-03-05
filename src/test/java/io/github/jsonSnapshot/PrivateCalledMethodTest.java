package io.github.jsonSnapshot;

import static io.github.jsonSnapshot.SnapshotMatcher.expect;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class PrivateCalledMethodTest {

  @BeforeAll
  static void beforeAll() {
    SnapshotMatcher.start();
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
