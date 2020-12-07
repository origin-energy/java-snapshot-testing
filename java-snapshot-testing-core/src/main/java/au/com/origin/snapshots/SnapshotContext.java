package au.com.origin.snapshots;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.lang.reflect.Method;
import java.nio.file.Path;

@Builder
@Data
@RequiredArgsConstructor
public class SnapshotContext {
    private final Class<?> testClass;
    private final Method testMethod;
    private final String snapshotFilePath;
    private final String snapshotName;
    private final String existingSnapshot;
    private final String incomingSnapshot;
}
