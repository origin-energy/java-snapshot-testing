package au.com.origin.snapshots.serializers;

import au.com.origin.snapshots.Snapshot;
import au.com.origin.snapshots.SnapshotSerializerContext;
import au.com.origin.snapshots.exceptions.SnapshotExtensionException;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.PrettyPrinter;
import com.fasterxml.jackson.core.util.DefaultIndenter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.core.util.Separators;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.util.Arrays;
import java.util.List;

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

    this.registerModule(new JavaTimeModule());
    this.registerModule(new Jdk8Module());

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
  public Snapshot apply(Object object, SnapshotSerializerContext gen) {
    try {
      List<?> objects = Arrays.asList(object);
      String body = objectMapper.writer(pp).writeValueAsString(objects);
      return  gen.toSnapshot(body);
    } catch (Exception e) {
      throw new SnapshotExtensionException("Jackson Serialization failed", e);
    }
  }

  @Override
  public String getOutputFormat() {
    return SerializerType.JSON.name();
  }
}
