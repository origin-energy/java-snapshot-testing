package au.com.origin.snapshots.reporters;

import au.com.origin.snapshots.comparators.PlainTextEqualsComparator;
import au.com.origin.snapshots.exceptions.SnapshotMatchException;
import org.assertj.core.api.Assertions;
import org.assertj.core.util.diff.DiffUtils;
import org.assertj.core.util.diff.Patch;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

class PlainTextSnapshotDiffReporterTest {
    private static final PlainTextSnapshotDiffReporter REPORTER = new PlainTextSnapshotDiffReporter();

    @Test
    void supportedComparators() {
        Assertions.assertThat(REPORTER.supportedComparators()).contains(PlainTextEqualsComparator.class);
    }

    @Test
    void doReport() {
        Patch<String> patch = DiffUtils.diff(Collections.singletonList("blah"), Collections.singletonList("bloo"));

        assertThatExceptionOfType(SnapshotMatchException.class)
                .isThrownBy(() -> REPORTER.doReport(patch, "bloo"))
                .withMessageContaining("expecting:")
                .withMessageContaining("[\"blah\"]")
                .withMessageContaining("but was:")
                .withMessageContaining("[\"bloo\"]");
    }
}
