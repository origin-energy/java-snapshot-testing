package au.com.origin.snapshots.serializers;

import au.com.origin.snapshots.Snapshot;
import au.com.origin.snapshots.SnapshotSerializerContext;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * This Serializer converts a byte[] into a base64 encoded string.  If the input is not
 * a byte[] it will be converted using `.getBytes(StandardCharsets.UTF_8)` method
 */
public class Base64SnapshotSerializer implements SnapshotSerializer {
  private static final ToStringSnapshotSerializer toStringSnapshotSerializer =
      new ToStringSnapshotSerializer();

  @Override
  public Snapshot apply(Object[] objects, SnapshotSerializerContext gen) {
    List<?> encoded = Arrays.stream(objects)
        .filter(Objects::nonNull)
        .map(it -> {
          byte[] bytes = it instanceof byte[] ? (byte[]) it : it.toString().getBytes(StandardCharsets.UTF_8);
          return Base64.getEncoder().encodeToString(bytes);
        })
        .collect(Collectors.toList());
    return toStringSnapshotSerializer.apply(encoded.toArray(), gen);
  }

  @Override
  public String getOutputFormat() {
    return SerializerType.BASE64.name();
  }
}
