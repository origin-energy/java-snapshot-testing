package au.com.origin.snapshots.serializers;

import au.com.origin.snapshots.Snapshot;
import au.com.origin.snapshots.SnapshotSerializerContext;
import au.com.origin.snapshots.SnapshotHeader;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Files;

import static org.assertj.core.api.Assertions.assertThat;

public class Base64SnapshotSerializerTest {

  private SnapshotSerializerContext mockSnapshotGenerator = new SnapshotSerializerContext(
          "base64Test",
          null,
          new SnapshotHeader(),
          Base64SnapshotSerializerTest.class,
          null // it's not used in these scenarios
  );

  @Test
  void shouldSnapshotAByteArray() {
    Base64SnapshotSerializer serializer = new Base64SnapshotSerializer();
    Snapshot result = serializer.apply(new Object[] {"John Doe".getBytes()}, mockSnapshotGenerator);
    assertThat(result.getBody()).isEqualTo("[\nSm9obiBEb2U=\n]");
  }

  @Test
  void shouldSnapshotAnyString() {
    Base64SnapshotSerializer serializer = new Base64SnapshotSerializer();
    Snapshot result = serializer.apply(new Object[] {"John Doe"}, mockSnapshotGenerator);
    assertThat(result.getBody()).isEqualTo("[\nSm9obiBEb2U=\n]");
  }

  @Test
  void shouldSnapshotAFile() throws Exception {
    Base64SnapshotSerializer serializer = new Base64SnapshotSerializer();
    File f = new File("src/test/resources/origin-logo.png");
    byte[] content = Files.readAllBytes(f.toPath());

    Snapshot result = serializer.apply(new Object[] {content}, mockSnapshotGenerator);
    assertThat(result.getBody()).isEqualTo(
        "[\n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\n]");
  }

  @Test
  void shouldSupportBase64SerializerType() {
    Base64SnapshotSerializer serializer = new Base64SnapshotSerializer();
    assertThat(serializer.getOutputFormat()).isEqualTo("BASE64");
  }

}
