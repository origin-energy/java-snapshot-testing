package io.github.jsonSnapshot;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static io.github.jsonSnapshot.SnapshotMatcher.*;
import static io.github.jsonSnapshot.SnapshotUtils.extractArgs;

@ExtendWith(MockitoExtension.class)
public class SnapshotUtilsTest {

    @Mock
    private FakeObject fakeObject;

    @BeforeAll
    public static void beforeAll() {
        start();
    }

    @AfterAll
    public static void afterAll() {
        validateSnapshots();
    }


    @Test
    public void shouldExtractArgsFromFakeMethod() {

        fakeObject.fakeMethod("test1", 1L, Arrays.asList("listTest1"));
        fakeObject.fakeMethod("test2", 2L, Arrays.asList("listTest1", "listTest2"));

        Object fakeMethod = extractArgs(fakeObject, "fakeMethod", new SnapshotCaptor(String.class), new SnapshotCaptor(Long.class), new SnapshotCaptor(List.class));
        expect(fakeMethod).toMatchSnapshot();
    }

    @Test
    public void shouldExtractArgsFromFakeMethodWithComplexObject() {
        FakeObject fake = new FakeObject.FakeObjectBuilder().id("idMock").name("nameMock").build();

        //With Ignore
        fakeObject.fakeMethodWithComplexFakeObject(fake);
        Object fakeMethodWithComplexObjectWithIgnore = extractArgs(fakeObject, "fakeMethodWithComplexFakeObject", new SnapshotCaptor(FakeObject.class, "name"));

        Mockito.reset(fakeObject);

        // Without Ignore
        fakeObject.fakeMethodWithComplexFakeObject(fake);
        Object fakeMethodWithComplexObjectWithoutIgnore = extractArgs(fakeObject, "fakeMethodWithComplexFakeObject", new SnapshotCaptor(FakeObject.class));

        expect(fakeMethodWithComplexObjectWithIgnore, fakeMethodWithComplexObjectWithoutIgnore).toMatchSnapshot();
    }

    @Test
    public void shouldExtractArgsFromFakeMethodWithComplexFakeObject() {

        FakeObject fake = new FakeObject.FakeObjectBuilder().id("idMock").name("nameMock").build();

        //With Ignore
        fakeObject.fakeMethodWithComplexObject(fake);
        Object fakeMethodWithComplexObjectWithIgnore = extractArgs(fakeObject, "fakeMethodWithComplexObject", new SnapshotCaptor(Object.class, FakeObject.class, "name"));

        Mockito.reset(fakeObject);

        // Without Ignore
        fakeObject.fakeMethodWithComplexObject(fake);
        Object fakeMethodWithComplexObjectWithoutIgnore = extractArgs(fakeObject, "fakeMethodWithComplexObject", new SnapshotCaptor(Object.class, FakeObject.class));

        expect(fakeMethodWithComplexObjectWithIgnore, fakeMethodWithComplexObjectWithoutIgnore).toMatchSnapshot();
    }
}
