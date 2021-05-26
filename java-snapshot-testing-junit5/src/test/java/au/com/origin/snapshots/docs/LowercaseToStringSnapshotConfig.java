package au.com.origin.snapshots.docs;

import au.com.origin.snapshots.PropertyResolvingSnapshotConfig;
import au.com.origin.snapshots.serializers.SnapshotSerializer;

public class LowercaseToStringSnapshotConfig extends PropertyResolvingSnapshotConfig {

  @Override
  public SnapshotSerializer getSerializer() {
    return new LowercaseToStringSerializer();
  }
}
