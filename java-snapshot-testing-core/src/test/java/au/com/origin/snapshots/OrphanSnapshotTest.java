package au.com.origin.snapshots;

import au.com.origin.snapshots.config.TestSnapshotConfig;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static au.com.origin.snapshots.SnapshotMatcher.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class OrphanSnapshotTest {

    private static final SnapshotConfig DEFAULT_CONFIG = new TestSnapshotConfig();

    @BeforeAll
    static void beforeAll() {
        SnapshotUtils.copyTestSnapshots();
        start(DEFAULT_CONFIG);
    }

    @DisplayName("should fail the build when an orphan snapshot exists in the `.snap` file")
    @Test
    void orphanSnapshotsShouldFailTheBuild() throws IOException {
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
}
