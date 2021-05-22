package au.com.origin.snapshots.serializers;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ToStringSnapshotSerializerTest {
    ToStringSnapshotSerializer serializer = new ToStringSnapshotSerializer();

    @Test
    void shouldSnapshotAnyString() {
        String result = serializer.apply(new Object[] { "John Doe" });
        assertThat(result).isEqualTo("[\nJohn Doe\n]");
    }

    @Test
    void shouldSnapshotUnicode() {
        String result = serializer.apply(new Object[] { "🤔" });
        assertThat(result).isEqualTo("[\n🤔\n]");
    }

    @Test
    void shouldSnapshotAnyObject() {
        String result = serializer.apply(new Object[] { new Dummy(1, "John Doe") });
        assertThat(result).isEqualTo("[\nToStringSerializerTest.Dummy(id=1, name=John Doe)\n]");
    }

    @Test
    void shouldSnapshotMultipleObjects() {
        String result = serializer.apply(new Object[] { new Dummy(1, "John Doe"), new Dummy(2, "Sarah Doe") });
        assertThat(result).isEqualTo("[\nToStringSerializerTest.Dummy(id=1, name=John Doe)\nToStringSerializerTest.Dummy(id=2, name=Sarah Doe)\n]");
    }

    @Test
    void shouldSupportBase64SerializerType() {
        assertThat(serializer.getOutputFormat()).isEqualTo("TEXT");
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
