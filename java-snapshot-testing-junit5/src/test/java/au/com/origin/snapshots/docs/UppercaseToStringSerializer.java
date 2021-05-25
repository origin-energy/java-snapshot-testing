package au.com.origin.snapshots.docs;

import au.com.origin.snapshots.serializers.SerializerType;
import au.com.origin.snapshots.serializers.SnapshotSerializer;

import java.util.Arrays;
import java.util.stream.Collectors;

public class UppercaseToStringSerializer implements SnapshotSerializer {
  @Override
  public String apply(Object[] objects) {
    return Arrays.stream(objects).map(Object::toString).collect(Collectors.joining()).toUpperCase();
  }

  @Override
  public String getOutputFormat() {
    return SerializerType.TEXT.name();
  }
}
