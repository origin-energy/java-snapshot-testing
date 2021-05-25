package au.com.origin.snapshots;

import au.com.origin.snapshots.config.BaseSnapshotConfig;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

public class SnapshotOverrideClassTest extends SnapshotSuperClassTest {

  @BeforeEach
  void beforeEach() {
    snapshotVerifier = new SnapshotVerifier(new BaseSnapshotConfig(), SnapshotOverrideClassTest.class);
  }

  @AfterEach
  void afterEach() {
    snapshotVerifier.validateSnapshots();
  }

  @Override
  public String getName() {
    return "anyName";
  }
}
