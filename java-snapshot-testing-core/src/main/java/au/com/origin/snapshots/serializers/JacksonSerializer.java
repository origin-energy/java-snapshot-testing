package au.com.origin.snapshots.serializers;

import au.com.origin.snapshots.SnapshotMatchException;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.PrettyPrinter;
import com.fasterxml.jackson.core.util.DefaultIndenter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter.Indenter;
import com.fasterxml.jackson.core.util.Separators;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.util.function.Function;

public class JacksonSerializer implements Serializer {

    @Override
    public Function<Object, String> getSerializer() {
        ObjectMapper objectMapper = buildObjectMapper();

        PrettyPrinter pp = buildDefaultPrettyPrinter();

        return (object) -> {
            try {
                return objectMapper.writer(pp).writeValueAsString(object);
            } catch (Exception e) {
                throw new SnapshotMatchException(e.getMessage());
            }
        };
    }

    private PrettyPrinter buildDefaultPrettyPrinter() {
        DefaultPrettyPrinter pp =
            new DefaultPrettyPrinter("") {
                @Override
                public DefaultPrettyPrinter withSeparators(Separators separators) {
                    this._separators = separators;
                    this._objectFieldValueSeparatorWithSpaces =
                            separators.getObjectFieldValueSeparator() + " ";
                    return this;
                }
            };
        Indenter lfOnlyIndenter = new DefaultIndenter("  ", "\n");
        pp.indentArraysWith(lfOnlyIndenter);
        pp.indentObjectsWith(lfOnlyIndenter);
        return pp;
    }

    private ObjectMapper buildObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        objectMapper.setVisibility(
            objectMapper
                .getSerializationConfig()
                .getDefaultVisibilityChecker()
                .withFieldVisibility(JsonAutoDetect.Visibility.ANY)
                .withGetterVisibility(JsonAutoDetect.Visibility.NONE)
                .withSetterVisibility(JsonAutoDetect.Visibility.NONE)
                .withCreatorVisibility(JsonAutoDetect.Visibility.NONE));
        return objectMapper;
    }
}
