package au.com.origin.snapshots;

import au.com.origin.snapshots.junit4.SnapshotClassRule;
import au.com.origin.snapshots.junit4.SnapshotRule;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

public class SnapshotRuleUsedTest {

    @ClassRule public static SnapshotClassRule snapshotClassRule = new SnapshotClassRule();
    @Rule public SnapshotRule snapshotRule = new SnapshotRule();

    @Test
    public void shouldUseExtension() {
        SnapshotMatcher.expect("Hello World").toMatchSnapshot();
    }

    @Test
    public void shouldUseExtensionAgain() {
        SnapshotMatcher.expect("Hello World", "Hello World Again").toMatchSnapshot();
    }
}
