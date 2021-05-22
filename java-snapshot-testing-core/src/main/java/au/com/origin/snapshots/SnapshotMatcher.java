package au.com.origin.snapshots;

import au.com.origin.snapshots.annotations.UseSnapshotConfig;
import au.com.origin.snapshots.exceptions.SnapshotExtensionException;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.regex.Matcher;

@Slf4j
public class SnapshotMatcher {

    private static final ThreadLocal<SnapshotVerifier> INSTANCES = new ThreadLocal<>();
    private static final ThreadLocal<Boolean> SNAPSHOT_TEST_IN_PROGRESS = new ThreadLocal<Boolean>() {
        @Override
        protected Boolean initialValue() {
            return false;
        }
    };

    public static void start(SnapshotConfig config) {
        start(config, false, config.getTestClass());
    }

    public static void start(SnapshotConfig config, boolean failOnOrphans) {
        start(config, failOnOrphans, config.getTestClass());
    }

    /**
     * Execute before any tests have run for a given class
     * @param frameworkSnapshotConfig configuration to use
     * @param failOnOrphans should the test break if snapshots exist with no matching method in the test class
     * @param testClass     reference to class under test
     */
    public static void start(SnapshotConfig frameworkSnapshotConfig, boolean failOnOrphans, Class<?> testClass) {
        try {
            UseSnapshotConfig customConfig = testClass.getAnnotation(UseSnapshotConfig.class);
            SnapshotConfig snapshotConfig = customConfig == null ? frameworkSnapshotConfig : customConfig.value().newInstance();

            // Matcher.quoteReplacement required for Windows
            String testFilename = testClass.getName().replaceAll("\\.", Matcher.quoteReplacement(File.separator)) + ".snap";

            File fileUnderTest = new File(testFilename);
            File snapshotDir = new File(fileUnderTest.getParentFile(), snapshotConfig.getSnapshotFolder());

            // Support legacy trailing space syntax
            String testSrcDir = snapshotConfig.getOutputDir();
            String testSrcDirNoTrailing = testSrcDir.endsWith("/") ? testSrcDir.substring(0, testSrcDir.length()-1) : testSrcDir;
            SnapshotFile snapshotFile = new SnapshotFile(
                    testSrcDirNoTrailing,
                    snapshotDir.getPath() + File.separator + fileUnderTest.getName(),
                    testClass,
                    snapshotConfig::onSaveSnapshotFile
            );

            SnapshotVerifier snapshotVerifier = new SnapshotVerifier(
                testClass,
                snapshotFile,
                snapshotConfig,
                failOnOrphans
            );
            SNAPSHOT_TEST_IN_PROGRESS.set(true);
            INSTANCES.set(snapshotVerifier);
        } catch (IOException | InstantiationException | IllegalAccessException e) {
            throw new SnapshotExtensionException(e.getMessage());
        }
    }

    /**
     * Used to update the current test method being executed
     * @param method under test
     */
    public static void setTestMethod(Method method) {
        getSnapshotVerifier().setTestMethod(method);
    }

    /**
     * Execute after all tests have run for a given class
     */
    public static void validateSnapshots() {
        getSnapshotVerifier().validateSnapshots();
        SNAPSHOT_TEST_IN_PROGRESS.set(false);
    }

    /**
     * Make an assertion on the given input parameters againt what already exists
     *
     * @param firstObject first snapshot object
     * @param objects other snapshot objects
     * @return snapshot
     */
    public static Snapshot expect(Object firstObject, Object... objects) {
        if (!SNAPSHOT_TEST_IN_PROGRESS.get()) {
            throw new SnapshotExtensionException("setTestMethod() not called! Has SnapshotMatcher.start() been called?");
        }
        return getSnapshotVerifier().expectCondition(firstObject, objects);
    }

    private static SnapshotVerifier getSnapshotVerifier() {
        SnapshotVerifier instance = INSTANCES.get();
        if (instance == null) {
            throw new SnapshotExtensionException("Unable to locate SnapshotVerifier instance! Has SnapshotMatcher.start() been called?");
        }
        return instance;
    }
}
