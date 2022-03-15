package au.com.origin.snapshots;

import au.com.origin.snapshots.config.BaseSnapshotConfig;
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
class SnapshotUtilsTest {

  @Mock
  private FakeObject fakeObject;

  @Test
  void shouldExtractArgsFromFakeMethod(TestInfo testInfo) {
    fakeObject.fakeMethod("test1", 1L, Arrays.asList("listTest1"));
    fakeObject.fakeMethod("test2", 2L, Arrays.asList("listTest1", "listTest2"));

    SnapshotVerifier snapshotVerifier = new SnapshotVerifier(new BaseSnapshotConfig(), testInfo.getTestClass().get());
    Expect expect = Expect.of(snapshotVerifier, testInfo.getTestMethod().get());
    expect
        .toMatchSnapshot(extractArgs(
            fakeObject,
            "fakeMethod",
            new SnapshotCaptor(String.class),
            new SnapshotCaptor(Long.class),
            new SnapshotCaptor(List.class)));
    snapshotVerifier.validateSnapshots();
  }

  @Test
  void shouldExtractArgsFromFakeMethodWithComplexObject(TestInfo testInfo) {
    FakeObject fake = new FakeObject.FakeObjectBuilder().id("idMock").name("nameMock").build();

    // With Ignore
    fakeObject.fakeMethodWithComplexFakeObject(fake);
    Object fakeMethodWithComplexObjectWithIgnore =
        extractArgs(
            fakeObject,
            "fakeMethodWithComplexFakeObject",
            new SnapshotCaptor(FakeObject.class, "name"));

    SnapshotVerifier snapshotVerifier = new SnapshotVerifier(new BaseSnapshotConfig(), testInfo.getTestClass().get());
    Expect expect = Expect.of(snapshotVerifier, testInfo.getTestMethod().get());
    expect.toMatchSnapshot(fakeMethodWithComplexObjectWithIgnore);
    snapshotVerifier.validateSnapshots();
  }

  @Test
  void shouldExtractArgsFromFakeMethodWithComplexFakeObject(TestInfo testInfo) {

    FakeObject fake = new FakeObject.FakeObjectBuilder().id("idMock").name("nameMock").build();

    // With Ignore
    fakeObject.fakeMethodWithComplexObject(fake);
    Object fakeMethodWithComplexObjectWithIgnore =
        extractArgs(
            fakeObject,
            "fakeMethodWithComplexObject",
            new SnapshotCaptor(Object.class, FakeObject.class, "name"));

    SnapshotVerifier snapshotVerifier = new SnapshotVerifier(new BaseSnapshotConfig(), testInfo.getTestClass().get());
    Expect expect = Expect.of(snapshotVerifier, testInfo.getTestMethod().get());
    expect
        .toMatchSnapshot(fakeMethodWithComplexObjectWithIgnore);
    snapshotVerifier.validateSnapshots();
  }
}
