package au.com.origin.snapshots;

import au.com.origin.snapshots.comparators.PlainTextEqualsComparator;
import au.com.origin.snapshots.comparators.SnapshotComparator;
import au.com.origin.snapshots.reporters.PlainTextSnapshotReporter;
import au.com.origin.snapshots.reporters.SnapshotReporter;
import au.com.origin.snapshots.serializers.SnapshotSerializer;
import au.com.origin.snapshots.serializers.ToStringSnapshotSerializer;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Snapshot Configuration
 * ----------------------
 *
 * Implement this interface when integrating `java-snapshot-testing` with a custom testing library
 *
 * For frameworks - consider extending the framework implementation instead
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
     * @return method under tests
     */
    Method getTestMethod(Class<?> testClass);

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
    default SnapshotSerializer getSerializer() {
        return new ToStringSnapshotSerializer();
    }

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
    default SnapshotComparator getComparator() {
        return new PlainTextEqualsComparator();
    }

    /**
     * Optional
     * Override to supply your own custom reporter functions
     * Reporters will run in the same sequence as provided.
     * Reporters can choose to throw exceptions in which case subsequent reporters will be skipped.
     * Typically one such reporter can be included at the end which can use common assertion libraries
     * like assertj or junit assertions to 'fail' the test. This is useful since IDEs like intellij can
     * then show a diff using their native diff tools.
     * <p>
     * In the absence of any 'throwing' reporters in the list, a default failure exception will be thrown
     * to make sure tests fail.
     *
     * @return custom reporter functions
     */
    default List<SnapshotReporter> getReporters() {
        return Collections.singletonList(new PlainTextSnapshotReporter());
    }

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
     * Default logic to determine if running on a CI environment is to check for the presence of a 'CI' env variable
     * Override this method if a custom CI detection logic is required.
     *
     * @return boolean indicating if we're running on a CI environment or not
     */
    default boolean isCi() {
        return StringUtils.isNotEmpty(System.getenv("CI"));
    }
}
