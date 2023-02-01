package au.com.origin.snapshots.reporters;

import au.com.origin.snapshots.logging.LoggingHelper;
import lombok.extern.slf4j.Slf4j;

@Deprecated
@Slf4j
public class PlainTextSnapshotReporter
    extends au.com.origin.snapshots.reporters.v1.PlainTextSnapshotReporter {

  public PlainTextSnapshotReporter() {
    super();
    LoggingHelper.deprecatedV5(
        log,
        "Update to `au.com.origin.snapshots.reporters.v1.PlainTextSnapshotReporter` in `snapshot.properties`");
  }
}
