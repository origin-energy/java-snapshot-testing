package io.github.jsonSnapshot;

import static io.github.jsonSnapshot.SnapshotMatcher.start;
import static io.github.jsonSnapshot.SnapshotMatcher.validateSnapshots;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

public class SnapshotOverrideClassTest extends SnapshotSuperClassTest {

  @BeforeAll
  static void beforeAll() {
    start();
  }

  @AfterAll
  static void afterAll() {
    validateSnapshots();
  }

  @Override
  public String getName() {
    return "anyName";
  }
}
