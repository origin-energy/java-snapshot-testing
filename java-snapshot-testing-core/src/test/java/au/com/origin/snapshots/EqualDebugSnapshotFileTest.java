package au.com.origin.snapshots;

import static org.junit.jupiter.api.Assertions.assertTrue;

import au.com.origin.snapshots.config.ToStringSnapshotConfig;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

public class EqualDebugSnapshotFileTest {

  private static final String SNAPSHOT_FILE_PATH =
      "src/test/java/au/com/origin/snapshots/__snapshots__/EqualDebugSnapshotFileTest.snap";
  private static final String DEBUG_FILE_PATH =
      "src/test/java/au/com/origin/snapshots/__snapshots__/EqualDebugSnapshotFileTest.snap.debug";

  @BeforeAll
  static void beforeAll() {
    SnapshotUtils.copyTestSnapshots();
  }

  @DisplayName("Should remove equal debug snapshots")
  @Test
  public void shouldRemoveEmptySnapshots(TestInfo testInfo) {
    assertTrue(Files.exists(Paths.get(SNAPSHOT_FILE_PATH)));
    assertTrue(Files.exists(Paths.get(DEBUG_FILE_PATH)));

    SnapshotVerifier snapshotVerifier =
        new SnapshotVerifier(new ToStringSnapshotConfig(), testInfo.getTestClass().get());
    snapshotVerifier.validateSnapshots();

    assertTrue(Files.exists(Paths.get(SNAPSHOT_FILE_PATH)));
    assertTrue(Files.notExists(Paths.get(DEBUG_FILE_PATH)));
  }
}
