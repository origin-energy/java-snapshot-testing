package au.com.origin.snapshots.exceptions;

public class SnapshotExtensionException extends RuntimeException {

    public SnapshotExtensionException(String message) {
        super(message);
    }

    public SnapshotExtensionException(String message, Throwable cause) {
        super(message, cause);
    }
}
