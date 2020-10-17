package au.com.origin.snapshots.serializers;

import java.util.Arrays;
import java.util.stream.Collectors;

public class LowercaseToStringSerializer implements SnapshotSerializer {
    @Override
    public String apply(Object[] objects) {
        return Arrays.stream(objects).map(Object::toString).collect(Collectors.joining()).toLowerCase();
    }
}
