package au.com.origin.snapshots.exceptions;

public class LogGithubIssueException extends RuntimeException {

  private static final String LOG_SUPPORT_TICKET =
          "\n\n*** This exception should never be thrown ***\n" +
          "Log a support ticket at https://github.com/origin-energy/java-snapshot-testing/issues with details of the exception\n";

  public LogGithubIssueException(String message) {
    super(message + LOG_SUPPORT_TICKET);
  }
}
