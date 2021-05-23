package au.com.origin.snapshots;

import au.com.origin.snapshots.config.BaseSnapshotConfig;
import au.com.origin.snapshots.exceptions.SnapshotMatchException;
import org.junit.jupiter.api.Test;

import static au.com.origin.snapshots.SnapshotMatcher.start;
import static au.com.origin.snapshots.SnapshotMatcher.validateSnapshots;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ScenarioTest {

    private static final SnapshotConfig DEFAULT_CONFIG = new BaseSnapshotConfig();

    @Test
    void canTakeMultipleSnapshotsUsingScenario() {
        start(DEFAULT_CONFIG);
        SnapshotMatcher.expect("Default Snapshot").toMatchSnapshot();
        SnapshotMatcher.expect("Additional Snapshot").scenario("additional").toMatchSnapshot();
        validateSnapshots();
    }

    @Test
    void canTakeTheSameSnapshotTwice() {
        start(DEFAULT_CONFIG);
        SnapshotMatcher.expect("Default Snapshot").toMatchSnapshot();
        SnapshotMatcher.expect("Default Snapshot").toMatchSnapshot();
        SnapshotMatcher.expect("Scenario Snapshot").scenario("scenario").toMatchSnapshot();
        SnapshotMatcher.expect("Scenario Snapshot").scenario("scenario").toMatchSnapshot();
        validateSnapshots();
    }

    @Test
    void cannotTakeDifferentSnapshotsAtDefaultLevel() {
        start(DEFAULT_CONFIG);
        SnapshotMatcher.expect("Default Snapshot").toMatchSnapshot();
        assertThrows(SnapshotMatchException.class, () -> SnapshotMatcher.expect("Default Snapshot 2").toMatchSnapshot());
    }

    @Test
    void cannotTakeDifferentSnapshotsAtScenarioLevel() {
        start(DEFAULT_CONFIG);
        SnapshotMatcher.expect("Default Snapshot").scenario("scenario").toMatchSnapshot();
        assertThrows(SnapshotMatchException.class, () -> SnapshotMatcher.expect("Default Snapshot 2").scenario("scenario").toMatchSnapshot());
    }
}
