package au.com.origin.snapshots;

import au.com.origin.snapshots.config.BaseSnapshotConfig;
import au.com.origin.snapshots.exceptions.SnapshotMatchException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static au.com.origin.snapshots.SnapshotMatcher.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class UpdateSnapshotPropertyTest {

  @BeforeAll
  static void beforeAll() {
    start(new BaseSnapshotConfig());
  }

  @AfterAll
  static void afterAll() {
    validateSnapshots();
    System.clearProperty(SnapshotConfig.JVM_UPDATE_SNAPSHOTS_PARAMETER);
  }

  @AfterEach
  public void afterEach() throws Exception {
    File file = new File("src/test/java/au/com/origin/snapshots/__snapshots__/UpdateSnapshotPropertyTest.snap");
    String content = "au.com.origin.snapshots.UpdateSnapshotPropertyTest.shouldNotUpdateSnapshot=[\n" +
            "  {\n" +
            "    \"id\": \"ERROR\",\n" +
            "    \"value\": 1,\n" +
            "    \"name\": \"anyName1\"\n" +
            "  }\n" +
            "]\n" +
            "\n" +
            "\n" +
            "au.com.origin.snapshots.UpdateSnapshotPropertyTest.shouldUpdateSnapshot=[\n" +
            "  {\n" +
            "    \"id\": \"ERROR\",\n" +
            "    \"value\": 1,\n" +
            "    \"name\": \"anyName1\"\n" +
            "  }\n" +
            "]";
    Files.write(file.toPath(), content.getBytes());
  }

  @Test
  void shouldUpdateSnapshot() throws IOException {
    System.setProperty(SnapshotConfig.JVM_UPDATE_SNAPSHOTS_PARAMETER, "");
    expect(FakeObject.builder().id("anyId2").value(2).name("anyName2").build()).toMatchSnapshot();
    validateSnapshots();
    String content = new String(Files.readAllBytes(Paths.get("src/test/java/au/com/origin/snapshots/__snapshots__/UpdateSnapshotPropertyTest.snap")));
    Assertions.assertThat(content).isEqualTo(
            "au.com.origin.snapshots.UpdateSnapshotPropertyTest.shouldNotUpdateSnapshot=[\n" +
            "  {\n" +
            "    \"id\": \"ERROR\",\n" +
            "    \"value\": 1,\n" +
            "    \"name\": \"anyName1\"\n" +
            "  }\n" +
            "]\n" +
            "\n" +
            "\n" +
            "au.com.origin.snapshots.UpdateSnapshotPropertyTest.shouldUpdateSnapshot=[\n" +
            "  {\n" +
            "    \"id\": \"anyId2\",\n" +
            "    \"value\": 2,\n" +
            "    \"name\": \"anyName2\"\n" +
            "  }\n" +
            "]"
    );
  }

  @Disabled
  @Test
  void shouldUpdateAllSnapshots() throws IOException {
    System.setProperty(SnapshotConfig.JVM_UPDATE_SNAPSHOTS_PARAMETER, "");
    // FIXME
  }

  @Test
  void shouldNotUpdateSnapshot() {
    System.setProperty(SnapshotConfig.JVM_UPDATE_SNAPSHOTS_PARAMETER, "ClassNotFound");
    assertThrows(
            SnapshotMatchException.class,
            expect(FakeObject.builder().id("anyId1").value(1).name("anyName1").build())::toMatchSnapshot,
            "Error on: \n"
                    + "au.com.origin.snapshots.UpdateSnapshotPropertyTest.shouldNotUpdateSnapshot=[");
  }

}
