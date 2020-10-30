package au.com.origin.snapshots;

import au.com.origin.snapshots.serializers.SnapshotSerializer;
import au.com.origin.snapshots.serializers.ToStringSnapshotSerializer;

import java.lang.reflect.Method;
import java.util.Optional;

/**
 *  Snapshot Configuration
 *  ----------------------
 *
 *  Implement this interface when integrating `java-snapshot-testing` with a custom testing library
 *
 *  For frameworks - consider extending the framework implementation instead
 */
public interface SnapshotConfig {
    String JVM_UPDATE_SNAPSHOTS_PARAMETER = "updateSnapshot";

    /**
     * The base directory where files get written (excluding package directories)
     * default: "src/test/java"
     *
     * You might want to override if you have tests under "src/test/integration" for example
     *
     * @deprecated Use getOutputDir() instead
     * @return test src folder
     */
    @Deprecated
    default String getTestDir() {
        return getOutputDir();
    }

    /**
     * The base directory where files get written (excluding package directories)
     * default: "src/test/java"
     *
     * You might want to override if you have tests under "src/test/integration" for example
     *
     * @return snapshot output folder
     */
    default String getOutputDir() {
        return "src/test/java";
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
     * Optional
     * Algorithm to discover what class is under test.  Frameworks often have a better way to access this via frameworks hooks.
     * However, some don't and can use this method to discover it.
     *
     * @return class under test
     */
    Class<?> getTestClass();

    /**
     * Optional
     * Algorithm to discover what class is under test.  Frameworks often have a better way to access this via frameworks hooks.
     * However, some don't and can use this method to discover it.
     *
     * @param testClass class under test
     * @return  method under tests
     */
    Method getTestMethod(Class<?> testClass);

    /**
     * Optional
     *
     * @return snapshots should be updated automatically without verification
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
     * Optional
     * Override to supply your own custom serialization function
     *
     * @return custom serialization function
     */
    default SnapshotSerializer getSerializer() {
        return new ToStringSnapshotSerializer();
    }

}
