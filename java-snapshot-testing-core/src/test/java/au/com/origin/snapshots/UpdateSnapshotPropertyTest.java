package au.com.origin.snapshots;

import au.com.origin.snapshots.config.BaseSnapshotConfig;
import au.com.origin.snapshots.exceptions.SnapshotMatchException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class UpdateSnapshotPropertyTest {

  @AfterAll
  static void afterAll() {
    System.clearProperty(SnapshotConfig.JVM_UPDATE_SNAPSHOTS_PARAMETER);
  }

  @AfterEach
  public void afterEach() throws Exception {
    File file = new File("src/test/java/au/com/origin/snapshots/__snapshots__/UpdateSnapshotPropertyTest.snap");
    String content = "au.com.origin.snapshots.UpdateSnapshotPropertyTest.shouldNotUpdateSnapshot=[\n" +
        "FakeObject(id=ERROR, value=1, name=anyName1, fakeObject=null)\n" +
        "]\n" +
        "\n" +
        "\n" +
        "au.com.origin.snapshots.UpdateSnapshotPropertyTest.shouldUpdateSnapshot=[\n" +
        "FakeObject(id=ERROR, value=2, name=anyName2, fakeObject=null)\n" +
        "]";
    Files.write(file.toPath(), content.getBytes());
  }

  @Test
  void shouldUpdateSnapshot(TestInfo testInfo) throws IOException {
    SnapshotVerifier snapshotVerifier = new SnapshotVerifier(new BaseSnapshotConfig(), testInfo.getTestClass().get());
    System.setProperty(SnapshotConfig.JVM_UPDATE_SNAPSHOTS_PARAMETER, "");
    Expect expect = Expect.of(snapshotVerifier, testInfo.getTestMethod().get());
    expect.toMatchSnapshot(FakeObject.builder().id("anyId2").value(2).name("anyName2").build());
    snapshotVerifier.validateSnapshots();

    String content = new String(Files.readAllBytes(Paths.get("src/test/java/au/com/origin/snapshots/__snapshots__/UpdateSnapshotPropertyTest.snap")), StandardCharsets.UTF_8);
    Assertions.assertThat(content).isEqualTo(
        "au.com.origin.snapshots.UpdateSnapshotPropertyTest.shouldNotUpdateSnapshot=[\n" +
            "FakeObject(id=ERROR, value=1, name=anyName1, fakeObject=null)\n" +
            "]\n" +
            "\n" +
            "\n" +
            "au.com.origin.snapshots.UpdateSnapshotPropertyTest.shouldUpdateSnapshot=[\n" +
            "FakeObject(id=anyId2, value=2, name=anyName2, fakeObject=null)\n" +
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
  void shouldNotUpdateSnapshot(TestInfo testInfo) {
    SnapshotVerifier snapshotVerifier = new SnapshotVerifier(new BaseSnapshotConfig(), testInfo.getTestClass().get());
    Expect expect = Expect.of(snapshotVerifier, testInfo.getTestMethod().get());
    System.setProperty(SnapshotConfig.JVM_UPDATE_SNAPSHOTS_PARAMETER, "true");
    assertThrows(
        SnapshotMatchException.class,
        () -> expect.toMatchSnapshot(FakeObject.builder().id("anyId1").value(1).name("anyName1").build()),
        "Error on: \n"
            + "au.com.origin.snapshots.UpdateSnapshotPropertyTest.shouldNotUpdateSnapshot=[");
  }

}
