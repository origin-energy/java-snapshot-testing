package au.com.origin.snapshots;

import java.lang.reflect.Method;

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
     * Optional - return the test class name
     * @return
     */
    Class<?> getTestClass();

    /**
     * Optional - return the test method name
     *
     * @param testClass
     * @return
     */
    Method getTestMethod(Class<?> testClass);

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
