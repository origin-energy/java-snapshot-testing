package au.com.origin.snapshots.serializers;

import au.com.origin.snapshots.Snapshot;
import au.com.origin.snapshots.SnapshotSerializerContext;

import java.util.function.BiFunction;

public interface SnapshotSerializer extends BiFunction<Object, SnapshotSerializerContext, Snapshot> {
  String getOutputFormat();
}
