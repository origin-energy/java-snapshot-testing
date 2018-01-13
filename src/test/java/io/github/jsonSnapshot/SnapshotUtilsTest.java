package io.github.jsonSnapshot;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static io.github.jsonSnapshot.SnapshotMatcher.expect;
import static io.github.jsonSnapshot.SnapshotMatcher.start;
import static io.github.jsonSnapshot.SnapshotMatcher.validateSnapshots;

@RunWith(MockitoJUnitRunner.class)
public class SnapshotUtilsTest {

    @Mock
    private FakeObject fakeObject;

    @BeforeClass
    public static void beforeAll() {
        start();
    }

    @AfterClass
    public static void afterAll() {
        validateSnapshots();
    }


    @Test
    public void shouldExtractArgsFromMethod() {

        fakeObject.fakeMethod("test1", 1L, Arrays.asList("listTest1"));
        fakeObject.fakeMethod("test2", 2L, Arrays.asList("listTest1", "listTest2"));

        HashMap<?, ?> fakeMethodArgs = SnapshotUtils.extractArgs(fakeObject, "fakeMethod", String.class, Long.class, List.class);
        expect(fakeMethodArgs)
                .toMatchSnapshot();

    }

}
