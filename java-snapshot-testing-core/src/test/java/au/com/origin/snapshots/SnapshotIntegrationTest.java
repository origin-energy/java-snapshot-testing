package au.com.origin.snapshots;

import au.com.origin.snapshots.config.BaseSnapshotConfig;
import au.com.origin.snapshots.exceptions.SnapshotMatchException;
import au.com.origin.snapshots.serializers.ToStringSnapshotSerializer;
import au.com.origin.snapshots.serializers.UppercaseToStringSerializer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static au.com.origin.snapshots.SnapshotMatcher.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class SnapshotIntegrationTest {

  private static final SnapshotConfig DEFAULT_CONFIG = new BaseSnapshotConfig();

  @BeforeAll
  static void beforeAll() {
    SnapshotUtils.copyTestSnapshots();
    start(DEFAULT_CONFIG);
  }

  @AfterAll
  static void afterAll() {
    validateSnapshots();
  }

  @Test
  void shouldMatchSnapshotOne() {
    expect(FakeObject.builder().id("anyId1").value(1).name("anyName1").build()).toMatchSnapshot();
  }

  @Test
  void shouldMatchSnapshotTwo() {
    expect(FakeObject.builder().id("anyId2").value(2).name("anyName2").build()).toMatchSnapshot();
  }

  @Test
  void shouldMatchSnapshotThree() {
    expect(FakeObject.builder().id("anyId3").value(3).name("anyName3").build()).toMatchSnapshot();
  }

  @Test
  void shouldMatchSnapshotFour() {
    expect(FakeObject.builder().id("anyId4").value(4).name("any\n\n\nName4").build())
        .toMatchSnapshot();
  }

  @Test
  void shouldMatchSnapshotInsidePrivateMethod() {
    matchInsidePrivate();
  }

  @Test
  void shouldThrowSnapshotMatchException() {
    assertThrows(
        SnapshotMatchException.class,
        expect(FakeObject.builder().id("anyId5").value(6).name("anyName5").build())::toMatchSnapshot,
        "Error on: \n"
            + "au.com.origin.snapshots.SnapshotIntegrationTest.shouldThrowSnapshotMatchException=[");
  }

  @Test
  void shouldSnapshotUsingSerializerClass() {
    expect("Hello World").serializer(UppercaseToStringSerializer.class).toMatchSnapshot();
  }

  @Test
  void shouldSnapshotUsingSerializerPropertyName() {
    expect("Hello World").serializer("lowercase").toMatchSnapshot();
  }

  private void matchInsidePrivate() {
    expect(FakeObject.builder().id("anyPrivate").value(5).name("anyPrivate").build())
        .toMatchSnapshot();
  }

}
