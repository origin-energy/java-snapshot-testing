package au.com.origin.snapshots.config;

import au.com.origin.snapshots.serializers.SnapshotSerializer;
import au.com.origin.snapshots.serializers.UppercaseToStringSerializer;

import java.util.Arrays;
import java.util.function.Function;
import java.util.stream.Collectors;

public class UppercaseToStringSnapshotConfig extends BaseSnapshotConfig {

    @Override
    public SnapshotSerializer getSerializer() {
        return new UppercaseToStringSerializer();
    }
}
