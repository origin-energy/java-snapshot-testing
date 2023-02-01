package au.com.origin.snapshots.jackson.serializers;

import au.com.origin.snapshots.logging.LoggingHelper;
import lombok.extern.slf4j.Slf4j;

@Deprecated
@Slf4j
public class JacksonSnapshotSerializer
    extends au.com.origin.snapshots.jackson.serializers.v1.JacksonSnapshotSerializer {

  public JacksonSnapshotSerializer() {
    super();
    LoggingHelper.deprecatedV5(
        log,
        "Update to `au.com.origin.snapshots.jackson.serializers.v1.JacksonSnapshotSerializer` in `snapshot.properties`");
  }
}
