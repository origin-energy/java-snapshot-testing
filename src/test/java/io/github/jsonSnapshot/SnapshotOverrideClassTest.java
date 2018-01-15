package io.github.jsonSnapshot;

import org.junit.AfterClass;
import org.junit.BeforeClass;

import static io.github.jsonSnapshot.SnapshotMatcher.start;
import static io.github.jsonSnapshot.SnapshotMatcher.validateSnapshots;

public class SnapshotOverrideClassTest extends SnapshotSuperClassTest {

    @BeforeClass
    public static void beforeAll() {
        start();
    }

    @AfterClass
    public static void afterAll() {
        validateSnapshots();
    }

    @Override
    public String getName() {
        return "anyName";
    }
}
