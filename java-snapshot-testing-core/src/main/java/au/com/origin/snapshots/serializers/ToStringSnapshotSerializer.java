package au.com.origin.snapshots.serializers;

import au.com.origin.snapshots.logging.LoggingHelper;
import lombok.extern.slf4j.Slf4j;

/**
 * This Serializer does a snapshot of the {@link Object#toString()} method
 *
 * <p>Will render each toString() on a separate line
 */
@Slf4j
@Deprecated
public class ToStringSnapshotSerializer
    extends au.com.origin.snapshots.serializers.v1.ToStringSnapshotSerializer {
  public ToStringSnapshotSerializer() {
    super();
    LoggingHelper.deprecatedV5(
        log,
        "Update to `au.com.origin.snapshots.serializers.v1.ToStringSnapshotSerializer` in `snapshot.properties`");
  }
}
