package au.com.origin.snapshots.serializers;

import au.com.origin.snapshots.Snapshot;
import au.com.origin.snapshots.SnapshotSerializerContext;
import au.com.origin.snapshots.SnapshotHeader;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ToStringSnapshotSerializerTest {
  ToStringSnapshotSerializer serializer = new ToStringSnapshotSerializer();

  private SnapshotSerializerContext mockSnapshotGenerator = new SnapshotSerializerContext(
          "base64Test",
          null,
          new SnapshotHeader(),
          ToStringSnapshotSerializerTest.class,
          null // it's not used in these scenarios
  );

  @Test
  void shouldSnapshotAnyString() {
    Snapshot result = serializer.apply(new Object[] {"John Doe"}, mockSnapshotGenerator);
    assertThat(result.getBody()).isEqualTo("[\nJohn Doe\n]");
  }

  @Test
  void shouldSnapshotUnicode() {
    Snapshot result = serializer.apply(new Object[] {"🤔"}, mockSnapshotGenerator);
    assertThat(result.getBody()).isEqualTo("[\n🤔\n]");
  }

  @Test
  void shouldSnapshotAnyObject() {
    Snapshot result = serializer.apply(new Object[] {new Dummy(1, "John Doe")}, mockSnapshotGenerator);
    assertThat(result.getBody()).isEqualTo("[\nToStringSerializerTest.Dummy(id=1, name=John Doe)\n]");
  }

  @Test
  void shouldSnapshotMultipleObjects() {
    Snapshot result = serializer.apply(new Object[] {new Dummy(1, "John Doe"), new Dummy(2, "Sarah Doe")}, mockSnapshotGenerator);
    assertThat(result.getBody()).isEqualTo("[\nToStringSerializerTest.Dummy(id=1, name=John Doe)\nToStringSerializerTest.Dummy(id=2, name=Sarah Doe)\n]");
  }

  @Test
  void shouldSupportBase64SerializerType() {
    assertThat(serializer.getOutputFormat()).isEqualTo("TEXT");
  }

  @Test
  void shouldReplaceThreeConsecutiveNewLines() {
    Snapshot result = serializer.apply(new Object[] {"John\n\n\nDoe"}, mockSnapshotGenerator);
    assertThat(result.getBody()).isEqualTo("[\nJohn\n.\n.\nDoe\n]");
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
