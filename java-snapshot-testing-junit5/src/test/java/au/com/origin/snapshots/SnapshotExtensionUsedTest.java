package au.com.origin.snapshots;

import au.com.origin.snapshots.junit5.SnapshotExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(SnapshotExtension.class)
public class SnapshotExtensionUsedTest {

    @Test
    public void shouldUseExtension() {
        SnapshotMatcher.expect("Hello World").toMatchSnapshot();
    }

    @Test
    public void shouldUseExtensionAgain() {
        SnapshotMatcher.expect("Hello World", "Hello World Again").toMatchSnapshot();
    }
}
