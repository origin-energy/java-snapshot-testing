package au.com.origin.snapshots.serializers;

import java.util.function.Function;

public interface SnapshotSerializer extends Function<Object[], String> {

  String getOutputFormat();
}
