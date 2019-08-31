package au.com.origin.snapshots;

import org.apache.commons.lang3.StringUtils;
import org.assertj.core.util.diff.DiffUtils;
import org.assertj.core.util.diff.Patch;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.Set;
import java.util.function.Function;

class Snapshot {

    private final SnapshotConfig snapshotConfig;
    private final SnapshotFile snapshotFile;
    private final Class testClass;
    private final Method testMethod;
    private final Function<Object, String> jsonFunction;
    private final Object[] current;

    private String scenario = null;

    Snapshot(
            SnapshotConfig snapshotConfig,
            SnapshotFile snapshotFile,
            Class testClass,
            Method testMethod,
            Function<Object, String> jsonFunction,
            Object... current) {
        this.snapshotConfig = snapshotConfig;
        this.current = current;
        this.snapshotFile = snapshotFile;
        this.testClass = testClass;
        this.testMethod = testMethod;
        this.jsonFunction = jsonFunction;
    }

    /**
     * Normally a snapshot can be applied only once to a test method.
     *
     * For Parameterized tests where the same method is executed multiple times you can supply
     * the scenario() to overcome this restriction.  Ensure each scenario is unique.
     *
     * @param scenario - unique scenario description
     */
    public Snapshot scenario(String scenario) {
        this.scenario = scenario;
        return this;
    }

    public void toMatchSnapshot() {

        Set<String> rawSnapshots = snapshotFile.getRawSnapshots();

        String rawSnapshot = getRawSnapshot(rawSnapshots);

        String currentObject = takeSnapshot();

        // Match Snapshot
        if (rawSnapshot != null && !snapshotConfig.shouldUpdateSnapshot()) {
            if (!rawSnapshot.trim().equals(currentObject.trim())) {
                throw generateDiffError(rawSnapshot, currentObject);
            }
        }
        // Create New Snapshot
        else {
            snapshotFile.push(currentObject);
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
        return getSnapshotName() + jsonFunction.apply(current);
    }

    String getSnapshotName() {
        String scenarioFormat = StringUtils.isBlank(scenario) ? "" : "[" + scenario + "]";
        return testClass.getName() + "." + testMethod.getName() + scenarioFormat + "=";
    }
}
