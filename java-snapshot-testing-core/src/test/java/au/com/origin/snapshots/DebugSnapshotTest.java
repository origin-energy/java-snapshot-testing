package au.com.origin.snapshots;

import au.com.origin.snapshots.config.BaseSnapshotConfig;
import au.com.origin.snapshots.exceptions.SnapshotMatchException;
import au.com.origin.snapshots.serializers.SnapshotSerializer;
import au.com.origin.snapshots.serializers.ToStringSerializer;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.file.Files;
import java.nio.file.Paths;

import static au.com.origin.snapshots.SnapshotMatcher.expect;
import static au.com.origin.snapshots.SnapshotMatcher.start;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class DebugSnapshotTest {

    private static final SnapshotConfig DEFAULT_CONFIG = new BaseSnapshotConfig() {
        @Override
        public SnapshotSerializer getSerializer() {
            return new ToStringSerializer();
        }
    };

    private static final String DEBUG_FILE_PATH = "src/test/java/au/com/origin/snapshots/__snapshots__/DebugSnapshotTest.snap.debug";
    private static final String SNAPSHOT_FILE_PATH = "src/test/java/au/com/origin/snapshots/__snapshots__/DebugSnapshotTest.snap";

    @BeforeAll
    static void beforeAll() {
        SnapshotUtils.copyTestSnapshots();
    }

    @SneakyThrows
    @BeforeEach
    public void beforeEach() {
        Files.deleteIfExists(Paths.get(DEBUG_FILE_PATH));
    }


    @DisplayName("Debug file should be created when snapshots don't match")
    @Test
    void createDebugFile() {
        assertTrue(Files.exists(Paths.get(SNAPSHOT_FILE_PATH)));

        start(DEFAULT_CONFIG);
        assertTrue(Files.notExists(Paths.get(DEBUG_FILE_PATH)));
        assertThrows(SnapshotMatchException.class, () -> expect(new TestObjectBad()).toMatchSnapshot());
        assertTrue(Files.exists(Paths.get(DEBUG_FILE_PATH)));
    }

    @DisplayName("Debug file should not be created when snapshots match for a new snapshot")
    @Test
    void noDebugFileCreatedNewSnapshot() {
        assertTrue(Files.exists(Paths.get(SNAPSHOT_FILE_PATH)));

        start(DEFAULT_CONFIG);
        assertTrue(Files.notExists(Paths.get(DEBUG_FILE_PATH)));
        expect(new TestObjectGood()).toMatchSnapshot();
        assertTrue(Files.notExists(Paths.get(DEBUG_FILE_PATH)));
    }

    @DisplayName("Debug file should not be created when snapshots match for an existing snapshot")
    @Test
    void noDebugFileCreatedExistingSnapshot() {
        assertTrue(Files.exists(Paths.get(SNAPSHOT_FILE_PATH)));

        start(DEFAULT_CONFIG);
        assertTrue(Files.notExists(Paths.get(DEBUG_FILE_PATH)));
        expect(new TestObjectGood()).toMatchSnapshot();
        assertTrue(Files.notExists(Paths.get(DEBUG_FILE_PATH)));
    }

    @SneakyThrows
    @DisplayName("existing debug file should be deleted once snapshots match")
    @Test
    void deleteDebugFile() {
        assertTrue(Files.exists(Paths.get(SNAPSHOT_FILE_PATH)));
        Files.createFile(Paths.get(DEBUG_FILE_PATH));
        assertTrue(Files.exists(Paths.get(DEBUG_FILE_PATH)));
        start(DEFAULT_CONFIG);
        expect(new TestObjectGood()).toMatchSnapshot();
        assertTrue(Files.notExists(Paths.get(DEBUG_FILE_PATH)));
    }

    private class TestObjectBad {
        @Override
        public String toString() {
            return "Bad Snapshot";
        }
    }

    private class TestObjectGood {
        @Override
        public String toString() {
            return "Good Snapshot";
        }
    }
}
