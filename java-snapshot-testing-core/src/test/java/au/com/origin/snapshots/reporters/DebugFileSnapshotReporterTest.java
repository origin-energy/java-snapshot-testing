package au.com.origin.snapshots.reporters;

import au.com.origin.snapshots.SnapshotContext;
import au.com.origin.snapshots.serializers.SerializerType;
import lombok.SneakyThrows;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class DebugFileSnapshotReporterTest {
    private static final DebugFileSnapshotReporter REPORTER = new DebugFileSnapshotReporter();
    private static final Path DEBUG_SNAPSHOT = Paths.get("src/test/java/au/com/origin/snapshots/reporters/tmp.snap.debug");

    @SneakyThrows
    @AfterEach
    public void afterAll() {
        Files.deleteIfExists(DEBUG_SNAPSHOT);
    }

    @Test
    void shouldSupportAllFormats() {
        Assertions.assertThat(REPORTER.supportsFormat(SerializerType.TEXT.name())).isTrue();
        Assertions.assertThat(REPORTER.supportsFormat(SerializerType.JSON.name())).isTrue();
        Assertions.assertThat(REPORTER.supportsFormat("xml")).isTrue();
        Assertions.assertThat(REPORTER.supportsFormat("blah")).isTrue();
    }

    @SneakyThrows
    @Test
    void doReport() {
        SnapshotContext context = SnapshotContext.builder()
                .snapshotFilePath("src/test/java/au/com/origin/snapshots/reporters/tmp.snap")
                .existingSnapshot("foo")
                .incomingSnapshot("bar")
                .build();
        REPORTER.reportFailure(context);
        assertThat(Files.exists(DEBUG_SNAPSHOT)).isTrue();
        assertThat(Files.lines(DEBUG_SNAPSHOT).collect(Collectors.joining("\n"))).isEqualTo("bar");
    }
}
