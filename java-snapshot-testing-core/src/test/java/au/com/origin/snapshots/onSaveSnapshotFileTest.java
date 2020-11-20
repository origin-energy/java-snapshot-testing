package au.com.origin.snapshots;

import au.com.origin.snapshots.config.BaseSnapshotConfig;
import au.com.origin.snapshots.config.ToStringSnapshotConfig;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Paths;

import static au.com.origin.snapshots.SnapshotMatcher.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class onSaveSnapshotFileTest {

    private final SnapshotConfig CUSTOM_SNAPSHOT_CONFIG = new BaseSnapshotConfig() {
        @Override
        public String onSaveSnapshotFile(Class<?> testClass, String snapshotContent) {
            return "HEADER\n"+snapshotContent+"\nFOOTER";
        }
    };

    private static final String SNAPSHOT_FILE_PATH = "src/test/java/au/com/origin/snapshots/__snapshots__/onSaveSnapshotFileTest.snap";

    @BeforeAll
    static void beforeAll() throws IOException {
        Files.deleteIfExists(Paths.get(SNAPSHOT_FILE_PATH));
    }

    @DisplayName("Should remove empty snapshots")
    @Test
    public void shouldAllowFileModificationsBeforeFinishingTest() throws IOException {
        assertFalse(Files.exists(Paths.get(SNAPSHOT_FILE_PATH)));

        start(CUSTOM_SNAPSHOT_CONFIG);
        expect("Hello World").string().toMatchSnapshot();
        validateSnapshots();

        File f = new File(SNAPSHOT_FILE_PATH);
        assertThat(StringUtils.join(Files.readAllLines(f.toPath()), "\n"))
                .isEqualTo(
                    "HEADER\n"
                    + "au.com.origin.snapshots.onSaveSnapshotFileTest.shouldAllowFileModificationsBeforeFinishingTest=[\n"
                    + "Hello World\n"
                    + "]\n"
                    + "FOOTER");
    }

}
