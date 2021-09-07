package au.com.origin.snapshots;

import au.com.origin.snapshots.config.BaseSnapshotConfig;
import au.com.origin.snapshots.config.SnapshotConfig;
import au.com.origin.snapshots.exceptions.SnapshotMatchException;
import au.com.origin.snapshots.serializers.UppercaseToStringSerializer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class SnapshotIntegrationTest {

  private static final SnapshotConfig DEFAULT_CONFIG = new BaseSnapshotConfig();

  static SnapshotVerifier snapshotVerifier;

  @BeforeAll
  static void beforeAll() {
    SnapshotUtils.copyTestSnapshots();
    snapshotVerifier = new SnapshotVerifier(DEFAULT_CONFIG, SnapshotIntegrationTest.class);
  }

  @AfterAll
  static void afterAll() {
    snapshotVerifier.validateSnapshots();
  }

  @Test
  void shouldMatchSnapshotOne(TestInfo testInfo) {
    Expect expect = Expect.of(snapshotVerifier, testInfo.getTestMethod().get());
    expect.toMatchSnapshot(FakeObject.builder().id("anyId1").value(1).name("anyName1").build());
  }

  @Test
  void shouldMatchSnapshotTwo(TestInfo testInfo) {
    Expect expect = Expect.of(snapshotVerifier, testInfo.getTestMethod().get());
    expect.toMatchSnapshot(FakeObject.builder().id("anyId2").value(2).name("anyName2").build());
  }

  @Test
  void shouldMatchSnapshotThree(TestInfo testInfo) {
    Expect expect = Expect.of(snapshotVerifier, testInfo.getTestMethod().get());
    expect.toMatchSnapshot(FakeObject.builder().id("anyId3").value(3).name("anyName3").build());
  }

  @Test
  void shouldMatchSnapshotFour(TestInfo testInfo) {
    Expect expect = Expect.of(snapshotVerifier, testInfo.getTestMethod().get());
    expect
        .toMatchSnapshot(FakeObject.builder().id("anyId4").value(4).name("any\n\n\nName4").build());
  }

  @Test
  void shouldMatchSnapshotInsidePrivateMethod(TestInfo testInfo) {
    matchInsidePrivate(testInfo);
  }

  @Test
  void shouldThrowSnapshotMatchException(TestInfo testInfo) {
    Expect expect = Expect.of(snapshotVerifier, testInfo.getTestMethod().get());
    assertThrows(
        SnapshotMatchException.class,
        () -> expect.toMatchSnapshot(FakeObject.builder().id("anyId5").value(6).name("anyName5").build()),
        "Error on: \n"
            + "au.com.origin.snapshots.SnapshotIntegrationTest.shouldThrowSnapshotMatchException=[");
  }

  @Test
  void shouldSnapshotUsingSerializerClass(TestInfo testInfo) {
    Expect expect = Expect.of(snapshotVerifier, testInfo.getTestMethod().get());
    expect.serializer(UppercaseToStringSerializer.class).toMatchSnapshot("Hello World");
  }

  @Test
  void shouldSnapshotUsingSerializerPropertyName(TestInfo testInfo) {
    Expect expect = Expect.of(snapshotVerifier, testInfo.getTestMethod().get());
    expect.serializer("lowercase").toMatchSnapshot("Hello World");
  }


  private void matchInsidePrivate(TestInfo testInfo) {
    Expect expect = Expect.of(snapshotVerifier, testInfo.getTestMethod().get());
    expect
        .toMatchSnapshot(FakeObject.builder().id("anyPrivate").value(5).name("anyPrivate").build());
  }

}
