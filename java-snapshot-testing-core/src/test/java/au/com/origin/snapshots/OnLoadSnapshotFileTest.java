package au.com.origin.snapshots;

import au.com.origin.snapshots.config.BaseSnapshotConfig;
import au.com.origin.snapshots.config.SnapshotConfig;
import au.com.origin.snapshots.serializers.ToStringSnapshotSerializer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class OnLoadSnapshotFileTest {

  private static final String SNAPSHOT_FILE_PATH = "src/test/java/au/com/origin/snapshots/__snapshots__/OnLoadSnapshotFileTest.snap";
  private final SnapshotConfig CUSTOM_SNAPSHOT_CONFIG = new BaseSnapshotConfig();

  @BeforeAll
  static void beforeAll() throws IOException {
    Files.deleteIfExists(Paths.get(SNAPSHOT_FILE_PATH));
    String snapshotFileContent = "au.com.origin.snapshots.OnLoadSnapshotFileTest.shouldLoadFileWithCorrectEncodingForCompare=[\n"
        + "any special characters that need correct encoding äöüèéàè\n"
        + "]";
   createSnapshotFile(snapshotFileContent);
  }

  @DisplayName("Should load snapshots with correct encoding")
  @Test
  public void shouldLoadFileWithCorrectEncodingForCompare(TestInfo testInfo) throws IOException {
    assertTrue(Files.exists(Paths.get(SNAPSHOT_FILE_PATH)));

    SnapshotVerifier snapshotVerifier = new SnapshotVerifier(CUSTOM_SNAPSHOT_CONFIG, testInfo.getTestClass().get());
    Expect expect = Expect.of(snapshotVerifier, testInfo.getTestMethod().get());
    expect.serializer(ToStringSnapshotSerializer.class).toMatchSnapshot("any special characters that need correct encoding äöüèéàè");
    snapshotVerifier.validateSnapshots();

    File f = new File(SNAPSHOT_FILE_PATH);
    assertThat(String.join("\n", Files.readAllLines(f.toPath())))
        .isEqualTo(
            "au.com.origin.snapshots.OnLoadSnapshotFileTest.shouldLoadFileWithCorrectEncodingForCompare=[\n"
                + "any special characters that need correct encoding äöüèéàè\n"
                + "]");
  }

  private static void createSnapshotFile(String snapshot) {
    try {
      File file = new File(SNAPSHOT_FILE_PATH);
      file.getParentFile().mkdirs();
      file.createNewFile();
      try (FileOutputStream fileStream = new FileOutputStream(file, false)) {
        fileStream.write(snapshot.getBytes(StandardCharsets.UTF_8));
      } catch (IOException e) {
        e.printStackTrace();
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

}
