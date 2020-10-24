package au.com.origin.snapshots;

import au.com.origin.snapshots.config.BaseSnapshotConfig;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static au.com.origin.snapshots.SnapshotMatcher.expect;

public class CustomFolderSnapshotTest {

    private static final String OUTPUT_FILE = "src/test/some-folder/au/com/origin/snapshots/__snapshots__/CustomFolderSnapshotTest.snap";

    @BeforeEach
    public void beforeEach() throws IOException {
        Path path = Paths.get(OUTPUT_FILE);
        Files.deleteIfExists(path);
    }

    @Test
    void shouldBeAbleToChangeSnapshotFolder() {
        SnapshotMatcher.start(new CustomFolderSnapshotConfig());
        expect(FakeObject.builder().id("shouldBeAbleToChangeSnapshotFolder").build()).toMatchSnapshot();
        SnapshotMatcher.validateSnapshots();

        Path path = Paths.get(OUTPUT_FILE);
        Assertions.assertThat(Files.exists(path));
    }

    @Test
    void shouldBeAbleToChangeSnapshotFolderLegacyTrailginSlash() {
        SnapshotMatcher.start(new CustomFolderSnapshotConfigLegacy());
        expect(FakeObject.builder().id("shouldBeAbleToChangeSnapshotFolder").build()).toMatchSnapshot();
        SnapshotMatcher.validateSnapshots();

        Path path = Paths.get(OUTPUT_FILE);
        Assertions.assertThat(Files.exists(path));
    }

    public static class CustomFolderSnapshotConfig extends BaseSnapshotConfig {

        @Override
        public String getOutputDir() {
            return "src/test/some-folder";
        }
    }

    public static class CustomFolderSnapshotConfigLegacy extends BaseSnapshotConfig {

        @Override
        public String getOutputDir() {
            return "src/test/some-folder/";
        }
    }
}
