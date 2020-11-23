package au.com.origin.snapshots.serializers;

import au.com.origin.snapshots.SnapshotConfig;
import au.com.origin.snapshots.SnapshotUtils;
import au.com.origin.snapshots.config.BaseSnapshotConfig;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static au.com.origin.snapshots.SnapshotMatcher.*;

@ExtendWith(MockitoExtension.class)
public class ToStringSnapshotSerializerTest {
    private static final SnapshotConfig DEFAULT_CONFIG = new BaseSnapshotConfig() {
        @Override
        public SnapshotSerializer getSerializer() {
            return new ToStringSnapshotSerializer();
        }
    };

    @BeforeAll
    static void beforeAll() {
        SnapshotUtils.copyTestSnapshots();
    }

    @Test
    void canUseSnapshotConfigAnnotationAtClassLevel() {
        start(DEFAULT_CONFIG);
        expect(new Dummy(1, "John Doe")).toMatchSnapshot();
        validateSnapshots();
    }

    @Test
    void shouldSupportStringFormat() {
        Assertions.assertThat(new ToStringSnapshotSerializer().getOutputFormat()).isEqualTo(SerializerType.TEXT.name());
    }

    @AllArgsConstructor
    @Data
    private static class Dummy {
        private int id;
        private String name;

        public String toString() {
            return "ToStringSerializerTest.Dummy(id=" + this.getId() + ", name=" + this.getName() + ")";
        }
    }
}
