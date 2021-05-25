package au.com.origin.snapshots;

import au.com.origin.snapshots.config.BaseSnapshotConfig;
import au.com.origin.snapshots.serializers.UppercaseToStringSerializer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class UseCustomSerializerTest {

  private static final SnapshotConfig DEFAULT_CONFIG = new BaseSnapshotConfig();

  @BeforeAll
  static void beforeEach() {
    SnapshotUtils.copyTestSnapshots();
  }

  @DisplayName("@SnapshotSerializer on a method via new instance")
  @Test
  public void canUseSnapshotSerializerAnnotationAtMethodLevelUsingNewInstance(TestInfo testInfo) {
    SnapshotVerifier snapshotVerifier = new SnapshotVerifier(DEFAULT_CONFIG, testInfo.getTestClass().get());
    Expect expect = Expect.of(snapshotVerifier, testInfo.getTestMethod().get());
    expect
        .serializer(new UppercaseToStringSerializer())
        .toMatchSnapshot(new TestObject());
    snapshotVerifier.validateSnapshots();
  }

  @DisplayName("@SnapshotSerializer on a method via class name")
  @Test
  public void canUseSnapshotSerializerAnnotationAtMethodLevelUsingClassName(TestInfo testInfo) {
    SnapshotVerifier snapshotVerifier = new SnapshotVerifier(DEFAULT_CONFIG, testInfo.getTestClass().get());
    Expect expect = Expect.of(snapshotVerifier, testInfo.getTestMethod().get());
    expect
        .serializer(new UppercaseToStringSerializer())
        .toMatchSnapshot(new TestObject());
    snapshotVerifier.validateSnapshots();
  }

  private class TestObject {
    @Override
    public String toString() {
      return "This is a snapshot of the toString() method";
    }
  }
}
