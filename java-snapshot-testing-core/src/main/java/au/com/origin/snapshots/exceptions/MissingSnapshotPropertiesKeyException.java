package au.com.origin.snapshots.exceptions;

public class MissingSnapshotPropertiesKeyException extends RuntimeException {

    public MissingSnapshotPropertiesKeyException(String key) {
        super("\"snapshot.properties\" is missing required key="+key);
    }
}
