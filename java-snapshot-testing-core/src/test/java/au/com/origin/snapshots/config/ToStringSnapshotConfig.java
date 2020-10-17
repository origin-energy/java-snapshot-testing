package au.com.origin.snapshots.config;

import java.util.Arrays;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ToStringSnapshotConfig extends TestSnapshotConfig {

    @Override
    public Function<Object[], String> getSerializer() {
        return objects -> Arrays.stream(objects).map(Object::toString).collect(Collectors.joining());
    }
}
