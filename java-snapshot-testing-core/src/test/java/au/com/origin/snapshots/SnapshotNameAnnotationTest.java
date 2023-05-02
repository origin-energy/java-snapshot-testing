package au.com.origin.snapshots;

import static org.junit.jupiter.api.Assertions.assertThrows;

import au.com.origin.snapshots.annotations.SnapshotName;
import au.com.origin.snapshots.config.BaseSnapshotConfig;
import au.com.origin.snapshots.exceptions.ReservedWordException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

public class SnapshotNameAnnotationTest {

  @BeforeEach
  void beforeEach() {
    SnapshotUtils.copyTestSnapshots();
  }

  @SnapshotName("can_use_snapshot_name")
  @Test
  void canUseSnapshotNameAnnotation(TestInfo testInfo) {
    SnapshotVerifier snapshotVerifier =
        new SnapshotVerifier(new BaseSnapshotConfig(), testInfo.getTestClass().get());
    Expect expect = Expect.of(snapshotVerifier, testInfo.getTestMethod().get());
    expect.toMatchSnapshot("Hello World");
    snapshotVerifier.validateSnapshots();
  }

  @SnapshotName("can use snapshot name with spaces")
  @Test
  void canUseSnapshotNameAnnotationWithSpaces(TestInfo testInfo) {
    SnapshotVerifier snapshotVerifier =
        new SnapshotVerifier(new BaseSnapshotConfig(), testInfo.getTestClass().get());
    Expect expect = Expect.of(snapshotVerifier, testInfo.getTestMethod().get());
    expect.toMatchSnapshot("Hello World");
    snapshotVerifier.validateSnapshots();
  }

  @SnapshotName("can't use '=' character in snapshot name")
  @Test
  void cannotUseEqualsInsideSnapshotName(TestInfo testInfo) {
    SnapshotVerifier snapshotVerifier =
        new SnapshotVerifier(new BaseSnapshotConfig(), testInfo.getTestClass().get());
    Expect expect = Expect.of(snapshotVerifier, testInfo.getTestMethod().get());
    assertThrows(ReservedWordException.class, () -> expect.toMatchSnapshot("FooBar"));
  }

  @SnapshotName("can't use '[' character in snapshot name")
  @Test
  void cannotUseOpeningSquareBracketInsideSnapshotName(TestInfo testInfo) {
    SnapshotVerifier snapshotVerifier =
        new SnapshotVerifier(new BaseSnapshotConfig(), testInfo.getTestClass().get());
    Expect expect = Expect.of(snapshotVerifier, testInfo.getTestMethod().get());
    assertThrows(ReservedWordException.class, () -> expect.toMatchSnapshot("FooBar"));
  }

  @SnapshotName("can't use ']' character in snapshot name")
  @Test
  void cannotUseClosingSquareBracketInsideSnapshotName(TestInfo testInfo) {
    SnapshotVerifier snapshotVerifier =
        new SnapshotVerifier(new BaseSnapshotConfig(), testInfo.getTestClass().get());
    Expect expect = Expect.of(snapshotVerifier, testInfo.getTestMethod().get());
    assertThrows(ReservedWordException.class, () -> expect.toMatchSnapshot("FooBar"));
  }

  @Test
  void cannotUseEqualsInsideScenarioName(TestInfo testInfo) {
    SnapshotVerifier snapshotVerifier =
        new SnapshotVerifier(new BaseSnapshotConfig(), testInfo.getTestClass().get());
    Expect expect = Expect.of(snapshotVerifier, testInfo.getTestMethod().get());
    assertThrows(
        ReservedWordException.class,
        () -> expect.scenario("can't use = symbol in scenario").toMatchSnapshot("FooBar"));
  }

  @Test
  void cannotUseOpeningSquareBracketInsideScenarioName(TestInfo testInfo) {
    SnapshotVerifier snapshotVerifier =
        new SnapshotVerifier(new BaseSnapshotConfig(), testInfo.getTestClass().get());
    Expect expect = Expect.of(snapshotVerifier, testInfo.getTestMethod().get());
    assertThrows(
        ReservedWordException.class,
        () -> expect.scenario("can't use [ symbol in scenario").toMatchSnapshot("FooBar"));
  }

  @Test
  void cannotUseClosingSquareBracketInsideScenarioName(TestInfo testInfo) {
    SnapshotVerifier snapshotVerifier =
        new SnapshotVerifier(new BaseSnapshotConfig(), testInfo.getTestClass().get());
    Expect expect = Expect.of(snapshotVerifier, testInfo.getTestMethod().get());
    assertThrows(
        ReservedWordException.class,
        () -> expect.scenario("can't use ] symbol in scenario").toMatchSnapshot("FooBar"));
  }
}
