package au.com.origin.snapshots;

import au.com.origin.snapshots.comparators.SnapshotComparator;
import au.com.origin.snapshots.exceptions.SnapshotMatchException;
import au.com.origin.snapshots.reporters.SnapshotReporter;
import au.com.origin.snapshots.serializers.DeterministicJacksonSnapshotSerializer;
import au.com.origin.snapshots.serializers.JacksonSnapshotSerializer;
import au.com.origin.snapshots.serializers.SnapshotSerializer;
import au.com.origin.snapshots.serializers.ToStringSnapshotSerializer;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.With;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Snapshot {

    private final SnapshotConfig snapshotConfig;
    private final SnapshotFile snapshotFile;
    private final Class<?> testClass;
    private final Method testMethod;
    private final Object[] current;
    private final boolean isCi;

    @With
    private final SnapshotSerializer snapshotSerializer;
    @With
    private final SnapshotComparator snapshotComparator;
    @With
    private final List<SnapshotReporter> snapshotReporters;
    @With
    private final String scenario;

    Snapshot(
            SnapshotConfig snapshotConfig,
            SnapshotFile snapshotFile,
            Class<?> testClass,
            Method testMethod,
            Object... current) {
        this(snapshotConfig,
                snapshotFile,
                testClass,
                testMethod,
                current,
                snapshotConfig.isCi(),
                snapshotConfig.getSerializer(),
                snapshotConfig.getComparator(),
                snapshotConfig.getReporters(),
                null);
    }

    /**
     * Normally a snapshot can be applied only once to a test method.
     *
     * For Parameterized tests where the same method is executed multiple times you can supply
     * the scenario() to overcome this restriction.  Ensure each scenario is unique.
     *
     * @param scenario - unique scenario description
     * @return Snapshot
     */
    public Snapshot scenario(String scenario) {
        return this.withScenario(scenario);
    }

    /**
     * Apply a custom serializer for this snapshot
     *
     * @param serializer your custom serializer
     * @return Snapshot
     */
    public Snapshot serializer(SnapshotSerializer serializer) {
        return this.withSnapshotSerializer(serializer);
    }

    /**
     * Apply a custom comparator for this snapshot
     *
     * @param comparator your custom comparator
     * @return Snapshot
     */
    public Snapshot comparator(SnapshotComparator comparator) {
        return this.withSnapshotComparator(comparator);
    }

    /**
     * Apply a list of custom reporters for this snapshot
     * This will replace the default reporters defined in the config
     *
     * @param reporters your custom reporters
     * @return Snapshot
     */
    public Snapshot reporters(SnapshotReporter... reporters) {
        return this.withSnapshotReporters(Arrays.asList(reporters));
    }

    /**
     * Alias for serializer(new ToStringSerializer())
     * @return Snapshot
     */
    public Snapshot string() {
        return serializer(new ToStringSnapshotSerializer());
    }

    /**
     * Alias for serializer(new JacksonSnapshotSerializer())
     * @return Snapshot
     */
    public Snapshot json() {
        return serializer(new JacksonSnapshotSerializer());
    }

    /**
     * Alias for serializer(new DeterministicJacksonSnapshotSerializer())
     * @return Snapshot
     */
    public Snapshot orderedJson() {
        return serializer(new DeterministicJacksonSnapshotSerializer());
    }

    /**
     * Apply a custom serializer for this snapshot
     *
     * @param serializer your custom serializer
     * @return this
     */
    @SneakyThrows
    public Snapshot serializer(Class<? extends SnapshotSerializer> serializer) {
        return this.withSnapshotSerializer(serializer.getConstructor().newInstance());
    }

    public void toMatchSnapshot() {

        Set<String> rawSnapshots = snapshotFile.getRawSnapshots();

        String rawSnapshot = getRawSnapshot(rawSnapshots);

        String currentObject = takeSnapshot();

        if (rawSnapshot != null && shouldUpdateSnapshot()) {
            snapshotFile.getRawSnapshots().remove(rawSnapshot);
            rawSnapshot = null;
        }

        if (rawSnapshot != null) {
            // Match existing Snapshot
            if (!snapshotComparator.matches(getSnapshotName(), rawSnapshot, currentObject)) {
                snapshotFile.createDebugFile(currentObject.trim());

                List<SnapshotReporter> reporters = snapshotReporters
                        .stream()
                        .filter(reporter -> reporter.supportsFormat(snapshotSerializer.getOutputFormat()))
                        .collect(Collectors.toList());

                if (reporters.isEmpty()) {
                    String comparator = snapshotComparator.getClass().getSimpleName();
                    throw new IllegalStateException("No compatible reporters found for comparator " + comparator);
                }

                for (SnapshotReporter reporter : reporters) {
                    reporter.report(getSnapshotName(), rawSnapshot, currentObject);
                }

                throw new SnapshotMatchException("Error matching snapshot");
            }
        } else {
            if (this.isCi) {
                log.warn("We detected you are running on a CI Server - if this is incorrect please override the isCI() method in SnapshotConfig");
                throw new SnapshotMatchException("Snapshot [" + getSnapshotName() + "] not found. Has this snapshot been committed ?");
            } else {
                log.warn("We detected you are running on a developer machine - if this is incorrect please override the isCI() method in SnapshotConfig");
                // Create New Snapshot
                snapshotFile.push(currentObject);
            }
        }
        snapshotFile.deleteDebugFile();
    }

    private boolean shouldUpdateSnapshot() {
        if (snapshotConfig.updateSnapshot().isPresent()) {
            return getSnapshotName().contains(snapshotConfig.updateSnapshot().get());
        } else {
            return false;
        }
    }

    private String getRawSnapshot(Collection<String> rawSnapshots) {
        for (String rawSnapshot : rawSnapshots) {
            if (rawSnapshot.contains(getSnapshotName())) {
                return rawSnapshot;
            }
        }
        return null;
    }

    private String takeSnapshot() {
        return getSnapshotName() + snapshotSerializer.apply(current);
    }

    String getSnapshotName() {
        String scenarioFormat = StringUtils.isBlank(scenario) ? "" : "[" + scenario + "]";
        return testClass.getName() + "." + testMethod.getName() + scenarioFormat + "=";
    }
}
