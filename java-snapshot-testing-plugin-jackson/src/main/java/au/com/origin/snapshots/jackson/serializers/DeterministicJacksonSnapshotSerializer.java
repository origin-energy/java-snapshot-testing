package au.com.origin.snapshots.jackson.serializers;

import au.com.origin.snapshots.logging.LoggingHelper;
import lombok.extern.slf4j.Slf4j;

/**
 * Attempts to deterministically render a snapshot.
 *
 * <p>This can help in situations where collections are rendering in a different order on subsequent
 * runs.
 *
 * <p>Note that collections will be ordered which mar or may not be desirable given your use case.
 */
@Deprecated
@Slf4j
public class DeterministicJacksonSnapshotSerializer
    extends au.com.origin.snapshots.jackson.serializers.v1.DeterministicJacksonSnapshotSerializer {
  public DeterministicJacksonSnapshotSerializer() {
    super();
    LoggingHelper.deprecatedV5(
        log,
        "Update to `au.com.origin.snapshots.jackson.serializers.v1.DeterministicJacksonSnapshotSerializer` in `snapshot.properties`");
  }
}
