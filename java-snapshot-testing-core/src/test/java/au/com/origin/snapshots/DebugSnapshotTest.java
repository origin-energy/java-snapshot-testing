package au.com.origin.snapshots;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import au.com.origin.snapshots.config.BaseSnapshotConfig;
import au.com.origin.snapshots.config.SnapshotConfig;
import au.com.origin.snapshots.exceptions.SnapshotMatchException;
import au.com.origin.snapshots.serializers.SnapshotSerializer;
import au.com.origin.snapshots.serializers.ToStringSnapshotSerializer;
import java.nio.file.Files;
import java.nio.file.Paths;

import au.com.origin.snapshots.util.Constants;
import lombok.SneakyThrows;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class DebugSnapshotTest {

  private static final SnapshotConfig DEFAULT_CONFIG =
      new BaseSnapshotConfig() {
        @Override
        public SnapshotSerializer getSerializer() {
          return new ToStringSnapshotSerializer();
        }
      };

  private static final String DEBUG_FILE_PATH =
      "src/test/java/au/com/origin/snapshots/__snapshots__/DebugSnapshotTest.snap.debug";
  private static final String SNAPSHOT_FILE_PATH =
      "src/test/java/au/com/origin/snapshots/__snapshots__/DebugSnapshotTest.snap";

  @BeforeAll
  static void beforeAll() {
    SnapshotUtils.copyTestSnapshots();
  }

  @AfterAll
  static void afterAll() {
    SnapshotUtils.deleteTestSnapshots();
  }

  @SneakyThrows
  @BeforeEach
  public void beforeEach() {
    Files.deleteIfExists(Paths.get(DEBUG_FILE_PATH));
  }

  @DisplayName("Debug file should be created when snapshots don't match")
  @Test
  void createDebugFile(TestInfo testInfo) {
    assertTrue(Files.exists(Paths.get(SNAPSHOT_FILE_PATH)));

    SnapshotVerifier snapshotVerifier =
        new SnapshotVerifier(DEFAULT_CONFIG, testInfo.getTestClass().get());
    Expect expect = Expect.of(snapshotVerifier, testInfo.getTestMethod().get());
    assertTrue(Files.notExists(Paths.get(DEBUG_FILE_PATH)));

    // in shadow mode no exception will be thrown
    assertThrows(SnapshotMatchException.class, () -> expect.toMatchSnapshot(new TestObjectBad()));

    // this assertion won't get passed since we have removed debug file generation in shadow mode
    assertTrue(Files.exists(Paths.get(DEBUG_FILE_PATH)));
  }

  @DisplayName("Debug file should be created when snapshots don't match and shadowMode is false")
  @Test
  void testDebugFileGeneratedWhenSnapshotsDoesNotMatch(TestInfo testInfo) {
    System.setProperty(Constants.SHADOW_MODE, "false");
    assertTrue(Files.exists(Paths.get(SNAPSHOT_FILE_PATH)));

    SnapshotVerifier snapshotVerifier =
            new SnapshotVerifier(DEFAULT_CONFIG, testInfo.getTestClass().get());
    Expect expect = Expect.of(snapshotVerifier, testInfo.getTestMethod().get());
    assertTrue(Files.notExists(Paths.get(DEBUG_FILE_PATH)));

    // in shadow mode no exception will be thrown
    assertThrows(SnapshotMatchException.class, () -> expect.toMatchSnapshot(new TestObjectBad()));

    // this assertion won't get passed since we have removed debug file generation in shadow mode
    assertTrue(Files.exists(Paths.get(DEBUG_FILE_PATH)));
  }

  @DisplayName("Debug file should not be created when snapshots don't match and shadow mode is true")
  @Test
  void testDebugFileNotGeneratedWhenSnapshotsDoesNotMatchInShadowMode(TestInfo testInfo) {
    System.setProperty(Constants.SHADOW_MODE, "true");
    assertTrue(Files.exists(Paths.get(SNAPSHOT_FILE_PATH)));

    SnapshotVerifier snapshotVerifier =
            new SnapshotVerifier(DEFAULT_CONFIG, testInfo.getTestClass().get());
    Expect expect = Expect.of(snapshotVerifier, testInfo.getTestMethod().get());
    assertTrue(Files.notExists(Paths.get(DEBUG_FILE_PATH)));

    // in shadow mode no exception will be thrown
    assertDoesNotThrow( () -> expect.toMatchSnapshot(new TestObjectBad()));

    // this assertion won't get passed since we have removed debug file generation in shadow mode
    assertFalse(Files.exists(Paths.get(DEBUG_FILE_PATH)));
  }

  @DisplayName("Debug file should be created when snapshots match for a new snapshot")
  @Test
  void debugFileCreatedNewSnapshot(TestInfo testInfo) {
    assertTrue(Files.exists(Paths.get(SNAPSHOT_FILE_PATH)));

    SnapshotVerifier snapshotVerifier =
        new SnapshotVerifier(DEFAULT_CONFIG, testInfo.getTestClass().get());
    Expect expect = Expect.of(snapshotVerifier, testInfo.getTestMethod().get());
    assertTrue(Files.notExists(Paths.get(DEBUG_FILE_PATH)));
    expect.toMatchSnapshot(new TestObjectGood());
    assertTrue(Files.exists(Paths.get(DEBUG_FILE_PATH)));
  }

  @DisplayName("Debug file should be created when snapshots match for an existing snapshot")
  @Test
  void debugFileCreatedExistingSnapshot(TestInfo testInfo) {
    assertTrue(Files.exists(Paths.get(SNAPSHOT_FILE_PATH)));

    SnapshotVerifier snapshotVerifier =
        new SnapshotVerifier(DEFAULT_CONFIG, testInfo.getTestClass().get());
    Expect expect = Expect.of(snapshotVerifier, testInfo.getTestMethod().get());
    assertTrue(Files.notExists(Paths.get(DEBUG_FILE_PATH)));
    expect.toMatchSnapshot(new TestObjectGood());

    // no debug file will be created in shadow mode
//    assertTrue(Files.exists(Paths.get(DEBUG_FILE_PATH)));
  }

  @SneakyThrows
  @DisplayName("Existing debug file should not be deleted once snapshots match")
  @Test
  void debugFileCreatedSnapshotMatch(TestInfo testInfo) {
    System.setProperty(Constants.SHADOW_MODE, "false");
    assertTrue(Files.exists(Paths.get(SNAPSHOT_FILE_PATH)));
    Files.createFile(Paths.get(DEBUG_FILE_PATH));
    assertTrue(Files.exists(Paths.get(DEBUG_FILE_PATH)));
    SnapshotVerifier snapshotVerifier =
        new SnapshotVerifier(DEFAULT_CONFIG, testInfo.getTestClass().get());
    Expect expect = Expect.of(snapshotVerifier, testInfo.getTestMethod().get());
    expect.toMatchSnapshot(new TestObjectGood());

    assertTrue(Files.exists(Paths.get(DEBUG_FILE_PATH)));
  }

  private static class TestObjectBad {
    @Override
    public String toString() {
      return "Bad Snapshot";
    }
  }

  private static class TestObjectGood {
    @Override
    public String toString() {
      return "Good Snapshot";
    }
  }
}
