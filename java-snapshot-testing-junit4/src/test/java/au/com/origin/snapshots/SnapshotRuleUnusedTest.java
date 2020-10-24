package au.com.origin.snapshots;

import au.com.origin.snapshots.exceptions.SnapshotExtensionException;
import org.junit.Test;

public class SnapshotRuleUnusedTest {

    @Test(expected = SnapshotExtensionException.class)
    public void shouldUseExtension() {
        SnapshotMatcher.expect("Hello World").toMatchSnapshot();
    }
}
