package au.com.origin.snapshots;

import au.com.origin.snapshots.config.TestSnapshotConfig;
import au.com.origin.snapshots.config.ToStringSnapshotConfig;
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

@UseSnapshotConfig(ToStringSnapshotConfig.class)
@ExtendWith(MockitoExtension.class)
public class UseCustomConfigTest {

    private static final SnapshotConfig DEFAULT_CONFIG = new TestSnapshotConfig();

    @BeforeAll
    static void beforeAll() {
        SnapshotUtils.copyTestSnapshots();
        start(DEFAULT_CONFIG);
    }

    @AfterAll
    static void afterAll() {
        validateSnapshots();
    }

    @Test
    void shouldUseSnapshotConfigDefinedOnTheClass() {
        expect(new TestObject()).toMatchSnapshot();
    }

    private class TestObject {
        @Override
        public String toString() {
            return "This is a snapshot of the toString() method";
        }
    }
}
