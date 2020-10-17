package au.com.origin.snapshots.serializers;

import java.util.function.Function;

public interface SnapshotSerializer {

    Function<Object[], String> getSerializer();
}
