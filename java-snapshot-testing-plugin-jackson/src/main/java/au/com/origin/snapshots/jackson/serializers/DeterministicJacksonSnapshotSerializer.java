package au.com.origin.snapshots.jackson.serializers;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Attempts to deterministically render a snapshot.
 *
 * <p>This can help in situations where collections are rendering in a different order on subsequent
 * runs.
 *
 * <p>Note that collections will be ordered which mar or may not be desirable given your use case.
 */
public class DeterministicJacksonSnapshotSerializer extends JacksonSnapshotSerializer {

  @Override
  public void configure(ObjectMapper objectMapper) {
    objectMapper.enable(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY);
    objectMapper.registerModule(new DeterministicCollectionModule());
  }
}
