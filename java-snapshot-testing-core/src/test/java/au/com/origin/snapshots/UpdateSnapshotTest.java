package au.com.origin.snapshots;

import static org.junit.jupiter.api.Assertions.assertThrows;

import au.com.origin.snapshots.config.BaseSnapshotConfig;
import au.com.origin.snapshots.config.SnapshotConfig;
import au.com.origin.snapshots.exceptions.SnapshotMatchException;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class UpdateSnapshotTest {

  @BeforeEach
  public void beforeEach() throws Exception {
    File file =
        new File("src/test/java/au/com/origin/snapshots/__snapshots__/UpdateSnapshotTest.snap");
    String content =
        "au.com.origin.snapshots.UpdateSnapshotTest.canUpdateAllSnapshots=[\n"
            + "OLD\n"
            + "]\n"
            + "\n"
            + "\n"
            + "au.com.origin.snapshots.UpdateSnapshotTest.canUpdateClassNameSnapshots=[\n"
            + "OLD\n"
            + "]\n"
            + "\n"
            + "\n"
            + "au.com.origin.snapshots.UpdateSnapshotTest.canUpdateNoSnapshots=[\n"
            + "OLD\n"
            + "]";
    Path parentDir = file.getParentFile().toPath();
    if (!Files.exists(parentDir)) {
      Files.createDirectories(parentDir);
    }
    Files.write(file.toPath(), content.getBytes(StandardCharsets.UTF_8));
  }

  @Test
  void canUpdateAllSnapshots(TestInfo testInfo) throws IOException {
    SnapshotConfig config =
        new BaseSnapshotConfig() {
          @Override
          public Optional<String> updateSnapshot() {
            return Optional.of("");
          }
        };
    SnapshotVerifier snapshotVerifier =
        new SnapshotVerifier(config, testInfo.getTestClass().get(), false);
    Expect expect = Expect.of(snapshotVerifier, testInfo.getTestMethod().get());
    expect.toMatchSnapshot("NEW");
    snapshotVerifier.validateSnapshots();

    String content =
        new String(
            Files.readAllBytes(
                Paths.get(
                    "src/test/java/au/com/origin/snapshots/__snapshots__/UpdateSnapshotTest.snap")),
            StandardCharsets.UTF_8);
    Assertions.assertThat(content)
        .isEqualTo(
            "au.com.origin.snapshots.UpdateSnapshotTest.canUpdateAllSnapshots=[\n"
                + "NEW\n"
                + "]\n"
                + "\n"
                + "\n"
                + "au.com.origin.snapshots.UpdateSnapshotTest.canUpdateClassNameSnapshots=[\n"
                + "OLD\n"
                + "]\n"
                + "\n"
                + "\n"
                + "au.com.origin.snapshots.UpdateSnapshotTest.canUpdateNoSnapshots=[\n"
                + "OLD\n"
                + "]");
  }

  @Test
  void canUpdateNoSnapshots(TestInfo testInfo) {
    SnapshotConfig config =
        new BaseSnapshotConfig() {
          @Override
          public Optional<String> updateSnapshot() {
            return Optional.empty();
          }
        };
    SnapshotVerifier snapshotVerifier =
        new SnapshotVerifier(config, testInfo.getTestClass().get(), false);
    Expect expect = Expect.of(snapshotVerifier, testInfo.getTestMethod().get());
    assertThrows(SnapshotMatchException.class, () -> expect.toMatchSnapshot("FOOBAR"));
  }

  @Test
  public void canUpdateNewSnapshots() {
    SnapshotConfig config =
        new BaseSnapshotConfig() {
          @Override
          public Optional<String> updateSnapshot() {
            return Optional.of("new");
          }
        };

    // TODO Pending Implementation
  }

  @Test
  public void canUpdateClassNameSnapshots(TestInfo testInfo) throws IOException {
    SnapshotConfig config =
        new BaseSnapshotConfig() {
          @Override
          public Optional<String> updateSnapshot() {
            return Optional.of("UpdateSnapshotTest");
          }
        };
    SnapshotVerifier snapshotVerifier =
        new SnapshotVerifier(config, testInfo.getTestClass().get(), false);
    Expect expect = Expect.of(snapshotVerifier, testInfo.getTestMethod().get());
    expect.toMatchSnapshot("NEW");
    snapshotVerifier.validateSnapshots();

    String content =
        new String(
            Files.readAllBytes(
                Paths.get(
                    "src/test/java/au/com/origin/snapshots/__snapshots__/UpdateSnapshotTest.snap")),
            StandardCharsets.UTF_8);
    Assertions.assertThat(content)
        .isEqualTo(
            "au.com.origin.snapshots.UpdateSnapshotTest.canUpdateAllSnapshots=[\n"
                + "OLD\n"
                + "]\n"
                + "\n"
                + "\n"
                + "au.com.origin.snapshots.UpdateSnapshotTest.canUpdateClassNameSnapshots=[\n"
                + "NEW\n"
                + "]\n"
                + "\n"
                + "\n"
                + "au.com.origin.snapshots.UpdateSnapshotTest.canUpdateNoSnapshots=[\n"
                + "OLD\n"
                + "]");
  }
}
