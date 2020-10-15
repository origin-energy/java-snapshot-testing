package au.com.origin.snapshots;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.regex.Matcher;

@Slf4j
public class SnapshotMatcher {

    private static final ThreadLocal<SnapshotVerifier> INSTANCES = new ThreadLocal<>();

    /**
     * Execute before any tests have run for a given class
     */
    public static void start(SnapshotConfig config) {
        start(config, config.getTestClass());
    }

    /**
     * Execute before any tests have run for a given class
     */
    public static void start(SnapshotConfig defaultConfig, Class<?> testClass) {
        try {
            UseSnapshotConfig customConfig = testClass.getAnnotation(UseSnapshotConfig.class);
            SnapshotConfig config = customConfig == null ? defaultConfig : customConfig.value().newInstance();

            String testFilename = testClass.getName().replaceAll("\\.", Matcher.quoteReplacement(File.separator)) + ".snap";

            File fileUnderTest = new File(testFilename);
            File snapshotDir = new File(fileUnderTest.getParentFile(), config.getSnapshotFolder());

            SnapshotFile snapshotFile =
                new SnapshotFile(config.getTestSrcDir(), snapshotDir.getPath() + File.separator + fileUnderTest.getName());

            SnapshotVerifier snapshotVerifier = new SnapshotVerifier(
                testClass,
                snapshotFile,
                config
            );
            INSTANCES.set(snapshotVerifier);
        } catch (IOException | InstantiationException | IllegalAccessException e) {
            throw new SnapshotMatchException(e.getMessage());
        }
    }

    /**
     * Used to update the current test method being executed
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
            throw new SnapshotMatchException("Could not find Snapshot Verifier for this thread");
        }
        snapshotVerifier.validateSnapshots();
    }

    /**
     * Make an assertion on the given input parameters
     *
     * @param firstObject first snapshot object
     * @param objects other snapshot objects
     */
    public static Snapshot expect(Object firstObject, Object... objects) {
        SnapshotVerifier instance = INSTANCES.get();
        if (instance == null) {
            throw new SnapshotMatchException("Unable to locate snapshot - has SnapshotMatcher.start() been called?");
        }
        return instance.expectCondition(firstObject, objects);
    }
}
