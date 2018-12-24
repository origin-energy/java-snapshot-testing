package io.github.jsonSnapshot;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

import static io.github.jsonSnapshot.SnapshotMatcher.start;
import static io.github.jsonSnapshot.SnapshotMatcher.validateSnapshots;

public class SnapshotOverrideClassTest extends SnapshotSuperClassTest {

    @BeforeAll
    public static void beforeAll() {
        start();
    }

    @AfterAll
    public static void afterAll() {
        validateSnapshots();
    }

    @Override
    public String getName() {
        return "anyName";
    }
}
