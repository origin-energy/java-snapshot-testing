package au.com.origin.snapshots.serializers;

import java.util.function.Function;

public interface Serializer {

    Function<Object, String> getSerializer();
}
