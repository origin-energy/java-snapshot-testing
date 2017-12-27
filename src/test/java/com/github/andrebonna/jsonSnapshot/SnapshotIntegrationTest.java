package com.github.andrebonna.jsonSnapshot;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class SnapshotIntegrationTest {

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
}
