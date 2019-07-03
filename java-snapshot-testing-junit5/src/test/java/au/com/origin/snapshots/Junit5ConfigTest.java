package au.com.origin.snapshots;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class Junit5ConfigTest {

  @AfterEach
  public void beforeEach() {
    System.setProperty(SnapshotConfig.JVM_UPDATE_SNAPSHOTS_PARAMETER, "");
  }

  @Test
  public void shouldNotUpdateSnapshotNotPassed() {
    SnapshotConfig snapshotConfig = new JUnit5Config();
    assertThat(snapshotConfig.shouldUpdateSnapshot()).isFalse();
  }

  @Test
  public void shouldUpdateSnapshotTrue() {
    System.setProperty(SnapshotConfig.JVM_UPDATE_SNAPSHOTS_PARAMETER, "true");
    SnapshotConfig snapshotConfig = new JUnit5Config();
    assertThat(snapshotConfig.shouldUpdateSnapshot()).isTrue();
  }

  @Test
  public void shouldUpdateSnapshotFalse() {
    System.setProperty(SnapshotConfig.JVM_UPDATE_SNAPSHOTS_PARAMETER, "false");
    SnapshotConfig snapshotConfig = new JUnit5Config();
    assertThat(snapshotConfig.shouldUpdateSnapshot()).isFalse();
  }
}
