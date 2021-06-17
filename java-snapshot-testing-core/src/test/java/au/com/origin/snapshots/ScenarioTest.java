package au.com.origin.snapshots;

import au.com.origin.snapshots.config.BaseSnapshotConfig;
import au.com.origin.snapshots.exceptions.SnapshotExtensionException;
import au.com.origin.snapshots.exceptions.SnapshotMatchException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

import static org.junit.jupiter.api.Assertions.assertThrows;

class ScenarioTest {

  private static final SnapshotConfig DEFAULT_CONFIG = new BaseSnapshotConfig();

  @Test
  void canTakeMultipleSnapshotsUsingScenario(TestInfo testInfo) {
    SnapshotVerifier snapshotVerifier = new SnapshotVerifier(DEFAULT_CONFIG, testInfo.getTestClass().get());
    Expect expect = Expect.of(snapshotVerifier, testInfo.getTestMethod().get());
    expect.toMatchSnapshot("Default Snapshot");
    expect.scenario("additional").toMatchSnapshot("Additional Snapshot");
    snapshotVerifier.validateSnapshots();
  }

  @Test
  void canNotAcceptTheSameSnapshotTwice(TestInfo testInfo) {
    SnapshotVerifier snapshotVerifier = new SnapshotVerifier(DEFAULT_CONFIG, testInfo.getTestClass().get());
    Expect expect = Expect.of(snapshotVerifier, testInfo.getTestMethod().get());
    expect.toMatchSnapshot("Default Snapshot");
    expect.scenario("scenario").toMatchSnapshot("Scenario Snapshot");
    assertThrows(SnapshotExtensionException.class, () -> expect.scenario("scenario").toMatchSnapshot("Scenario Snapshot"));
  }

  @Test
  void cannotTakeDifferentSnapshotsAtDefaultLevel(TestInfo testInfo) {
    SnapshotVerifier snapshotVerifier = new SnapshotVerifier(DEFAULT_CONFIG, testInfo.getTestClass().get());
    Expect expect = Expect.of(snapshotVerifier, testInfo.getTestMethod().get());
    expect.toMatchSnapshot("Default Snapshot");
    assertThrows(SnapshotMatchException.class, () -> expect.toMatchSnapshot("Default Snapshot 2"));
  }

  @Test
  void cannotTakeDifferentSnapshotsAtScenarioLevel(TestInfo testInfo) {
    SnapshotVerifier snapshotVerifier = new SnapshotVerifier(DEFAULT_CONFIG, testInfo.getTestClass().get());
    Expect expect = Expect.of(snapshotVerifier, testInfo.getTestMethod().get());
    expect.scenario("scenario").toMatchSnapshot("Default Snapshot");
    assertThrows(SnapshotMatchException.class, () -> expect.scenario("scenario").toMatchSnapshot("Default Snapshot 2"));
  }
}
