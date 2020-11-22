package au.com.origin.snapshots.reporters;

public interface SnapshotDiffReporter {

    boolean supportsFormat(String outputFormat);

    void reportDiff(String snapshotName, String rawSnapshot, String currentObject);
}
