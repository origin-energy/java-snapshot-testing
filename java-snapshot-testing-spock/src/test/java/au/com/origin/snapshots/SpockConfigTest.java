package au.com.origin.snapshots;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

public class SpockConfigTest {

  @AfterEach
  public void beforeEach() {
    System.setProperty(SnapshotConfig.JVM_UPDATE_SNAPSHOTS_PARAMETER, "");
  }

  @Test
  public void shouldNotUpdateSnapshotNotPassed() {
    SnapshotConfig snapshotConfig = new SpockConfig("spec");
    assertThat(snapshotConfig.shouldUpdateSnapshot()).isFalse();
  }

  @Test
  public void shouldUpdateSnapshotTrue() {
    System.setProperty(SnapshotConfig.JVM_UPDATE_SNAPSHOTS_PARAMETER, "true");
    SnapshotConfig snapshotConfig = new SpockConfig("spec");
    assertThat(snapshotConfig.shouldUpdateSnapshot()).isTrue();
  }

  @Test
  public void shouldUpdateSnapshotFalse() {
    System.setProperty(SnapshotConfig.JVM_UPDATE_SNAPSHOTS_PARAMETER, "false");
    SnapshotConfig snapshotConfig = new SpockConfig("spec");
    assertThat(snapshotConfig.shouldUpdateSnapshot()).isFalse();
  }
}
