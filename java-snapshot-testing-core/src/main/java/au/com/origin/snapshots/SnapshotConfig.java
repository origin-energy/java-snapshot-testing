package au.com.origin.snapshots;

import au.com.origin.snapshots.comparators.SnapshotComparator;
import au.com.origin.snapshots.reporters.SnapshotReporter;
import au.com.origin.snapshots.serializers.SnapshotSerializer;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;

/**
 * Snapshot Configuration
 * ----------------------
 *
 * Implement this interface when integrating `java-snapshot-testing` with a custom testing library
 *
 */
public interface SnapshotConfig {
    String JVM_UPDATE_SNAPSHOTS_PARAMETER = "updateSnapshot";

    /**
     * The base directory where files get written (excluding package directories)
     * default: "src/test/java"
     *
     * You might want to override if you have tests under "src/test/integration" for example
     *
     * @return snapshot output folder
     */
    String getOutputDir();

    /**
     * Subdirectory to store snapshots in
     *
     * @return name of subdirectory
     */
    String getSnapshotDir();

    /**
     * Optional
     * Algorithm to discover what class is under test.  Frameworks often have a better way to access this via frameworks hooks.
     * However, some don't and can use this method to discover it.
     *
     * @return class under test
     */
    default Class<?> getTestClass() {
        throw new RuntimeException("You forgot to to apply your testing framework annotations/rules for enabling snapshot matching.  See docs related to your framework for details.");
    }

    /**
     * Optional
     * Algorithm to discover what class is under test.  Frameworks often have a better way to access this via frameworks hooks.
     * However, some don't and can use this method to discover it.
     *
     * @param testClass class under test
     * @return method under tests
     */
    default Method getTestMethod(Class<?> testClass) {
        throw new RuntimeException("You forgot to to apply your testing framework annotations/rules for enabling snapshot matching.  See docs related to your framework for details.");
    }

    /**
     * Optional
     *
     * @return snapshots should be updated automatically without verification
     */
    default Optional<String> updateSnapshot() {
        return Optional.ofNullable(System.getProperty(JVM_UPDATE_SNAPSHOTS_PARAMETER));
    }

    /**
     * Optional
     * Override to supply your own custom serialization function
     *
     * @return custom serialization function
     */
    SnapshotSerializer getSerializer();

    /**
     * Optional
     * Allows you to perform any custom modifications to the file before it is saved
     *
     * @param testClass target test class
     * @param snapshotContent snapshot file as a string
     * @return snapshot file contents to be persisted
     */
    default String onSaveSnapshotFile(Class<?> testClass, String snapshotContent) {
        return snapshotContent;
    }

    /**
     * Optional
     * Override to supply your own custom comparator function
     *
     * @return custom comparator function
     */
    SnapshotComparator getComparator();

    /**
     * Optional
     * Override to supply your own custom reporter functions
     * Reporters will run in the same sequence as provided.
     * Reporters should throw exceptions to indicate comparison failure.
     * Exceptions thrown from reporters are aggregated and reported together.
     * Reporters that wish to leverage IDE comparison tools can use standard
     * assertion libraries like assertj, junit jupiter assertions (or) opentest4j.
     *
     * @return custom reporter functions
     */
    List<SnapshotReporter> getReporters();

    /**
     * Optional
     * This method is meant to detect if we're running on a CI environment.
     * This is used to determine the action to be taken when a snapshot is not found.
     *
     * If this method returns false, meaning we're NOT running on a CI environment (probably a dev machine),
     * a new snapshot is created when not found.
     *
     * If this method returns true, meaning we're running on a CI environment, no new snapshots are created
     * and an error is thrown instead to prevent tests from silently passing when snapshots are not found.
     *
     * Often to determine if running on a CI environment is to check for the presence of a 'CI' env variable
     *
     * @return boolean indicating if we're running on a CI environment or not
     */
    boolean isCI();
}
