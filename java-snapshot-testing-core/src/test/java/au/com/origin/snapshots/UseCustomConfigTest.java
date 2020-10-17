package au.com.origin.snapshots;

import au.com.origin.snapshots.annotations.UseSnapshotConfig;
import au.com.origin.snapshots.annotations.UseSnapshotSerializer;
import au.com.origin.snapshots.config.BaseSnapshotConfig;
import au.com.origin.snapshots.config.ToStringSnapshotConfig;
import au.com.origin.snapshots.serializers.UppercaseToStringSerializer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static au.com.origin.snapshots.SnapshotMatcher.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@UseSnapshotConfig(ToStringSnapshotConfig.class)
@ExtendWith(MockitoExtension.class)
public class UseCustomConfigTest {

    private static final SnapshotConfig DEFAULT_CONFIG = new BaseSnapshotConfig();

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
    void canUseSnapshotConfigAnnotationAtClassLevel() {
        expect(new TestObject()).toMatchSnapshot();
    }

    @UseSnapshotSerializer(UppercaseToStringSerializer.class)
    @Test
    public void canUseSnapshotConfigAnnotationAtMethodLevel() {
        expect(new TestObject()).toMatchSnapshot();
    }

    private class TestObject {
        @Override
        public String toString() {
            return "This is a snapshot of the toString() method";
        }
    }
}
