package au.com.origin.snapshots.serializers;

import au.com.origin.snapshots.Snapshot;
import au.com.origin.snapshots.SnapshotSerializerContext;

public class UppercaseToStringSerializer implements SnapshotSerializer {
  @Override
  public Snapshot apply(Object object, SnapshotSerializerContext gen) {
    return gen.toSnapshot(object.toString().toUpperCase());
  }

  @Override
  public String getOutputFormat() {
    return SerializerType.TEXT.name();
  }
}
