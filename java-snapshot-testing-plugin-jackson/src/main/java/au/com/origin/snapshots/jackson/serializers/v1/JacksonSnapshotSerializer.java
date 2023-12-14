package au.com.origin.snapshots.jackson.serializers.v1;

import au.com.origin.snapshots.Snapshot;
import au.com.origin.snapshots.SnapshotSerializerContext;
import au.com.origin.snapshots.exceptions.SnapshotExtensionException;
import au.com.origin.snapshots.serializers.SerializerType;
import au.com.origin.snapshots.serializers.SnapshotSerializer;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.PrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.util.Arrays;
import java.util.List;

public class JacksonSnapshotSerializer implements SnapshotSerializer {

  private final PrettyPrinter pp = new SnapshotPrettyPrinter();
  private final ObjectMapper objectMapper =
      new ObjectMapper() {
        {
          this.enable(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS);
          this.enable(SerializationFeature.WRITE_DATES_WITH_ZONE_ID);
          this.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
          this.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
          this.setSerializationInclusion(JsonInclude.Include.NON_NULL);

          if (shouldFindAndRegisterModules()) {
            this.findAndRegisterModules();
          }

          this.setVisibility(
              this.getSerializationConfig()
                  .getDefaultVisibilityChecker()
                  .withFieldVisibility(JsonAutoDetect.Visibility.ANY)
                  .withGetterVisibility(JsonAutoDetect.Visibility.NONE)
                  .withSetterVisibility(JsonAutoDetect.Visibility.NONE)
                  .withCreatorVisibility(JsonAutoDetect.Visibility.NONE));
          JacksonSnapshotSerializer.this.configure(this);
        }
      };

  /**
   * Override to customize the Jackson objectMapper
   *
   * @param objectMapper existing ObjectMapper
   */
  public void configure(ObjectMapper objectMapper) {}

  /**
   * Override to control the registration of all available jackson modules within the classpath
   * which are locatable via JDK ServiceLoader facility, along with module-provided SPI.
   */
  protected boolean shouldFindAndRegisterModules() {
    return true;
  }

  @Override
  public Snapshot apply(Object object, SnapshotSerializerContext gen) {
    try {
      List<?> objects = Arrays.asList(object);
      String body = objectMapper.writer(pp).writeValueAsString(objects);
      return gen.toSnapshot(body);
    } catch (Exception e) {
      throw new SnapshotExtensionException("Jackson Serialization failed", e);
    }
  }

  @Override
  public String getOutputFormat() {
    return SerializerType.JSON.name();
  }
}
