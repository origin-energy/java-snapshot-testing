package au.com.origin.snapshots;

import static au.com.origin.snapshots.SnapshotMatcher.*;
import static au.com.origin.snapshots.SnapshotUtils.extractArgs;

import java.util.Arrays;
import java.util.List;

import au.com.origin.snapshots.config.BaseSnapshotConfig;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SnapshotUtilsTest {

  @Mock private FakeObject fakeObject;

  @BeforeAll
  static void beforeAll() {
    start(new BaseSnapshotConfig());
  }

  @AfterAll
  static void afterAll() {
    validateSnapshots();
  }

  @Test
  void shouldExtractArgsFromFakeMethod() {

    fakeObject.fakeMethod("test1", 1L, Arrays.asList("listTest1"));
    fakeObject.fakeMethod("test2", 2L, Arrays.asList("listTest1", "listTest2"));

    Object fakeMethod =
        extractArgs(
            fakeObject,
            "fakeMethod",
            new SnapshotCaptor(String.class),
            new SnapshotCaptor(Long.class),
            new SnapshotCaptor(List.class));
    expect(fakeMethod).toMatchSnapshot();
  }

  @Test
  void shouldExtractArgsFromFakeMethodWithComplexObject() {
    FakeObject fake = new FakeObject.FakeObjectBuilder().id("idMock").name("nameMock").build();

    // With Ignore
    fakeObject.fakeMethodWithComplexFakeObject(fake);
    Object fakeMethodWithComplexObjectWithIgnore =
        extractArgs(
            fakeObject,
            "fakeMethodWithComplexFakeObject",
            new SnapshotCaptor(FakeObject.class, "name"));

    Mockito.reset(fakeObject);

    // Without Ignore
    fakeObject.fakeMethodWithComplexFakeObject(fake);
    Object fakeMethodWithComplexObjectWithoutIgnore =
        extractArgs(
            fakeObject, "fakeMethodWithComplexFakeObject", new SnapshotCaptor(FakeObject.class));

    expect(fakeMethodWithComplexObjectWithIgnore, fakeMethodWithComplexObjectWithoutIgnore)
        .toMatchSnapshot();
  }

  @Test
  void shouldExtractArgsFromFakeMethodWithComplexFakeObject() {

    FakeObject fake = new FakeObject.FakeObjectBuilder().id("idMock").name("nameMock").build();

    // With Ignore
    fakeObject.fakeMethodWithComplexObject(fake);
    Object fakeMethodWithComplexObjectWithIgnore =
        extractArgs(
            fakeObject,
            "fakeMethodWithComplexObject",
            new SnapshotCaptor(Object.class, FakeObject.class, "name"));

    Mockito.reset(fakeObject);

    // Without Ignore
    fakeObject.fakeMethodWithComplexObject(fake);
    Object fakeMethodWithComplexObjectWithoutIgnore =
        extractArgs(
            fakeObject,
            "fakeMethodWithComplexObject",
            new SnapshotCaptor(Object.class, FakeObject.class));

    expect(fakeMethodWithComplexObjectWithIgnore, fakeMethodWithComplexObjectWithoutIgnore)
        .toMatchSnapshot();
  }
}
