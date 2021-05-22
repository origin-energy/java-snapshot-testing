package au.com.origin.snapshots.serializers;

import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Base64SnapshotSerializer implements SnapshotSerializer {
  private static final ToStringSnapshotSerializer toStringSnapshotSerializer =
      new ToStringSnapshotSerializer();

  @Override
  public String apply(Object[] objects) {
    List<?> encoded = Arrays.stream(objects)
        .filter(Objects::nonNull)
        .map(it -> {
          byte[] bytes = it instanceof byte[] ? (byte[])it : it.toString().getBytes();
          return Base64.getEncoder().encodeToString(bytes);
        })
        .collect(Collectors.toList());
    return toStringSnapshotSerializer.apply(encoded.toArray());
  }

  @Override
  public String getOutputFormat() {
    return SerializerType.BASE64.name();
  }
}
