package io.github.jsonSnapshot;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static io.github.jsonSnapshot.SnapshotMatcher.*;
import static org.hamcrest.CoreMatchers.startsWith;

public class SnapshotIntegrationTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @BeforeClass
    public static void beforeAll() {
        start();
    }

    @AfterClass
    public static void afterAll() {
        validateSnapshots();
    }

    @Test
    public void shouldMatchSnapshotOne() {
        expect(FakeObject.builder().id("anyId1").value(1).name("anyName1").build()).toMatchSnapshot();
    }

    @Test
    public void shouldMatchSnapshotTwo() {
        expect(FakeObject.builder().id("anyId2").value(2).name("anyName2").build()).toMatchSnapshot();
    }

    @Test
    public void shouldMatchSnapshotThree() {
        expect(FakeObject.builder().id("anyId3").value(3).name("anyName3").build()).toMatchSnapshot();
    }

    @Test
    public void shouldMatchSnapshotFour() {
        expect(FakeObject.builder().id("anyId4").value(4).name("any\n\n\nName4").build()).toMatchSnapshot();
    }

    @Test
    public void shouldMatchSnapshotFive() {
        expectedException.expect(SnapshotMatchException.class);
        expectedException.expectMessage(startsWith("Error on: \n" +
                "io.github.jsonSnapshot.SnapshotIntegrationTest| with |shouldMatchSnapshotFive=["));
        expect(FakeObject.builder().id("anyId5").value(6).name("anyName5").build()).toMatchSnapshot();
    }
}
