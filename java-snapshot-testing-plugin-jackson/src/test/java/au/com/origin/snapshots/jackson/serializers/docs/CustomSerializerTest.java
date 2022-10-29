package au.com.origin.snapshots.jackson.serializers.docs;

import au.com.origin.snapshots.Expect;
import au.com.origin.snapshots.PropertyResolvingSnapshotConfig;
import au.com.origin.snapshots.SnapshotVerifier;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

import java.time.Instant;

public class CustomSerializerTest {

  @Test
  public void test1(TestInfo testInfo) {
    SnapshotVerifier snapshotVerifier = new SnapshotVerifier(new PropertyResolvingSnapshotConfig(), testInfo.getTestClass().get(), false);

    Expect expect = Expect.of(snapshotVerifier, testInfo.getTestMethod().get());
    expect
        .serializer(HibernateSnapshotSerializer.class)
        .toMatchSnapshot(new BaseEntity(1L, Instant.now(), Instant.now(), "This should render"));

    snapshotVerifier.validateSnapshots();
  }
}
