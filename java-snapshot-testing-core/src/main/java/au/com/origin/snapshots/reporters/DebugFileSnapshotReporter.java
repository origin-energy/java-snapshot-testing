package au.com.origin.snapshots.reporters;

import au.com.origin.snapshots.SnapshotContext;
import lombok.SneakyThrows;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.stream.Collectors;

public class DebugFileSnapshotReporter implements SnapshotReporter {
    @Override
    public boolean supportsFormat(String outputFormat) {
        return true;
    }

    @SneakyThrows
    @Override
    public void reportFailure(SnapshotContext context) {
        Files.write(
                getDebugFile(context.getSnapshotFilePath()),
                context.getIncomingSnapshot().trim().getBytes(),
                StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE, StandardOpenOption.CREATE
        );
    }

    @SneakyThrows
    @Override
    public void reportSuccess(SnapshotContext context) {
        Path debugFile = getDebugFile(context.getSnapshotFilePath());
        if (Files.exists(debugFile)) {
            Files.lines(debugFile)
                    .filter(it -> it.startsWith(context.getSnapshotName()))
                    .findAny()
                    .ifPresent(it -> {
                        try {
                            Files.deleteIfExists(debugFile);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
        }
    }

    private Path getDebugFile(String filename) {
        return Paths.get(filename + ".debug");
    }

}
