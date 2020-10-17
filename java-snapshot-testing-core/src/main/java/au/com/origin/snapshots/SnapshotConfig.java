package au.com.origin.snapshots;

import au.com.origin.snapshots.serializers.JacksonSnapshotSerializer;
import au.com.origin.snapshots.serializers.SnapshotSerializer;

import java.lang.reflect.Method;
import java.util.Optional;

/**
 *
 */
public interface SnapshotConfig {
    String JVM_UPDATE_SNAPSHOTS_PARAMETER = "updateSnapshot";

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
     * Will determine what snapshots should be updated automatically without verification
     */
    default Optional<String> updateSnapshot() {
        String value = System.getProperty(JVM_UPDATE_SNAPSHOTS_PARAMETER);
        if ( value != null) {
            return Optional.of(value);
        } else {
            return Optional.empty();
        }
    }

    /**
     * Override to supply your own serializion function
     * @return
     */
    default SnapshotSerializer getSerializer() {
        return new JacksonSnapshotSerializer();
    }
}
