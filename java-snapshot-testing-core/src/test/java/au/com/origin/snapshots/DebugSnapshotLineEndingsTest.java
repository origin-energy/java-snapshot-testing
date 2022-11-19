package au.com.origin.snapshots;

import static org.junit.jupiter.api.Assertions.assertTrue;

import au.com.origin.snapshots.config.BaseSnapshotConfig;
import au.com.origin.snapshots.config.SnapshotConfig;
import au.com.origin.snapshots.serializers.SerializerType;
import au.com.origin.snapshots.serializers.SnapshotSerializer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

class DebugSnapshotLineEndingsTest {
  private static final SnapshotConfig CONFIG =
      new BaseSnapshotConfig() {
        @Override
        public SnapshotSerializer getSerializer() {
          return new MultiLineSnapshotSerializer();
        }
      };
  private static final String DEBUG_FILE_PATH =
      "src/test/java/au/com/origin/snapshots/__snapshots__/DebugSnapshotLineEndingsTest.snap.debug";
  private static final String SNAPSHOT_FILE_PATH =
      "src/test/java/au/com/origin/snapshots/__snapshots__/DebugSnapshotLineEndingsTest.snap";

  @BeforeAll
  static void beforeAll() {
    SnapshotUtils.copyTestSnapshots();
  }

  @SneakyThrows
  @BeforeEach
  void beforeEach() {
    Files.deleteIfExists(Paths.get(DEBUG_FILE_PATH));
  }

  /**
   * Scenario: - An existing snapshot file checked out from git will have CR LF line endings on
   * Windows OS - A newly created snapshot file will have LF line endings on any OS (see
   * MultiLineSnapshotSerializer) Expectation: - As snapshot file content is identical (except for
   * line endings), the debug file should not be created
   */
  @DisplayName(
      "Debug file should not be created when snapshots match the existing snapshot regardless of line endings")
  @Test
  void existingSnapshotDifferentLineEndings(TestInfo testInfo) {
    assertTrue(Files.exists(Paths.get(SNAPSHOT_FILE_PATH)));
    assertTrue(Files.notExists(Paths.get(DEBUG_FILE_PATH)));

    SnapshotVerifier snapshotVerifier = new SnapshotVerifier(CONFIG, testInfo.getTestClass().get());
    Expect expect = Expect.of(snapshotVerifier, testInfo.getTestMethod().get());
    expect.toMatchSnapshot(Arrays.asList("a", "b"));
    snapshotVerifier.validateSnapshots();

    assertTrue(Files.notExists(Paths.get(DEBUG_FILE_PATH)), "Debug file should not be created");
  }

  private static class MultiLineSnapshotSerializer implements SnapshotSerializer {
    @Override
    public String getOutputFormat() {
      return SerializerType.TEXT.name();
    }

    @Override
    public Snapshot apply(Object object, SnapshotSerializerContext snapshotSerializerContext) {
      Object body =
          "[\n"
              + Arrays.asList(object).stream()
                  .flatMap(
                      o -> {
                        if (o instanceof Collection) {
                          return ((Collection<?>) o).stream();
                        }
                        return Stream.of(o);
                      })
                  .map(Object::toString)
                  .collect(Collectors.joining("\n"))
              + "\n]";

      return Snapshot.builder()
          .name(
              "au.com.origin.snapshots.DebugSnapshotLineEndingsTest.existingSnapshotDifferentLineEndings")
          .body(body.toString())
          .build();
    }
  }
}
