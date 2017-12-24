package com.bonna.jsonSnapshot;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.lang.reflect.Method;
import java.util.List;

public class Snapshot {

    private SnapshotFile snapshotFile;

    private Class clazz;

    private Method method;

    private Gson gson;

    private Object[] current;

    Snapshot(SnapshotFile snapshotFile, Class clazz, Method method, Object ...current) {
        this.current = current;
        this.snapshotFile = snapshotFile;
        this.clazz = clazz;
        this.method = method;
        gson = new GsonBuilder().setPrettyPrinting().create();
    }

    public void toMatchSnapshot() {

        List<String> rawSnapshots = snapshotFile.getRawSnapshots();

        String rawSnapshot = getRawSnapshot(rawSnapshots);

        String currentObject = takeSnapshot();

        // Match Snapshot
        if (rawSnapshot != null) {
            if (!rawSnapshot.trim().equals(currentObject.trim())) {
                throw new SnapshotMatchException(rawSnapshot + "\n is different than \n " + currentObject);
            }
        }
        // Create New Snapshot
        else {
            snapshotFile.push(currentObject);
        }
    }

    private String getRawSnapshot(List<String> rawSnapshots) {
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
        return clazz.getName() + "| with |" + method.getName() + "=";
    }
}
