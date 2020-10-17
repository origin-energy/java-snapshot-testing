package au.com.origin.snapshots;

import au.com.origin.snapshots.config.TestSnapshotConfig;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.nio.file.Files;

import static au.com.origin.snapshots.SnapshotMatcher.expect;
import static au.com.origin.snapshots.SnapshotMatcher.start;
import static au.com.origin.snapshots.SnapshotMatcher.validateSnapshots;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class UpdateSnapshotPropertyTest {

  @BeforeAll
  static void beforeAll() {
    start(new TestSnapshotConfig());
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
  void shouldUpdateSnapshot() {
    System.setProperty(SnapshotConfig.JVM_UPDATE_SNAPSHOTS_PARAMETER, "UpdateSnapshotPropertyTest");
    expect(FakeObject.builder().id("anyId1").value(1).name("anyName1").build()).toMatchSnapshot();
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
