package au.com.origin.snapshots;

import au.com.origin.snapshots.serializers.JacksonSerializer;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.util.function.Function;

@Slf4j
public class SnapshotMatcher {

    private static final ThreadLocal<SnapshotVerifier> INSTANCES = new ThreadLocal<>();

    public static void start(SnapshotConfig config) {
        try {
            StackTraceElement stackElement = config.findStacktraceElement();
            Class<?> clazz = Class.forName(stackElement.getClassName());
            start(config, clazz);
        } catch (ClassNotFoundException e) {
            throw new SnapshotMatchException("Unable to locate test method");
        }
    }

    public static void start(SnapshotConfig config, Class<?> testClass) {
        start(config, testClass, new JacksonSerializer().getSerializer());
    }

    public static void start(SnapshotConfig config, Class<?> testClass, Function<Object, String> serializer) {
        try {
            String testFilename = testClass.getName().replaceAll("\\.", File.separator) + ".snap";

            File fileUnderTest = new File(testFilename);
            File snapshotDir = new File(fileUnderTest.getParentFile(), config.getSnapshotFolder());

            SnapshotFile snapshotFile =
                new SnapshotFile(config.getTestSrcDir(), snapshotDir.getPath() + File.separator + fileUnderTest.getName());

            SnapshotVerifier snapshotVerifier = new SnapshotVerifier(
                testClass,
                snapshotFile,
                serializer,
                config
            );
            INSTANCES.set(snapshotVerifier);
        } catch (IOException e) {
            throw new SnapshotMatchException(e.getMessage());
        }
    }

    public static void validateSnapshots() {
        SnapshotVerifier snapshotVerifier = INSTANCES.get();
        if (snapshotVerifier == null) {
            throw new SnapshotMatchException("Could not find Snapshot Verifier for this thread");
        }
        snapshotVerifier.validateSnapshots();
    }

    public static Snapshot expect(Object firstObject, Object... objects) {
        SnapshotVerifier instance = INSTANCES.get();
        if (instance == null) {
            throw new SnapshotMatchException("Unable to locate snapshot - has SnapshotMatcher.start() been called?");
        }
        return instance.expectCondition(firstObject, objects);
    }
}
