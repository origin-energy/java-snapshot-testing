package au.com.origin.snapshots.reporters;

import au.com.origin.snapshots.Snapshot;
import au.com.origin.snapshots.serializers.SerializerType;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.opentest4j.AssertionFailedError;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

class PlainTextSnapshotReporterTest {
  private static final PlainTextSnapshotReporter REPORTER = new PlainTextSnapshotReporter();

  @Test
  void shouldSupportAllFormats() {
    Assertions.assertThat(REPORTER.supportsFormat(SerializerType.TEXT.name())).isTrue();
    Assertions.assertThat(REPORTER.supportsFormat(SerializerType.JSON.name())).isTrue();

    Assertions.assertThat(REPORTER.supportsFormat("xml")).isTrue();
    Assertions.assertThat(REPORTER.supportsFormat("blah")).isTrue();
  }

  @Test
  void doReport() {
    Snapshot snap1 = Snapshot.builder()
            .name("snap1")
            .scenario("A")
            .body("[\nfoo\n]")
            .build();
    Snapshot snap2 = Snapshot.builder()
            .name("snap1")
            .scenario("A")
            .body("[\nbar\n]")
            .build();
    assertThatExceptionOfType(AssertionFailedError.class)
        .isThrownBy(() -> REPORTER.report(snap1, snap2))
        .withMessageContaining("expecting:")
        .withMessageContaining("[\"foo\"]")
        .withMessageContaining("but was:")
        .withMessageContaining("[\"bar\"]");
  }
}
