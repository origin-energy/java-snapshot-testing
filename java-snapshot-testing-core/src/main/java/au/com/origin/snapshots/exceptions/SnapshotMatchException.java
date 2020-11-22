package au.com.origin.snapshots.exceptions;

import org.opentest4j.AssertionFailedError;
import org.opentest4j.MultipleFailuresError;

import java.util.Collections;
import java.util.List;

public class SnapshotMatchException extends MultipleFailuresError {

    public SnapshotMatchException(String message) {
        super(message, Collections.emptyList());
    }

    public SnapshotMatchException(String message, Throwable cause) {
        super(message, Collections.singletonList(cause));
    }

    public SnapshotMatchException(String message, List<Throwable> causes) {
        super(message, causes);
    }

    // for a single failure with difference
    public static SnapshotMatchException of(String message, Object expected, Object actual) {
        return new SnapshotMatchException(message, new AssertionFailedError(message, expected, actual));
    }
}
