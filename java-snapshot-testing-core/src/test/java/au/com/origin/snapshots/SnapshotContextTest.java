package au.com.origin.snapshots;

import au.com.origin.snapshots.config.BaseSnapshotConfig;
import au.com.origin.snapshots.config.SnapshotConfig;
import au.com.origin.snapshots.exceptions.SnapshotMatchException;
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
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class SnapshotContextTest {

  private static final SnapshotConfig DEFAULT_CONFIG = new BaseSnapshotConfig();
  private static final String FILE_PATH = "src/test/java/anyFilePath";
  private static final String SNAPSHOT_NAME = "java.lang.String.toString";
  private static final String SNAPSHOT = "java.lang.String.toString=[\nanyObject\n]";

  private SnapshotFile snapshotFile;

  private SnapshotContext snapshotContext;

  @BeforeEach
  void setUp() throws NoSuchMethodException, IOException {
    snapshotFile = new SnapshotFile(DEFAULT_CONFIG.getOutputDir(), "anyFilePath", SnapshotContextTest.class);
    snapshotContext =
        new SnapshotContext(
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
    String snapshotName = snapshotContext.resolveSnapshotIdentifier();
    assertThat(snapshotName).isEqualTo(SNAPSHOT_NAME);
  }

  @Test
  void shouldMatchSnapshotSuccessfully() {
    snapshotContext.toMatchSnapshot();
    assertThat(snapshotFile.getSnapshots().stream().findFirst().get().raw())
        .isEqualTo(SNAPSHOT);
  }

  @Test
  void shouldMatchSnapshotWithException() {
    snapshotFile.push(Snapshot.parse(SNAPSHOT_NAME + "=anyWrongSnapshot"));

    assertThrows(SnapshotMatchException.class, snapshotContext::toMatchSnapshot);
  }

  @SneakyThrows
  @Test
  void shouldRenderScenarioNameWhenSupplied() {
    SnapshotContext snapshotContextWithScenario =
        new SnapshotContext(
            DEFAULT_CONFIG,
            snapshotFile,
            String.class,
            String.class.getDeclaredMethod("toString"),
            "anyObject");
    snapshotContextWithScenario.setScenario("hello world");
    assertThat(snapshotContextWithScenario.resolveSnapshotIdentifier())
        .isEqualTo("java.lang.String.toString[hello world]");
  }

  @SneakyThrows
  @Test
  void shouldNotRenderScenarioNameWhenNull() {
    SnapshotContext snapshotContextWithoutScenario =
        new SnapshotContext(
            DEFAULT_CONFIG,
            snapshotFile,
            String.class,
            String.class.getDeclaredMethod("toString"),
            "anyObject");
    assertThat(snapshotContextWithoutScenario.resolveSnapshotIdentifier()).isEqualTo("java.lang.String.toString");
  }

  @SneakyThrows
  @Test
  void shouldOverwriteSnapshotsWhenParamIsPassed() {
    SnapshotConfig mockConfig = Mockito.mock(SnapshotConfig.class);
    Mockito.when(mockConfig.updateSnapshot()).thenReturn(Optional.of(""));
    Mockito.when(mockConfig.getSerializer()).thenReturn(new ToStringSnapshotSerializer());
    SnapshotFile snapshotFile = Mockito.mock(SnapshotFile.class);
    Set<Snapshot> set = new HashSet<>();
    set.add(Snapshot.parse("java.lang.String.toString[hello world]=[{" + "\"a\": \"b\"" + "}]"));
    Mockito.when(snapshotFile.getSnapshots()).thenReturn(set);

    SnapshotContext snapshotContext =
        new SnapshotContext(
            mockConfig,
            snapshotFile,
            String.class,
            String.class.getDeclaredMethod("toString"),
            "anyObject");
    snapshotContext.setScenario("hello world");
    snapshotContext.toMatchSnapshot();
    Mockito.verify(snapshotFile)
        .push(Snapshot.parse("java.lang.String.toString[hello world]=[\nanyObject\n]"));
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

    SnapshotContext ciSnapshotContext = new SnapshotContext(
        ciSnapshotConfig,
        new SnapshotFile(ciSnapshotConfig.getOutputDir(), "blah", SnapshotContextTest.class),
        String.class,
        String.class.getDeclaredMethod("toString"),
        "anyObject");

    Assertions.assertThatThrownBy(ciSnapshotContext::toMatchSnapshot)
        .hasMessage("Snapshot [java.lang.String.toString] not found. Has this snapshot been committed ?");
  }

  @SneakyThrows
  @Test
  void shouldAggregateMultipleFailures() {
    SnapshotFile snapshotFile = Mockito.mock(SnapshotFile.class);
    Set<Snapshot> set = new HashSet<>();
    set.add(Snapshot.parse("java.lang.String.toString=[\n  \"hello\"\n]"));
    Mockito.when(snapshotFile.getSnapshots()).thenReturn(set);

    Stream<BiConsumer<Snapshot, Snapshot>> reportingFunctions = Stream.of(
        (rawSnapshot, currentObject) -> assertThat(currentObject).isEqualTo(rawSnapshot), // assertj
        org.junit.jupiter.api.Assertions::assertEquals, // junit jupiter
        (previous, current) -> {
          String message = String.join(System.lineSeparator(),
              "Expected : ", previous.raw(), "Actual : ", current.raw());
          throw new AssertionFailedError(message, previous, current); // opentest4j
        }
    );

    Stream<SnapshotReporter> reporters = reportingFunctions.map(consumer -> new SnapshotReporter() {
      @Override
      public boolean supportsFormat(String outputFormat) {
        return true;
      }

      @Override
      public void report(Snapshot previous, Snapshot current) {
        consumer.accept(previous, current);
      }
    });

    SnapshotContext failingSnapshotContext = new SnapshotContext(
        DEFAULT_CONFIG,
        snapshotFile,
        String.class,
        String.class.getDeclaredMethod("toString"),
        "hola"
    );
    failingSnapshotContext.setSnapshotSerializer(new ToStringSnapshotSerializer());
    failingSnapshotContext.setSnapshotReporters(reporters.collect(Collectors.toList()));

    try {
      failingSnapshotContext.toMatchSnapshot();
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
