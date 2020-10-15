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
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.util.function.Consumer;
import java.util.function.Function;

public class JacksonSerializer implements Serializer {

    private Consumer<ObjectMapper> customConfiguration = objectMapper -> {};

    @Override
    public Function<Object, String> getSerializer() {
        ObjectMapper objectMapper = buildObjectMapper();
        customConfiguration.accept(objectMapper);
        PrettyPrinter pp = buildDefaultPrettyPrinter();

        return (object) -> {
            try {
                return objectMapper.writer(pp).writeValueAsString(object);
            } catch (Exception e) {
                throw new SnapshotMatchException(e.getMessage());
            }
        };
    }

    public void configure(Consumer<ObjectMapper> customConfiguration) {
        this.customConfiguration = customConfiguration;
    }

    private PrettyPrinter buildDefaultPrettyPrinter() {
        DefaultPrettyPrinter pp =
            new DefaultPrettyPrinter("") {

                // It's a requirement
                // @see https://github.com/FasterXML/jackson-databind/issues/2203
                public DefaultPrettyPrinter createInstance() {
                    return new DefaultPrettyPrinter(this);
                }

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

        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.registerModule(new Jdk8Module());

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
