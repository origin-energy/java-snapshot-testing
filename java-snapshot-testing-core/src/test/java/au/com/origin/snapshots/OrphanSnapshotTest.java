package au.com.origin.snapshots;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import au.com.origin.snapshots.config.BaseSnapshotConfig;
import au.com.origin.snapshots.config.SnapshotConfig;
import au.com.origin.snapshots.exceptions.SnapshotMatchException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

public class OrphanSnapshotTest {

  private static final SnapshotConfig DEFAULT_CONFIG = new BaseSnapshotConfig();

  @BeforeAll
  static void beforeAll() {
    SnapshotUtils.copyTestSnapshots();
  }

  @DisplayName("should fail the build when failOnOrphans=true")
  @Test
  void orphanSnapshotsShouldFailTheBuild(TestInfo testInfo) throws IOException {
    SnapshotVerifier snapshotVerifier =
        new SnapshotVerifier(DEFAULT_CONFIG, testInfo.getTestClass().get(), true);
    FakeObject fakeObject1 = FakeObject.builder().id("anyId1").value(1).name("anyName1").build();
    final Path snapshotFile =
        Paths.get("src/test/java/au/com/origin/snapshots/__snapshots__/OrphanSnapshotTest.snap");

    long bytesBefore = Files.size(snapshotFile);

    Expect expect = Expect.of(snapshotVerifier, testInfo.getTestMethod().get());
    expect.toMatchSnapshot(fakeObject1);

    Throwable exceptionThatWasThrown =
        assertThrows(
            SnapshotMatchException.class,
            () -> {
              snapshotVerifier.validateSnapshots();
            });

    assertThat(exceptionThatWasThrown.getMessage()).isEqualTo("ERROR: Found orphan snapshots");

    // Ensure file has not changed
    long bytesAfter = Files.size(snapshotFile);
    assertThat(bytesAfter).isGreaterThan(bytesBefore);
  }

  @DisplayName("should not fail the build when failOnOrphans=false")
  @Test
  void orphanSnapshotsShouldNotFailTheBuild(TestInfo testInfo) throws IOException {
    SnapshotVerifier snapshotVerifier =
        new SnapshotVerifier(DEFAULT_CONFIG, testInfo.getTestClass().get(), false);
    FakeObject fakeObject1 = FakeObject.builder().id("anyId1").value(1).name("anyName1").build();
    final Path snapshotFile =
        Paths.get("src/test/java/au/com/origin/snapshots/__snapshots__/OrphanSnapshotTest.snap");

    long bytesBefore = Files.size(snapshotFile);

    Expect expect = Expect.of(snapshotVerifier, testInfo.getTestMethod().get());
    expect.toMatchSnapshot(fakeObject1);

    snapshotVerifier.validateSnapshots();

    // Ensure file has not changed
    long bytesAfter = Files.size(snapshotFile);
    assertThat(bytesAfter).isGreaterThan(bytesBefore);
  }
}
