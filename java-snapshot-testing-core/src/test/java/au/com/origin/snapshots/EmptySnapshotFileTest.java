package au.com.origin.snapshots;

import au.com.origin.snapshots.config.ToStringSnapshotConfig;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Paths;

import static au.com.origin.snapshots.SnapshotMatcher.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class EmptySnapshotFileTest {

    private static final String SNAPSHOT_FILE_PATH = "src/test/java/au/com/origin/snapshots/__snapshots__/EmptySnapshotFileTest.snap";
    private static final String DEBUG_FILE_PATH = "src/test/java/au/com/origin/snapshots/__snapshots__/EmptySnapshotFileTest.snap.debug";

    @BeforeAll
    static void beforeAll() {
        SnapshotUtils.copyTestSnapshots();
    }

    @DisplayName("Should remove empty snapshots")
    @Test
    public void shouldRemoveEmptySnapshots() {
        assertTrue(Files.exists(Paths.get(SNAPSHOT_FILE_PATH)));
        assertTrue(Files.exists(Paths.get(DEBUG_FILE_PATH)));

        start(new ToStringSnapshotConfig());
        validateSnapshots();

        assertTrue(Files.notExists(Paths.get(DEBUG_FILE_PATH)));
        assertTrue(Files.notExists(Paths.get(DEBUG_FILE_PATH)));
    }

    @Nested
    public class NestedClass {

        private static final String SNAPSHOT_FILE_PATH_NESTED = "src/test/java/au/com/origin/snapshots/__snapshots__/EmptySnapshotFileTest$NestedClass.snap";
        private static final String DEBUG_FILE_PATH_NESTED = "src/test/java/au/com/origin/snapshots/__snapshots__/EmptySnapshotFileTest$NestedClass.snap.debug";

        @DisplayName("Should remove empty nested snapshots")
        @Test
        public void shouldRemoveEmptyNestedSnapshots() {
            assertTrue(Files.exists(Paths.get(SNAPSHOT_FILE_PATH_NESTED)));
            assertTrue(Files.exists(Paths.get(DEBUG_FILE_PATH_NESTED)));

            start(new ToStringSnapshotConfig());
            validateSnapshots();

            assertTrue(Files.notExists(Paths.get(SNAPSHOT_FILE_PATH_NESTED)));
            assertTrue(Files.notExists(Paths.get(DEBUG_FILE_PATH_NESTED)));
        }

    }

}
