package au.com.origin.snapshots;

public class SnapshotMatchException extends RuntimeException {

    public SnapshotMatchException(String message) {
        super(message);
    }

    public SnapshotMatchException(String message, Throwable cause) {
        super(message, cause);
    }
}
