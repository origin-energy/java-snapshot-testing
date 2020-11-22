package au.com.origin.snapshots.config;

import au.com.origin.snapshots.serializers.SnapshotSerializer;
import au.com.origin.snapshots.serializers.ToStringSnapshotSerializer;

import java.util.Arrays;
import java.util.stream.Collectors;

public class ToStringSnapshotConfig extends BaseSnapshotConfig {

    @Override
    public SnapshotSerializer getSerializer() {
        return new SnapshotSerializer() {
            @Override
            public String getOutputFormat() {
                return ToStringSnapshotSerializer.FORMAT;
            }

            @Override
            public String apply(Object[] objects) {
                return Arrays.stream(objects).map(Object::toString).collect(Collectors.joining());
            }
        };
    }
}
