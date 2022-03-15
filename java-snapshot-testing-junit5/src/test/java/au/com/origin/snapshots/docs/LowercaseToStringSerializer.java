package au.com.origin.snapshots.docs;

import au.com.origin.snapshots.Snapshot;
import au.com.origin.snapshots.SnapshotSerializerContext;
import au.com.origin.snapshots.serializers.SerializerType;
import au.com.origin.snapshots.serializers.SnapshotSerializer;

public class LowercaseToStringSerializer implements SnapshotSerializer {
  @Override
  public Snapshot apply(Object object, SnapshotSerializerContext gen) {
    return gen.toSnapshot(object.toString().toLowerCase());
  }

  @Override
  public String getOutputFormat() {
    return SerializerType.TEXT.name();
  }
}
