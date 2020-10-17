package au.com.origin.snapshots;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import au.com.origin.snapshots.config.TestSnapshotConfig;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SnapshotMatcherTest {

  private static final String FILE_PATH =
      "src/test/java/au/com/origin/snapshots//__snapshots__/SnapshotMatcherTest.snap";

  @BeforeAll
  static void beforeAll() {
    SnapshotMatcher.start(new TestSnapshotConfig());
  }

  @AfterAll
  static void afterAll() throws IOException {
    SnapshotMatcher.validateSnapshots();
    File f = new File(FILE_PATH);
    assertThat(StringUtils.join(Files.readAllLines(f.toPath()), "\n"))
        .isEqualTo(
            "au.com.origin.snapshots.SnapshotMatcherTest.should1ShowSnapshotSuccessfully=[\n"
                + "  \"any type of object\"\n"
                + "]\n\n\n"
                + "au.com.origin.snapshots.SnapshotMatcherTest.should2SecondSnapshotExecutionSuccessfully=[\n"
                + "  \"any second type of object\",\n"
                + "  \"any third type of object\"\n"
                + "]");
    Files.delete(Paths.get(FILE_PATH));
  }

  @Test
  void should1ShowSnapshotSuccessfully() throws IOException {
    File f = new File(FILE_PATH);
    if (!f.exists() || f.isDirectory()) {
      throw new RuntimeException("File should exist here");
    }
    SnapshotMatcher.expect("any type of object").toMatchSnapshot();
  }

  @Test
  void should2SecondSnapshotExecutionSuccessfully() throws IOException {

    File f = new File(FILE_PATH);
    if (!f.exists() || f.isDirectory()) {
      throw new RuntimeException("File should exist here");
    }
    SnapshotMatcher.expect("any second type of object", "any third type of object")
        .toMatchSnapshot();
  }
}
