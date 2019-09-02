package au.com.origin.snapshots;

import org.junit.Test;

public class SnapshotRuleUnusedTest {

    @Test(expected = SnapshotMatchException.class)
    public void shouldUseExtension() {
        SnapshotMatcher.expect("Hello Wolrd").toMatchSnapshot();
    }
}
