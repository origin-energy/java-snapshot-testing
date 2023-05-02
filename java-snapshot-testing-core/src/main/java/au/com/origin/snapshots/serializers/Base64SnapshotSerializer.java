package au.com.origin.snapshots.serializers;

import au.com.origin.snapshots.logging.LoggingHelper;
import lombok.extern.slf4j.Slf4j;

/**
 * This Serializer converts a byte[] into a base64 encoded string. If the input is not a byte[] it
 * will be converted using `.getBytes(StandardCharsets.UTF_8)` method
 */
@Slf4j
@Deprecated
public class Base64SnapshotSerializer
    extends au.com.origin.snapshots.serializers.v1.Base64SnapshotSerializer {
  public Base64SnapshotSerializer() {
    super();
    LoggingHelper.deprecatedV5(
        log,
        "Update to `au.com.origin.snapshots.serializers.v1.Base64SnapshotSerializer` in `snapshot.properties`");
  }
}
