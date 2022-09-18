package au.com.origin.snapshots;

import au.com.origin.snapshots.config.BaseSnapshotConfig;
import au.com.origin.snapshots.exceptions.SnapshotMatchException;
import au.com.origin.snapshots.serializers.SnapshotSerializer;
import au.com.origin.snapshots.serializers.ToStringSnapshotSerializer;
import lombok.SneakyThrows;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class DebugSnapshotTest {

  private static final SnapshotConfig DEFAULT_CONFIG = new BaseSnapshotConfig() {
    @Override
    public SnapshotSerializer getSerializer() {
      return new ToStringSnapshotSerializer();
    }
  };

  private static final String DEBUG_FILE_PATH = "src/test/java/au/com/origin/snapshots/__snapshots__/DebugSnapshotTest.snap.debug";
  private static final String SNAPSHOT_FILE_PATH = "src/test/java/au/com/origin/snapshots/__snapshots__/DebugSnapshotTest.snap";

  @BeforeAll
  static void beforeAll() {
    SnapshotUtils.copyTestSnapshots();
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

    SnapshotVerifier snapshotVerifier = new SnapshotVerifier(DEFAULT_CONFIG, testInfo.getTestClass().get());
    Expect expect = Expect.of(snapshotVerifier, testInfo.getTestMethod().get());
    assertTrue(Files.notExists(Paths.get(DEBUG_FILE_PATH)));
    assertThrows(SnapshotMatchException.class, () -> expect.toMatchSnapshot(new TestObjectBad()));
    assertTrue(Files.exists(Paths.get(DEBUG_FILE_PATH)));
  }

  @DisplayName("Debug file should be created when snapshots match for a new snapshot")
  @Test
  void debugFileCreatedNewSnapshot(TestInfo testInfo) {
    assertTrue(Files.exists(Paths.get(SNAPSHOT_FILE_PATH)));

    SnapshotVerifier snapshotVerifier = new SnapshotVerifier(DEFAULT_CONFIG, testInfo.getTestClass().get());
    Expect expect = Expect.of(snapshotVerifier, testInfo.getTestMethod().get());
    assertTrue(Files.notExists(Paths.get(DEBUG_FILE_PATH)));
    expect.toMatchSnapshot(new TestObjectGood());
    assertTrue(Files.exists(Paths.get(DEBUG_FILE_PATH)));
  }

  @DisplayName("Debug file should be created when snapshots match for an existing snapshot")
  @Test
  void debugFileCreatedExistingSnapshot(TestInfo testInfo) {
    assertTrue(Files.exists(Paths.get(SNAPSHOT_FILE_PATH)));

    SnapshotVerifier snapshotVerifier = new SnapshotVerifier(DEFAULT_CONFIG, testInfo.getTestClass().get());
    Expect expect = Expect.of(snapshotVerifier, testInfo.getTestMethod().get());
    assertTrue(Files.notExists(Paths.get(DEBUG_FILE_PATH)));
    expect.toMatchSnapshot(new TestObjectGood());
    assertTrue(Files.exists(Paths.get(DEBUG_FILE_PATH)));
  }

  @SneakyThrows
  @DisplayName("Existing debug file should not be deleted once snapshots match")
  @Test
  void debugFileCreatedSnapshotMatch(TestInfo testInfo) {
    assertTrue(Files.exists(Paths.get(SNAPSHOT_FILE_PATH)));
    Files.createFile(Paths.get(DEBUG_FILE_PATH));
    assertTrue(Files.exists(Paths.get(DEBUG_FILE_PATH)));
    SnapshotVerifier snapshotVerifier = new SnapshotVerifier(DEFAULT_CONFIG, testInfo.getTestClass().get());
    Expect expect = Expect.of(snapshotVerifier, testInfo.getTestMethod().get());
    expect.toMatchSnapshot(new TestObjectGood());
    assertTrue(Files.exists(Paths.get(DEBUG_FILE_PATH)));
  }

  private class TestObjectBad {
    @Override
    public String toString() {
      return "Bad Snapshot";
    }
  }

  private class TestObjectGood {
    @Override
    public String toString() {
      return "Good Snapshot";
    }
  }
}
