package au.com.origin.snapshots.logging;

import org.slf4j.Logger;

public class LoggingHelper {

  public static void deprecatedV5(Logger log, String message) {
    log.warn(
        "\n\n** Deprecation Warning **\nThis feature will be removed in version 5.X\n"
            + message
            + "\n\n");
  }
}
