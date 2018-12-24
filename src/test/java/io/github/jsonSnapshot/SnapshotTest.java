package io.github.jsonSnapshot;

import com.google.gson.GsonBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class SnapshotTest {

    private static final SnapshotConfig DEFAULT_CONFIG = new DefaultConfig();
    private static final String FILE_PATH = "src/test/java/anyFilePath";
    private static final String SNAPSHOT_NAME = "java.lang.String.toString=";
    private static final String SNAPSHOT = "java.lang.String.toString=[\n  \"anyObject\"\n]";

    private SnapshotFile snapshotFile;

    private Snapshot snapshot;


    @BeforeEach
    public void setUp() throws NoSuchMethodException, IOException {
        snapshotFile = new SnapshotFile(DEFAULT_CONFIG.getFilePath(), "anyFilePath");
        snapshot = new Snapshot(snapshotFile, String.class,
                String.class.getDeclaredMethod("toString"),
                (object) -> new GsonBuilder().setPrettyPrinting().create().toJson(object),"anyObject");
    }

    @AfterEach
    public void tearDown() throws IOException {
        Files.delete(Paths.get(FILE_PATH));
    }

    @Test
    public void shouldGetSnapshotNameSuccessfully() {
        String snapshotName = snapshot.getSnapshotName();
        assertThat(snapshotName).isEqualTo(SNAPSHOT_NAME);
    }

    @Test
    public void shouldMatchSnapshotSuccessfully() {
        snapshot.toMatchSnapshot();
        assertThat(snapshotFile.getRawSnapshots()).isEqualTo(Stream.of(SNAPSHOT).collect(Collectors.toCollection(TreeSet::new)));
    }

    @Test
    public void shouldMatchSnapshotWithException() {
        snapshotFile.push(SNAPSHOT_NAME + "anyWrongSnapshot");

        assertThrows(SnapshotMatchException.class, snapshot::toMatchSnapshot);
    }

}
