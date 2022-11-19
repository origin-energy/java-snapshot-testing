package au.com.origin.snapshots;

import static org.assertj.core.api.Assertions.assertThat;

import au.com.origin.snapshots.junit5.SnapshotExtension;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

public class NestedClassTestWithExtends {

  @AfterAll
  public static void afterAll() {
    Path path =
        Paths.get(
            "src/test/java/au/com/origin/snapshots/__snapshots__/NestedClassTestWithExtends.snap");
    assertThat(Files.exists(path)).isFalse();
  }

  @ExtendWith(SnapshotExtension.class)
  @Nested
  class NestedClass {

    Expect expect;

    @Test
    public void helloWorldTest() {
      expect.toMatchSnapshot("Hello World");
    }
  }
}
