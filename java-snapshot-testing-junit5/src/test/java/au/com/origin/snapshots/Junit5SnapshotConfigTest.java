package au.com.origin.snapshots;

import au.com.origin.snapshots.junit5.JUnit5SnapshotConfig;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class Junit5SnapshotConfigTest {

  @AfterEach
  public void beforeEach() {
    System.clearProperty(SnapshotConfig.JVM_UPDATE_SNAPSHOTS_PARAMETER);
  }

  @Test
  public void shouldNotUpdateSnapshotNotPassed() {
    SnapshotConfig snapshotConfig = new JUnit5SnapshotConfig();
    assertThat(snapshotConfig.updateSnapshot().isPresent()).isFalse();
  }

  @Test
  public void shouldUpdateSnapshotPassed() {
    System.setProperty(SnapshotConfig.JVM_UPDATE_SNAPSHOTS_PARAMETER, "example");
    SnapshotConfig snapshotConfig = new JUnit5SnapshotConfig();
    assertThat(snapshotConfig.updateSnapshot().get()).isEqualTo("example");
  }

}
