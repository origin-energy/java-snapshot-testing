package au.com.origin.snapshots.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Inspired by: https://www.stubbornjava.com/posts/creating-a-somewhat-deterministic-jackson-objectmapper
 */
@Slf4j
public class DeterministicCollectionModule extends SimpleModule {

    public DeterministicCollectionModule() {
        addSerializer(Collection.class, new CollectionSerializer());
    }

    /**
     * Collections gets converted into a sorted Object[].  This then gets serialized using the default Array serializer.
     */
    private static class CollectionSerializer extends JsonSerializer<Collection> {

        @Override
        public void serialize(Collection value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
            Object[] sorted = convert(value);
            serializers.defaultSerializeValue(sorted, gen);
        }

        private Object[] convert(Collection<?> value) {
            if (value == null || value.isEmpty()) {
                return Collections.emptyList().toArray();
            }

            try {
                return value.stream()
                        .filter(Objects::nonNull)
                        .sorted()
                        .collect(Collectors.toList())
                        .toArray();
            } catch (ClassCastException ex) {
                log.warn("Unable to sort() collection - this may result in a non deterministic snapshot.\n" +
                        "Consider adding a custom serializer for this type via the JacksonSnapshotSerializer#configure() method.\n" + ex.getMessage());
                return value.toArray();
            }
        }
    }
}
