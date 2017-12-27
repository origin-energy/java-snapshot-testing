package com.github.andrebonna.jsonSnapshot;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.hamcrest.CoreMatchers.startsWith;

public class SnapshotIntegrationTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @BeforeClass
    public static void beforeAll() {
        SnapshotMatcher.start();
    }

    @AfterClass
    public static void afterAll() {
        SnapshotMatcher.validateSnapshots();
    }

    @Test
    public void shouldMatchSnapshotOne() {
        SnapshotMatcher.expect(FakeObject.builder().id("anyId1").value(1).name("anyName1").build()).toMatchSnapshot();
    }

    @Test
    public void shouldMatchSnapshotTwo() {
        SnapshotMatcher.expect(FakeObject.builder().id("anyId2").value(2).name("anyName2").build()).toMatchSnapshot();
    }

    @Test
    public void shouldMatchSnapshotThree() {
        SnapshotMatcher.expect(FakeObject.builder().id("anyId3").value(3).name("anyName3").build()).toMatchSnapshot();
    }

    @Test
    public void shouldMatchSnapshotFour() {
        SnapshotMatcher.expect(FakeObject.builder().id("anyId4").value(4).name("any\n\n\nName4").build()).toMatchSnapshot();
    }

    @Test
    public void shouldMatchSnapshotFive() {
        expectedException.expect(SnapshotMatchException.class);
        expectedException.expectMessage(startsWith("Error on: \n" +
                "com.github.andrebonna.jsonSnapshot.SnapshotIntegrationTest| with |shouldMatchSnapshotFive=["));
        SnapshotMatcher.expect(FakeObject.builder().id("anyId5").value(6).name("anyName5").build()).toMatchSnapshot();
    }
}
