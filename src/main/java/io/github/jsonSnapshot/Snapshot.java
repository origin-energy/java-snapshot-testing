package io.github.jsonSnapshot;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.assertj.core.util.diff.DiffUtils;
import org.assertj.core.util.diff.Patch;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public class Snapshot {

    private SnapshotFile snapshotFile;

    private Class clazz;

    private Method method;

    private Gson gson;

    private Object[] current;

    Snapshot(SnapshotFile snapshotFile, Class clazz, Method method, Object... current) {
        this.current = current;
        this.snapshotFile = snapshotFile;
        this.clazz = clazz;
        this.method = method;
        gson = new GsonBuilder().setPrettyPrinting().create();
    }

    public void toMatchSnapshot() {

        Set<String> rawSnapshots = snapshotFile.getRawSnapshots();

        String rawSnapshot = getRawSnapshot(rawSnapshots);

        String currentObject = takeSnapshot();

        // Match Snapshot
        if (rawSnapshot != null) {
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
        //compute the patch: this is the diffutils part
        Patch<String> patch =
                DiffUtils.diff(
                        Arrays.asList(rawSnapshot.trim().split("\n")),
                        Arrays.asList(currentObject.trim().split("\n")));
        String error = "Error on: \n" + currentObject.trim() + "\n\n" + patch.getDeltas().stream().map(delta -> delta.toString() + "\n").reduce(String::concat).get();
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
        return getSnapshotName() + gson.toJson(current);
    }

    public String getSnapshotName() {
        return clazz.getName() + "." + method.getName() + "=";
    }
}
