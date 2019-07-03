package au.com.origin.snapshots;

/**
 *
 */
public interface SnapshotConfig {
    String JVM_UPDATE_SNAPSHOTS_PARAMETER = "update-snapshots";

    /**
     * The directory containing the src files
     *
     * @return test src folder
     */
    default String getTestSrcDir() {
        return "src/test/java/";
    }

    /**
     * Subdirectory to store snapshots in
     *
     * @return name of subdirectory
     */
    default String getSnapshotFolder() {
        return "__snapshots__";
    }

    /**
     * Locate the current test file on the call stack.  This file wil control the
     * name of the generated .snap file
     */
    StackTraceElement findStacktraceElement();

    /**
     * Should the snapshots be updated without verification
     *
     * @return if true will replace snapshots without verification
     */
    default boolean shouldUpdateSnapshot() {
        String value = System.getProperty(JVM_UPDATE_SNAPSHOTS_PARAMETER);
        return value != null && value.toUpperCase().startsWith("T");
    }
}
