package au.com.origin.snapshots;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class SnapshotExtensionUnusedTest {

    @Test
    public void shouldUseExtension() {
        Assertions.assertThrows(SnapshotMatchException.class, () -> {
            SnapshotMatcher.expect("Hello Wolrd").toMatchSnapshot();
        });
    }
}
