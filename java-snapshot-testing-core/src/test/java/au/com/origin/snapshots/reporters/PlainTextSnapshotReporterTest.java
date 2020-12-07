package au.com.origin.snapshots.reporters;

import au.com.origin.snapshots.SnapshotContext;
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
        SnapshotContext context = SnapshotContext.builder()
                .existingSnapshot("foo")
                .incomingSnapshot("bar")
                .build();
        assertThatExceptionOfType(AssertionFailedError.class)
                .isThrownBy(() -> REPORTER.reportFailure(context))
                .withMessageContaining("expecting:")
                .withMessageContaining("[\"foo\"]")
                .withMessageContaining("but was:")
                .withMessageContaining("[\"bar\"]");
    }
}
