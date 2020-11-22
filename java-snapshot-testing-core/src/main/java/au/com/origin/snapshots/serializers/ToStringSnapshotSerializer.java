package au.com.origin.snapshots.serializers;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * This Serializer does a snapshot of the {@link Object#toString()} method
 *
 * Will render each toString() on a separate line
 */
public class ToStringSnapshotSerializer implements SnapshotSerializer {

    @Override
    public String apply(Object[] objects) {
        return "[\n" + Arrays.stream(objects).map(Object::toString).collect(Collectors.joining("\n")) + "\n]";
    }

    @Override
    public String getOutputFormat() {
        return SerializerType.TEXT.name();
    }
}
