package au.com.origin.snapshots.serializers;

import au.com.origin.snapshots.exceptions.SnapshotExtensionException;
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

public class JacksonSnapshotSerializer implements SnapshotSerializer {

    private Consumer<ObjectMapper> customConfiguration = objectMapper -> {};

    /**
     * Override to customize the Jackson objectMapper
     * @param objectMapper
     */
    public void configure(ObjectMapper objectMapper) { }

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
        objectMapper.enable(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS);
        objectMapper.enable(SerializationFeature.WRITE_DATES_WITH_ZONE_ID);
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
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
        configure(objectMapper);
        return objectMapper;
    }

    @Override
    public String apply(Object[] objects) {
        ObjectMapper objectMapper = buildObjectMapper();
        customConfiguration.accept(objectMapper);
        PrettyPrinter pp = buildDefaultPrettyPrinter();

        try {
            return objectMapper.writer(pp).writeValueAsString(objects);
        } catch (Exception e) {
            throw new SnapshotExtensionException("Jackson Serialization failed", e);
        }
    }
}
