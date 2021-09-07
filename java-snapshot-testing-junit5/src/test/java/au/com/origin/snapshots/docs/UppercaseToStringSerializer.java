package au.com.origin.snapshots.docs;

import au.com.origin.snapshots.Snapshot;
import au.com.origin.snapshots.SnapshotSerializerContext;
import au.com.origin.snapshots.serializers.SerializerType;
import au.com.origin.snapshots.serializers.SnapshotSerializer;

import java.util.Arrays;
import java.util.stream.Collectors;

public class UppercaseToStringSerializer implements SnapshotSerializer {
  @Override
  public Snapshot apply(Object[] objects, SnapshotSerializerContext gen) {
    String body = Arrays.stream(objects).map(Object::toString).collect(Collectors.joining()).toUpperCase();
    return gen.toSnapshot(body);
  }

  @Override
  public String getOutputFormat() {
    return SerializerType.TEXT.name();
  }
}
