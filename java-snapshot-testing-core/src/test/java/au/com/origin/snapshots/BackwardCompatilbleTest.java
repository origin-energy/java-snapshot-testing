package au.com.origin.snapshots;

import au.com.origin.snapshots.config.BaseSnapshotConfig;
import lombok.ToString;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static au.com.origin.snapshots.SnapshotUtils.extractArgs;

@ExtendWith(MockitoExtension.class)
public class BackwardCompatilbleTest {

  @Mock
  private FakeObject fakeObject;

  @Test // Snapshot any object
  public void shouldShowSnapshotExample(TestInfo testInfo) {
    SnapshotVerifier snapshotVerifier = new SnapshotVerifier(new BaseSnapshotConfig(), testInfo.getTestClass().get());
    Expect.of(snapshotVerifier, testInfo.getTestMethod().get()).toMatchSnapshot("<any type of object>");
    snapshotVerifier.validateSnapshots();
  }

  @Test // Snapshot arguments passed to mocked object (from Mockito library)
  public void shouldExtractArgsFromMethod(TestInfo testInfo) {
    SnapshotVerifier snapshotVerifier = new SnapshotVerifier(new BaseSnapshotConfig(), testInfo.getTestClass().get());

    fakeObject.fakeMethod("test1", 1L, Arrays.asList("listTest1"));
    fakeObject.fakeMethod("test2", 2L, Arrays.asList("listTest1", "listTest2"));

    Expect.of(snapshotVerifier, testInfo.getTestMethod().get())
        .toMatchSnapshot(extractArgs(
            fakeObject,
            "fakeMethod",
            new SnapshotCaptor(String.class),
            new SnapshotCaptor(Long.class),
            new SnapshotCaptor(List.class)));

    snapshotVerifier.validateSnapshots();
  }

  @Test // Snapshot arguments passed to mocked object support ignore of fields
  public void shouldExtractArgsFromFakeMethodWithComplexObject(TestInfo testInfo) {
    SnapshotVerifier snapshotVerifier = new SnapshotVerifier(new BaseSnapshotConfig(), testInfo.getTestClass().getClass());

    FakeObject fake = new FakeObject();
    fake.setId("idMock");
    fake.setName("nameMock");

    // With Ignore
    fakeObject.fakeMethodWithComplexObject(fake);
    Object fakeMethodWithComplexObjectWithIgnore =
        extractArgs(
            fakeObject,
            "fakeMethodWithComplexObject",
            new SnapshotCaptor(Object.class, FakeObject.class, "name"));

    Mockito.reset(fakeObject);

    // Without Ignore of fields
    fakeObject.fakeMethodWithComplexObject(fake);
    Object fakeMethodWithComplexObjectWithoutIgnore =
        extractArgs(
            fakeObject,
            "fakeMethodWithComplexObject",
            new SnapshotCaptor(Object.class, FakeObject.class));

    Expect.of(snapshotVerifier, testInfo.getTestMethod().get())
        .toMatchSnapshotLegacy(fakeMethodWithComplexObjectWithIgnore, fakeMethodWithComplexObjectWithoutIgnore);

    snapshotVerifier.validateSnapshots();
  }

  @ToString
  class FakeObject {

    private String id;

    private Integer value;

    private String name;

    void fakeMethod(String fakeName, Long fakeNumber, List<String> fakeList) {
    }

    void fakeMethodWithComplexObject(Object fakeObj) {
    }

    void setId(String id) {
      this.id = id;
    }

    void setName(String name) {
      this.name = name;
    }
  }
}
