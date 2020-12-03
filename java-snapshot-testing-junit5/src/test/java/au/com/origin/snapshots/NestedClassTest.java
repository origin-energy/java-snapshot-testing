package au.com.origin.snapshots;

import au.com.origin.snapshots.junit5.SnapshotExtension;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static au.com.origin.snapshots.SnapshotMatcher.expect;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith({SnapshotExtension.class})
public class NestedClassTest {

    @AfterAll
    public static void afterAll() {
        Path path = Paths.get("src/test/java/au/com/origin/snapshots/__snapshots__/NestedClassTest.snap");
        assertThat(Files.exists(path)).isFalse();
    }

    @Nested
    class NestedClass {

        @Test
        public void helloWorldTest() {
            expect("Hello World").toMatchSnapshot();
        }
    }
}
