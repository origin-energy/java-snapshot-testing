package au.com.origin.snapshots;

import au.com.origin.snapshots.exceptions.SnapshotExtensionException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class SnapshotExtensionUnusedTest {

    @Test
    public void shouldUseExtension() {
        Assertions.assertThrows(SnapshotExtensionException.class, () -> {
            SnapshotMatcher.expect("Hello World").toMatchSnapshot();
        });
    }
}
