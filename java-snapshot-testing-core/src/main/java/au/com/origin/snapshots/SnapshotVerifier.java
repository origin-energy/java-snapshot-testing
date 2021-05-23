package au.com.origin.snapshots;

import au.com.origin.snapshots.exceptions.SnapshotMatchException;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.util.Arrays.isNullOrEmpty;

@Slf4j
@RequiredArgsConstructor
public class SnapshotVerifier {

    private final Class<?> testClass;
    private final SnapshotFile snapshotFile;
    private final SnapshotConfig config;
    private final boolean failOnOrphans;

    private final List<Snapshot> calledSnapshots = new ArrayList<>();

    @Setter
    private Method testMethod = null;

    @SneakyThrows
    public Snapshot expectCondition(Object firstObject, Object... others) {
        Object[] objects = mergeObjects(firstObject, others);
        Method resolvedTestMethod = testMethod == null ? config.getTestMethod(testClass) : testMethod;
        Snapshot snapshot =
                new Snapshot(config, snapshotFile, testClass, resolvedTestMethod, objects);
        calledSnapshots.add(snapshot);
        return snapshot;
    }

    public void validateSnapshots() {
        Set<String> rawSnapshots = snapshotFile.getRawSnapshots();
        Set<String> snapshotNames =
                calledSnapshots.stream().map(Snapshot::getSnapshotName).collect(Collectors.toSet());
        List<String> unusedRawSnapshots = new ArrayList<>();

        for (String rawSnapshot : rawSnapshots) {
            boolean foundSnapshot = false;
            for (String snapshotName : snapshotNames) {
                if (rawSnapshot.contains(snapshotName)) {
                    foundSnapshot = true;
                    break;
                }
            }
            if (!foundSnapshot) {
                unusedRawSnapshots.add(rawSnapshot);
            }
        }
        if (unusedRawSnapshots.size() > 0) {
            String errorMessage = "All unused Snapshots:\n"
                    + String.join("\n", unusedRawSnapshots)
                    + "\n\nHave you deleted tests? Have you renamed a test method?";
            if (failOnOrphans) {
                log.warn(errorMessage);
                throw new SnapshotMatchException("ERROR: Found orphan snapshots");
            } else {
                log.warn(errorMessage);
            }
        }
        snapshotFile.cleanup();
    }

    private Object[] mergeObjects(Object firstObject, Object[] others) {
        Object[] objects = new Object[1];
        objects[0] = firstObject;
        if (!isNullOrEmpty(others)) {
            objects = Stream.concat(Arrays.stream(objects), Arrays.stream(others))
                .toArray(Object[]::new);
        }
        return objects;
    }
}
