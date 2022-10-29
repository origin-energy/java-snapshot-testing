package au.com.origin.snapshots.jackson.serializers;

import au.com.origin.snapshots.exceptions.SnapshotExtensionException;
import au.com.origin.snapshots.serializers.SerializerType;
import au.com.origin.snapshots.serializers.SnapshotSerializer;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.PrettyPrinter;
import com.fasterxml.jackson.core.util.DefaultIndenter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.core.util.Separators;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class JacksonSnapshotSerializer implements SnapshotSerializer {

  private final PrettyPrinter pp = new DefaultPrettyPrinter("") {
    {
      Indenter lfOnlyIndenter = new DefaultIndenter("  ", "\n");
      this.indentArraysWith(lfOnlyIndenter);
      this.indentObjectsWith(lfOnlyIndenter);
    }

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
  private final ObjectMapper objectMapper = new ObjectMapper() {{
    this.enable(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS);
    this.enable(SerializationFeature.WRITE_DATES_WITH_ZONE_ID);
    this.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    this.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
    this.setSerializationInclusion(JsonInclude.Include.NON_NULL);

    this.findAndRegisterModules();

    this.setVisibility(
        this
            .getSerializationConfig()
            .getDefaultVisibilityChecker()
            .withFieldVisibility(JsonAutoDetect.Visibility.ANY)
            .withGetterVisibility(JsonAutoDetect.Visibility.NONE)
            .withSetterVisibility(JsonAutoDetect.Visibility.NONE)
            .withCreatorVisibility(JsonAutoDetect.Visibility.NONE));
    JacksonSnapshotSerializer.this.configure(this);
  }};

  /**
   * Override to customize the Jackson objectMapper
   *
   * @param objectMapper existing ObjectMapper
   */
  public void configure(ObjectMapper objectMapper) {
  }

  @Override
  public String apply(Object[] objects) {
    try {
      return objectMapper.writer(pp).writeValueAsString(objects);
    } catch (Exception e) {
      throw new SnapshotExtensionException("Jackson Serialization failed", e);
    }
  }

  @Override
  public String getOutputFormat() {
    return SerializerType.JSON.name();
  }
}
