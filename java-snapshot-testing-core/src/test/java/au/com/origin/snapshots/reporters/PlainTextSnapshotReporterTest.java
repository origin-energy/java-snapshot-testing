package au.com.origin.snapshots.reporters;

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
        assertThatExceptionOfType(AssertionFailedError.class)
                .isThrownBy(() -> REPORTER.report("snap1", "blah", "bloo"))
                .withMessageContaining("expecting:")
                .withMessageContaining("[\"blah\"]")
                .withMessageContaining("but was:")
                .withMessageContaining("[\"bloo\"]");
    }
}
