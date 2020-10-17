package au.com.origin.snapshots;

import au.com.origin.snapshots.annotations.UseSnapshotSerializer;
import au.com.origin.snapshots.config.BaseSnapshotConfig;
import au.com.origin.snapshots.serializers.LowercaseToStringSerializer;
import au.com.origin.snapshots.serializers.UppercaseToStringSerializer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static au.com.origin.snapshots.SnapshotMatcher.*;

@UseSnapshotSerializer(LowercaseToStringSerializer.class)
@ExtendWith(MockitoExtension.class)
public class UseCustomSerializerTest {

    private static final SnapshotConfig DEFAULT_CONFIG = new BaseSnapshotConfig();

    @BeforeAll
    static void beforeAll() {
        SnapshotUtils.copyTestSnapshots();
    }

    @DisplayName("@SnapshotSerializer on a class")
    @Test
    void canUseSnapshotSerializerAnnotationAtClassLevel() {
        start(DEFAULT_CONFIG);
        expect(new TestObject()).toMatchSnapshot();
        validateSnapshots();
    }

    @DisplayName("@SnapshotSerializer on a method")
    @UseSnapshotSerializer(UppercaseToStringSerializer.class)
    @Test
    public void canUseSnapshotSerializerAnnotationAtMethodLevel() {
        expect(new TestObject()).toMatchSnapshot();
    }

    private class TestObject {
        @Override
        public String toString() {
            return "This is a snapshot of the toString() method";
        }
    }
}
