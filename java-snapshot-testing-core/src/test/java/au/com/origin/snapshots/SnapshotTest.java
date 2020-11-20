package au.com.origin.snapshots;

import au.com.origin.snapshots.config.BaseSnapshotConfig;
import au.com.origin.snapshots.exceptions.SnapshotMatchException;
import au.com.origin.snapshots.serializers.JacksonSnapshotSerializer;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class SnapshotTest {

  private static final SnapshotConfig DEFAULT_CONFIG = new BaseSnapshotConfig();
  private static final String FILE_PATH = "src/test/java/anyFilePath";
  private static final String SNAPSHOT_NAME = "java.lang.String.toString=";
  private static final String SNAPSHOT = "java.lang.String.toString=[\n  \"anyObject\"\n]";

  private SnapshotFile snapshotFile;

  private Snapshot snapshot;

  @BeforeEach
  void setUp() throws NoSuchMethodException, IOException {
    snapshotFile = new SnapshotFile(DEFAULT_CONFIG.getOutputDir(), "anyFilePath", this.getClass(), (a, b) -> b);
    snapshot =
        new Snapshot(
            DEFAULT_CONFIG,
            snapshotFile,
            String.class,
            String.class.getDeclaredMethod("toString"),
            "anyObject");
  }

  @AfterEach
  void tearDown() throws IOException {
    Files.delete(Paths.get(FILE_PATH));
  }

  @Test
  void shouldGetSnapshotNameSuccessfully() {
    String snapshotName = snapshot.getSnapshotName();
    assertThat(snapshotName).isEqualTo(SNAPSHOT_NAME);
  }

  @Test
  void shouldMatchSnapshotSuccessfully() {
    snapshot.toMatchSnapshot();
    assertThat(snapshotFile.getRawSnapshots())
        .isEqualTo(Stream.of(SNAPSHOT).collect(Collectors.toCollection(TreeSet::new)));
  }

  @Test
  void shouldMatchSnapshotWithException() {
    snapshotFile.push(SNAPSHOT_NAME + "anyWrongSnapshot");

    assertThrows(SnapshotMatchException.class, snapshot::toMatchSnapshot);
  }

  @SneakyThrows
  @Test
  void shouldRenderScenarioNameWhenSupplied() {
    Snapshot snapshotWithScenario =
        new Snapshot(
            DEFAULT_CONFIG,
            snapshotFile,
            String.class,
            String.class.getDeclaredMethod("toString"),
                new JacksonSnapshotSerializer(),
            "anyObject").scenario("hello world");
    assertThat(snapshotWithScenario.getSnapshotName())
        .isEqualTo("java.lang.String.toString[hello world]=");
  }

  @SneakyThrows
  @Test
  void shouldNotRenderScenarioNameWhenNull() {
    Snapshot snapshotWithoutScenario =
        new Snapshot(
            DEFAULT_CONFIG,
            snapshotFile,
            String.class,
            String.class.getDeclaredMethod("toString"),
            null,
            new JacksonSnapshotSerializer(),
            "anyObject");
    assertThat(snapshotWithoutScenario.getSnapshotName()).isEqualTo("java.lang.String.toString=");
  }

  @SneakyThrows
  @Test
  void shouldOverwriteSnapshotsWhenParamIsPassed() {
    SnapshotConfig mockConfig = Mockito.mock(SnapshotConfig.class);
    Mockito.when(mockConfig.updateSnapshot()).thenReturn(Optional.of(""));
    Mockito.when(mockConfig.getSerializer()).thenReturn(new JacksonSnapshotSerializer());
    SnapshotFile snapshotFile = Mockito.mock(SnapshotFile.class);
    Set<String> set = new HashSet<>();
    set.add("java.lang.String.toString[hello world]=[{" + "\"a\": \"b\"" + "}]");
    Mockito.when(snapshotFile.getRawSnapshots()).thenReturn(set);

    Snapshot snapshot =
        new Snapshot(
            mockConfig,
            snapshotFile,
            String.class,
            String.class.getDeclaredMethod("toString"),
            "anyObject").scenario("hello world");
    snapshot.toMatchSnapshot();
    Mockito.verify(snapshotFile)
        .push("java.lang.String.toString[hello world]=[\n" + "  \"anyObject\"\n" + "]");
  }
}
