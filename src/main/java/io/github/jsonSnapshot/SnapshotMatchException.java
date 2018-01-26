package io.github.jsonSnapshot;

public class SnapshotMatchException extends RuntimeException {

    SnapshotMatchException(String message) {
        super(message);
    }

    SnapshotMatchException(String message, Throwable cause) {
        super(message, cause);
    }
}
