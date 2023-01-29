package au.com.origin.snapshots.logging;

import org.slf4j.Logger;

public class LoggingHelper {

  public static void deprecatedV5(Logger log, String message) {
    log.warn(
        "Deprecation Warning:\n " + message + "\n\nThis feature will be removed in version 5.X");
  }
}
