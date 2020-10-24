package au.com.origin.snapshots;

import au.com.origin.snapshots.config.BaseSnapshotConfig;
import au.com.origin.snapshots.exceptions.SnapshotMatchException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static au.com.origin.snapshots.SnapshotMatcher.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class OrphanSnapshotTest {

    private static final SnapshotConfig DEFAULT_CONFIG = new BaseSnapshotConfig();

    @BeforeAll
    static void beforeAll() {
        SnapshotUtils.copyTestSnapshots();
    }

    @DisplayName("should fail the build when failOnOrphans=true")
    @Test
    void orphanSnapshotsShouldFailTheBuild() throws IOException {
        start(DEFAULT_CONFIG, true);
        FakeObject fakeObject1 = FakeObject.builder().id("anyId1").value(1).name("anyName1").build();
        final Path snapshotFile = Paths.get("src/test/java/au/com/origin/snapshots/__snapshots__/OrphanSnapshotTest.snap");

        long bytesBefore = Files.size(snapshotFile);

        expect(fakeObject1).toMatchSnapshot();

        Throwable exceptionThatWasThrown = assertThrows(SnapshotMatchException.class, () -> {
            validateSnapshots();
        });

        assertThat(exceptionThatWasThrown.getMessage()).isEqualTo("ERROR: Found orphan snapshots");

        // Ensure file has not changed
        long bytesAfter = Files.size(snapshotFile);
        assertThat(bytesAfter).isGreaterThan(bytesBefore);
    }

    @DisplayName("should not fail the build when failOnOrphans=false")
    @Test
    void orphanSnapshotsShouldNotFailTheBuild() throws IOException {
        start(DEFAULT_CONFIG, false);
        FakeObject fakeObject1 = FakeObject.builder().id("anyId1").value(1).name("anyName1").build();
        final Path snapshotFile = Paths.get("src/test/java/au/com/origin/snapshots/__snapshots__/OrphanSnapshotTest.snap");

        long bytesBefore = Files.size(snapshotFile);

        expect(fakeObject1).toMatchSnapshot();

        validateSnapshots();

        // Ensure file has not changed
        long bytesAfter = Files.size(snapshotFile);
        assertThat(bytesAfter).isGreaterThan(bytesBefore);
    }


}
