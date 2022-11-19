package au.com.origin.snapshots;

import lombok.Getter;
import lombok.Setter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

public abstract class SnapshotSuperClassTest {

  @Getter @Setter static SnapshotVerifier snapshotVerifier;

  public abstract String getName();

  @Test
  void shouldMatchSnapshotOne(TestInfo testInfo) {
    Expect.of(snapshotVerifier, testInfo.getTestMethod().get()).toMatchSnapshot(getName());
  }
}
