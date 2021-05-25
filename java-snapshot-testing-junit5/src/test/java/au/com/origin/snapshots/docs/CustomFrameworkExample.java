package au.com.origin.snapshots.docs;

import au.com.origin.snapshots.Expect;
import au.com.origin.snapshots.PropertyResolvingSnapshotConfig;
import au.com.origin.snapshots.SnapshotVerifier;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

// Notice we aren't using any framework extensions
public class CustomFrameworkExample {

  private static SnapshotVerifier snapshotVerifier;

  @BeforeAll
  static void beforeAll() {
    snapshotVerifier = new SnapshotVerifier(new PropertyResolvingSnapshotConfig(), CustomFrameworkExample.class);
  }

  @AfterAll
  static void afterAll() {
    snapshotVerifier.validateSnapshots();
  }

  @Test
  void shouldMatchSnapshotOne(TestInfo testInfo) {
    Expect expect = Expect.of(snapshotVerifier, testInfo.getTestMethod().get());
    expect.toMatchSnapshot("Hello World");
  }

}