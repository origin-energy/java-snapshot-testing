package au.com.origin.snapshots.comparators;

import au.com.origin.snapshots.logging.LoggingHelper;
import lombok.extern.slf4j.Slf4j;

@Deprecated
@Slf4j
public class PlainTextEqualsComparator
    extends au.com.origin.snapshots.comparators.v1.PlainTextEqualsComparator {

  public PlainTextEqualsComparator() {
    super();
    LoggingHelper.deprecatedV5(
        log,
        "Update to `au.com.origin.snapshots.comparators.v1.PlainTextEqualsComparator` in `snapshot.properties`");
  }
}
