package au.com.origin.snapshots;

import au.com.origin.snapshots.config.BaseSnapshotConfig;
import au.com.origin.snapshots.config.SnapshotConfig;
import au.com.origin.snapshots.serializers.ToStringSnapshotSerializer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class OnSaveSnapshotFileTest {

  private static final String SNAPSHOT_FILE_PATH = "src/test/java/au/com/origin/snapshots/__snapshots__/OnSaveSnapshotFileTest.snap";
  private final SnapshotConfig CUSTOM_SNAPSHOT_CONFIG = new BaseSnapshotConfig() {
    @Override
    public String onSaveSnapshotFile(Class<?> testClass, String snapshotContent) {
      return "HEADER\n" + snapshotContent + "\nFOOTER";
    }
  };

  @BeforeAll
  static void beforeAll() throws IOException {
    Files.deleteIfExists(Paths.get(SNAPSHOT_FILE_PATH));
  }

  @DisplayName("Should remove empty snapshots")
  @Test
  public void shouldAllowFileModificationsBeforeFinishingTest(TestInfo testInfo) throws IOException {
    assertFalse(Files.exists(Paths.get(SNAPSHOT_FILE_PATH)));

    SnapshotVerifier snapshotVerifier = new SnapshotVerifier(CUSTOM_SNAPSHOT_CONFIG, testInfo.getTestClass().get());
    Expect expect = Expect.of(snapshotVerifier, testInfo.getTestMethod().get());
    expect.serializer(ToStringSnapshotSerializer.class).toMatchSnapshot("Hello Wörld");
    snapshotVerifier.validateSnapshots();

    File f = new File(SNAPSHOT_FILE_PATH);
    assertThat(String.join("\n", Files.readAllLines(f.toPath())))
        .isEqualTo(
            "HEADER\n"
                + "au.com.origin.snapshots.OnSaveSnapshotFileTest.shouldAllowFileModificationsBeforeFinishingTest=[\n"
                + "Hello Wörld\n"
                + "]\n"
                + "FOOTER");
  }

}
