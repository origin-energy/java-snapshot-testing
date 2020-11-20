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

    public static void start(SnapshotConfig config) {
        start(config, false, config.getTestClass());
    }

    public static void start(SnapshotConfig config, boolean failOnOrphans) {
        start(config, failOnOrphans, config.getTestClass());
    }

    /**
     * Execute before any tests have run for a given class
     * @param defaultConfig configuration to use
     * @param failOnOrphans should the test break if snapshots exist with no matching method in the test class
     * @param testClass     reference to class under test
     */
    public static void start(SnapshotConfig defaultConfig, boolean failOnOrphans, Class<?> testClass) {
        try {
            UseSnapshotConfig customConfig = testClass.getAnnotation(UseSnapshotConfig.class);
            SnapshotConfig resolvedConfig = customConfig == null ? defaultConfig : customConfig.value().newInstance();

            // Matcher.quoteReplacement required for Windows
            String testFilename = testClass.getName().replaceAll("\\.", Matcher.quoteReplacement(File.separator)) + ".snap";

            File fileUnderTest = new File(testFilename);
            File snapshotDir = new File(fileUnderTest.getParentFile(), resolvedConfig.getSnapshotFolder());

            // Support legacy trailing space syntax
            String testSrcDir = resolvedConfig.getOutputDir();
            String testSrcDirNoTrailing = testSrcDir.endsWith("/") ? resolvedConfig.getOutputDir().substring(0, testSrcDir.length()-1) : resolvedConfig.getOutputDir();
            SnapshotFile snapshotFile = new SnapshotFile(
                    testSrcDirNoTrailing,
                    snapshotDir.getPath() + File.separator + fileUnderTest.getName(),
                    testClass,
                    resolvedConfig::onSaveSnapshotFile
            );

            SnapshotVerifier snapshotVerifier = new SnapshotVerifier(
                testClass,
                snapshotFile,
                resolvedConfig,
                failOnOrphans
            );
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
        INSTANCES.get().setTestMethod(method);
    }

    /**
     * Execute after all tests have run for a given class
     */
    public static void validateSnapshots() {
        SnapshotVerifier snapshotVerifier = INSTANCES.get();
        if (snapshotVerifier == null) {
            throw new SnapshotExtensionException("Could not find Snapshot Verifier for this thread");
        }
        snapshotVerifier.validateSnapshots();
    }

    /**
     * Make an assertion on the given input parameters againt what already exists
     *
     * @param firstObject first snapshot object
     * @param objects other snapshot objects
     * @return snapshot
     */
    public static Snapshot expect(Object firstObject, Object... objects) {
        SnapshotVerifier instance = INSTANCES.get();
        if (instance == null) {
            throw new SnapshotExtensionException("Unable to locate snapshot - has SnapshotMatcher.start() been called?");
        }
        return instance.expectCondition(firstObject, objects);
    }
}
