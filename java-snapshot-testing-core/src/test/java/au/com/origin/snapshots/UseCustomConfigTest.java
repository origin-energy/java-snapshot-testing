package au.com.origin.snapshots;

import au.com.origin.snapshots.annotations.UseSnapshotConfig;
import au.com.origin.snapshots.config.BaseSnapshotConfig;
import au.com.origin.snapshots.config.ToStringSnapshotConfig;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static au.com.origin.snapshots.SnapshotMatcher.*;

@UseSnapshotConfig(ToStringSnapshotConfig.class)
@ExtendWith(MockitoExtension.class)
public class UseCustomConfigTest {

    private static final SnapshotConfig DEFAULT_CONFIG = new BaseSnapshotConfig();

    @BeforeAll
    static void beforeAll() {
        SnapshotUtils.copyTestSnapshots();
    }

    @Test
    void canUseSnapshotConfigAnnotationAtClassLevel() {
        start(DEFAULT_CONFIG);
        expect(new TestObject()).toMatchSnapshot();
        validateSnapshots();
    }


    private class TestObject {
        @Override
        public String toString() {
            return "This is a snapshot of the toString() method";
        }
    }
}
