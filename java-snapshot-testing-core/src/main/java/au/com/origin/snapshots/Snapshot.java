package au.com.origin.snapshots;

import au.com.origin.snapshots.exceptions.SnapshotMatchException;
import au.com.origin.snapshots.serializers.DeterministicJacksonSnapshotSerializer;
import au.com.origin.snapshots.serializers.JacksonSnapshotSerializer;
import au.com.origin.snapshots.serializers.SnapshotSerializer;
import au.com.origin.snapshots.serializers.ToStringSnapshotSerializer;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.assertj.core.util.diff.DiffUtils;
import org.assertj.core.util.diff.Patch;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.Set;

public class Snapshot {

    private SnapshotConfig snapshotConfig;
    private SnapshotSerializer snapshotSerializer;
    private final SnapshotFile snapshotFile;
    private final Class testClass;
    private final Method testMethod;
    private final Object[] current;

    private String scenario = null;

    Snapshot(
            SnapshotConfig snapshotConfig,
            SnapshotFile snapshotFile,
            Class testClass,
            Method testMethod,
            Object... current) {
        this.snapshotConfig = snapshotConfig;
        this.snapshotSerializer = snapshotConfig.getSerializer();
        this.current = current;
        this.snapshotFile = snapshotFile;
        this.testClass = testClass;
        this.testMethod = testMethod;
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
        this.scenario = scenario;
        return this;
    }

    /**
     * Apply a custom serializer for this snapshot
     *
     * @param serializer your custom serializer
     * @return Snapshot
     */
    public Snapshot serializer(SnapshotSerializer serializer) {
        this.snapshotSerializer = serializer;
        return this;
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
     * @return  this
     */
    @SneakyThrows
    public Snapshot serializer(Class<? extends SnapshotSerializer> serializer) {
        this.snapshotSerializer = serializer.newInstance();
        return this;
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
            if (!rawSnapshot.trim().equals(currentObject.trim())) {
                snapshotFile.createDebugFile(currentObject.trim());
                throw generateDiffError(rawSnapshot, currentObject);
            }
        } else {
            // Create New Snapshot
            snapshotFile.push(currentObject);
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

    private SnapshotMatchException generateDiffError(String rawSnapshot, String currentObject) {
        // compute the patch: this is the diffutils part
        Patch<String> patch =
            DiffUtils.diff(
                Arrays.asList(rawSnapshot.trim().split("\n")),
                Arrays.asList(currentObject.trim().split("\n")));
        String error =
            "Error on: \n"
                + currentObject.trim()
                + "\n\n"
                + patch
                .getDeltas()
                .stream()
                .map(delta -> delta.toString() + "\n")
                .reduce(String::concat)
                .get();
        return new SnapshotMatchException(error);
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
