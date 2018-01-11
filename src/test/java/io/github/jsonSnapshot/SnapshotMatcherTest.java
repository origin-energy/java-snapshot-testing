package io.github.jsonSnapshot;

import org.apache.commons.lang3.StringUtils;
import org.junit.*;
import org.junit.runners.MethodSorters;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class SnapshotMatcherTest {

    private static final String FILE_PATH = "src/test/java/io/github/jsonSnapshot/SnapshotMatcherTest.snap";

    @BeforeClass
    public static void beforeAll() {
        SnapshotMatcher.start();
    }

    @AfterClass
    public static void afterAll() throws IOException {
        SnapshotMatcher.validateSnapshots();
        Files.delete(Paths.get(FILE_PATH));
    }


    @Test
    public void should1ShowSnapshotSuccessfully() throws IOException {

        File f = new File(FILE_PATH);
        if(!f.exists() || f.isDirectory()) {
            throw new RuntimeException("File should exist here");
        }

        assertThat(StringUtils.join(Files.readAllLines(f.toPath()), "\n")).isEqualTo("");

        SnapshotMatcher.expect("any type of object").toMatchSnapshot();

        assertThat(StringUtils.join(Files.readAllLines(f.toPath()), "\n")).
                isEqualTo("io.github.jsonSnapshot.SnapshotMatcherTest.should1ShowSnapshotSuccessfully=[\n" +
                        "  \"any type of object\"\n" +
                        "]");

    }

    @Test
    public void should2SecondSnapshotExecutionSuccessfully() throws IOException {

        File f = new File(FILE_PATH);
        if(!f.exists() || f.isDirectory()) {
            throw new RuntimeException("File should exist here");
        }

        SnapshotMatcher.expect("any second type of object", "any third type of object").toMatchSnapshot();

        assertThat(StringUtils.join(Files.readAllLines(f.toPath()), "\n")).
                isEqualTo("io.github.jsonSnapshot.SnapshotMatcherTest.should1ShowSnapshotSuccessfully=[\n" +
                        "  \"any type of object\"\n" +
                        "]\n\n\n" +
                        "io.github.jsonSnapshot.SnapshotMatcherTest.should2SecondSnapshotExecutionSuccessfully=[\n" +
                        "  \"any second type of object\",\n" +
                        "  \"any third type of object\"\n" +
                        "]");

    }
}
