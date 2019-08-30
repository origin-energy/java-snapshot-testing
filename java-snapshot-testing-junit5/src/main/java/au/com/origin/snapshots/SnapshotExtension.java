package au.com.origin.snapshots;

import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

public class SnapshotExtension implements AfterAllCallback, BeforeAllCallback {

    @Override
    public void beforeAll(ExtensionContext context) {
        Class<?> clazz = context.getTestClass()
                .orElseThrow(() -> new SnapshotMatchException("Unable to locate Test class"));
        SnapshotMatcher.start(new JUnit5Config(), clazz);
    }

    @Override
    public void afterAll(ExtensionContext context) throws Exception {
        SnapshotMatcher.validateSnapshots();
    }
}
