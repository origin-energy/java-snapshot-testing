package com.github.andrebonna.jsonSnapshot;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class SnapshotTest {

    private static final String FILE_PATH = "src/test/java/anyFilePath";
    private static final String SNAPSHOT_NAME = "java.lang.String| with |toString=";
    private static final String SNAPSHOT = "java.lang.String| with |toString=[\n  \"anyObject\"\n]";

    private SnapshotFile snapshotFile;

    private Snapshot snapshot;


    @Before
    public void setUp() throws NoSuchMethodException, IOException {
        snapshotFile = new SnapshotFile("anyFilePath");
        snapshot = new Snapshot(snapshotFile, String.class,
                String.class.getDeclaredMethod("toString"), "anyObject");
    }

    @After
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
        assertThat(snapshotFile.getRawSnapshots()).isEqualTo(Stream.of(SNAPSHOT).collect(Collectors.toList()));
    }

    @Test(expected = SnapshotMatchException.class)
    public void shouldMatchSnapshotWithException() {
        snapshotFile.push(SNAPSHOT_NAME + "anyWrongSnapshot");
        snapshot.toMatchSnapshot();
    }

}
