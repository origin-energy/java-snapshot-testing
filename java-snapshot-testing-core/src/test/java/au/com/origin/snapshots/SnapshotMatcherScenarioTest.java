package au.com.origin.snapshots;

import au.com.origin.snapshots.config.BaseSnapshotConfig;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class SnapshotMatcherScenarioTest {

  private static final String FILE_PATH =
      "src/test/java/au/com/origin/snapshots/__snapshots__/SnapshotMatcherScenarioTest.snap";

  static SnapshotVerifier snapshotVerifier;

  @BeforeAll
  static void beforeAll() {
    snapshotVerifier = new SnapshotVerifier(new BaseSnapshotConfig(), SnapshotMatcherScenarioTest.class);
  }

  @AfterAll
  static void afterAll() throws IOException {
    snapshotVerifier.validateSnapshots();
    File f = new File(FILE_PATH);
    assertThat(String.join("\n", Files.readAllLines(f.toPath())))
        .isEqualTo(
            "au.com.origin.snapshots.SnapshotMatcherScenarioTest.should1ShowSnapshotSuccessfully[Scenario A]=[\n"
                + "any type of object\n"
                + "]\n\n\n"
                + "au.com.origin.snapshots.SnapshotMatcherScenarioTest.should2SecondSnapshotExecutionSuccessfully[Scenario B]=[\n"
                + "any second type of object\n"
                + "any third type of object\n"
                + "]");
    Files.delete(Paths.get(FILE_PATH));
  }

  @Test
  void should1ShowSnapshotSuccessfully(TestInfo testInfo) {
    Expect expect = Expect.of(snapshotVerifier, testInfo.getTestMethod().get());
    expect.scenario("Scenario A").toMatchSnapshot("any type of object");
    File f = new File(FILE_PATH);
    if (!f.exists() || f.isDirectory()) {
      throw new RuntimeException("File should exist here");
    }
  }

  @Test
  void should2SecondSnapshotExecutionSuccessfully(TestInfo testInfo) {
    Expect expect = Expect.of(snapshotVerifier, testInfo.getTestMethod().get());
    expect
        .scenario("Scenario B")
        .toMatchSnapshot("any second type of object", "any third type of object");
    File f = new File(FILE_PATH);
    if (!f.exists() || f.isDirectory()) {
      throw new RuntimeException("File should exist here");
    }
  }
}
