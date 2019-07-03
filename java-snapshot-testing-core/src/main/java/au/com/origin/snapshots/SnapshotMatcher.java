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
        start(config, new JacksonSerializer().getSerializer());
    }

    public static void start(SnapshotConfig config, Function<Object, String> serializer) {
        try {
            StackTraceElement stackElement = config.findStacktraceElement();
            Class<?> clazz = Class.forName(stackElement.getClassName());
            String testFilename = stackElement.getClassName().replaceAll("\\.", File.separator) + ".snap";

            File fileUnderTest = new File(testFilename);
            File snapshotDir = new File(fileUnderTest.getParentFile(), config.getSnapshotFolder());

            SnapshotFile snapshotFile =
                new SnapshotFile(config.getTestSrcDir(), snapshotDir.getPath() + File.separator + fileUnderTest.getName());

            SnapshotVerifier snapshotVerifier = new SnapshotVerifier(
                clazz,
                snapshotFile,
                serializer,
                config
            );
            INSTANCES.set(snapshotVerifier);
        } catch (ClassNotFoundException | IOException e) {
            throw new SnapshotMatchException(e.getMessage());
        }
    }

    public static void validateSnapshots() {
        INSTANCES.get().validateSnapshots();
    }

    public static Snapshot expect(Object firstObject, Object... objects) {
        return INSTANCES.get().expectCondition(firstObject, objects);
    }
}
