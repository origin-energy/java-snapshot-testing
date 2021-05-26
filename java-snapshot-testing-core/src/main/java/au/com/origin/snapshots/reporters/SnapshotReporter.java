package au.com.origin.snapshots.reporters;

public interface SnapshotReporter {

  boolean supportsFormat(String outputFormat);

  void report(String snapshotName, String rawSnapshot, String currentObject);
}
