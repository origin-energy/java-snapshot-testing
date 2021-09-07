package au.com.origin.snapshots.serializers;

import au.com.origin.snapshots.Snapshot;
import au.com.origin.snapshots.SnapshotSerializerContext;

import java.util.Arrays;
import java.util.stream.Collectors;

public class LowercaseToStringSerializer implements SnapshotSerializer {
  @Override
  public Snapshot apply(Object[] objects, SnapshotSerializerContext gen) {
    String body = Arrays.stream(objects).map(Object::toString).collect(Collectors.joining()).toLowerCase();
    return gen.toSnapshot(body);
  }

  @Override
  public String getOutputFormat() {
    return SerializerType.TEXT.name();
  }
}
