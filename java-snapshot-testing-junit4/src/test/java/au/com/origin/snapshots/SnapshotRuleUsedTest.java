package au.com.origin.snapshots;

import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

public class SnapshotRuleUsedTest {

    @ClassRule public static SnapshotClassRule snapshotClassRule = new SnapshotClassRule();
    @Rule public SnapshotRule snapshotRule = new SnapshotRule();

    @Test
    public void shouldUseExtension() {
        SnapshotMatcher.expect("Hello Wolrd").toMatchSnapshot();
    }

    @Test
    public void shouldUseExtensionAgain() {
        SnapshotMatcher.expect("Hello Wolrd Again").toMatchSnapshot();
    }
}
