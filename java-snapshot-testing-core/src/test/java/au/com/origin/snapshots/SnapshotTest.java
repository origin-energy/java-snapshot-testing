package au.com.origin.snapshots;

import au.com.origin.snapshots.config.BaseSnapshotConfig;
import au.com.origin.snapshots.exceptions.SnapshotExtensionException;
import au.com.origin.snapshots.reporters.SnapshotReporter;
import au.com.origin.snapshots.serializers.ToStringSnapshotSerializer;
import lombok.SneakyThrows;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.opentest4j.AssertionFailedError;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class SnapshotTest {

  private static final SnapshotConfig DEFAULT_CONFIG = new BaseSnapshotConfig();
  private static final String FILE_PATH = "src/test/java/anyFilePath";
  private static final String SNAPSHOT_NAME = "java.lang.String.toString=";
  private static final String SNAPSHOT = "java.lang.String.toString=[\nanyObject\n]";

  private SnapshotFile snapshotFile;

  private Snapshot snapshot;

  @BeforeEach
  void setUp() throws NoSuchMethodException, IOException {
    snapshotFile = new SnapshotFile(DEFAULT_CONFIG.getOutputDir(), "anyFilePath", SnapshotTest.class, (a, b) -> b);
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
    Files.deleteIfExists(Paths.get(FILE_PATH));
  }

  @Test
  void shouldGetSnapshotNameSuccessfully() {
    String snapshotName = snapshot.getQualifiedName();
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

    assertThrows(SnapshotExtensionException.class, snapshot::toMatchSnapshot);
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
            new ToStringSnapshotSerializer(),
            "anyObject");
    snapshotWithScenario.setScenario("hello world");
    assertThat(snapshotWithScenario.getQualifiedName())
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
            new ToStringSnapshotSerializer(),
            "anyObject");
    assertThat(snapshotWithoutScenario.getQualifiedName()).isEqualTo("java.lang.String.toString=");
  }

  @SneakyThrows
  @Test
  void shouldOverwriteSnapshotsWhenParamIsPassed() {
    SnapshotConfig mockConfig = Mockito.mock(SnapshotConfig.class);
    Mockito.when(mockConfig.updateSnapshot()).thenReturn(Optional.of(""));
    Mockito.when(mockConfig.getSerializer()).thenReturn(new ToStringSnapshotSerializer());
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
            "anyObject");
    snapshot.setScenario("hello world");
    snapshot.toMatchSnapshot();
    Mockito.verify(snapshotFile)
        .push("java.lang.String.toString[hello world]=[\nanyObject\n]");
  }

  @SneakyThrows
  @Test
  void shouldFailWhenRunningOnCiWithoutExistingSnapshot() {
    BaseSnapshotConfig ciSnapshotConfig = new BaseSnapshotConfig() {
      @Override
      public boolean isCI() {
        return true;
      }
    };

    Snapshot ciSnapshot = new Snapshot(
        ciSnapshotConfig,
        new SnapshotFile(ciSnapshotConfig.getOutputDir(), "blah", SnapshotTest.class, (a, b) -> b),
        String.class,
        String.class.getDeclaredMethod("toString"),
        "anyObject");

    Assertions.assertThatThrownBy(ciSnapshot::toMatchSnapshot)
        .hasMessage("Snapshot [java.lang.String.toString=] not found. Has this snapshot been committed ?");
  }

  @SneakyThrows
  @Test
  void shouldAggregateMultipleFailures() {
    SnapshotFile snapshotFile = Mockito.mock(SnapshotFile.class);
    Set<String> set = new HashSet<>();
    set.add("java.lang.String.toString=[\n  \"hello\"\n]");
    Mockito.when(snapshotFile.getRawSnapshots()).thenReturn(set);

    Stream<BiConsumer<String, String>> reportingFunctions = Stream.of(
        (rawSnapshot, currentObject) -> assertThat(currentObject).isEqualTo(rawSnapshot), // assertj
        org.junit.jupiter.api.Assertions::assertEquals, // junit jupiter
        (rawSnapshot, currentObject) -> {
          String message = String.join(System.lineSeparator(),
              "Expected : ", rawSnapshot, "Actual : ", currentObject);
          throw new AssertionFailedError(message, rawSnapshot, currentObject); // opentest4j
        }
    );

    Stream<SnapshotReporter> reporters = reportingFunctions.map(consumer -> new SnapshotReporter() {
      @Override
      public boolean supportsFormat(String outputFormat) {
        return true;
      }

      @Override
      public void report(String snapshotName, String rawSnapshot, String currentObject) {
        consumer.accept(rawSnapshot, currentObject);
      }
    });

    Snapshot failingSnapshot = new Snapshot(
        DEFAULT_CONFIG,
        snapshotFile,
        String.class,
        String.class.getDeclaredMethod("toString"),
        "hola"
    );
    failingSnapshot.setSnapshotSerializer(new ToStringSnapshotSerializer());
    failingSnapshot.setSnapshotReporters(reporters.collect(Collectors.toList()));

    try {
      failingSnapshot.toMatchSnapshot();
    } catch (Throwable m) {
      String cleanMessage = m.getMessage()
          .replace("<\"", "")
          .replace("<", "")
          .replaceAll("\n", "")
          .replaceAll("\r", "")
          .replaceAll("\t", "")
          .replace("\">", " ")
          .replace(">", " ")
          .replace("]", "")
          .replace("java.lang.String.toString=[", "")
          .replaceAll(" +", " ");

      assertThat(cleanMessage).containsPattern("Expecting.*hola.*to be equal to.*hello.*but was not"); // assertj
      assertThat(cleanMessage).containsPattern("expected.*hello.*but was.*hola"); // junit jupiter
      assertThat(cleanMessage).containsPattern("Expected.*hello.*Actual.*hola"); // opentest4j

      return;
    }

    Assertions.fail("Expected an error to be thrown");
  }
}
