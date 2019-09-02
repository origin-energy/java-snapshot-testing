package au.com.origin.snapshots.junit5;

import au.com.origin.snapshots.SnapshotConfig;
import au.com.origin.snapshots.SnapshotConfigInjector;
import au.com.origin.snapshots.SnapshotMatchException;
import au.com.origin.snapshots.SnapshotMatcher;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.lang.reflect.Method;

public class SnapshotExtension implements AfterAllCallback, BeforeAllCallback, BeforeEachCallback, SnapshotConfigInjector {

    @Override
    public void beforeAll(ExtensionContext context) {
        Class<?> testClass = context.getTestClass()
                .orElseThrow(() -> new SnapshotMatchException("Unable to locate Test class"));
        SnapshotMatcher.start(getSnapshotConfig(), testClass);
    }

    @Override
    public void afterAll(ExtensionContext context) {
        SnapshotMatcher.validateSnapshots();
    }

    @Override
    public void beforeEach(ExtensionContext context) {
        Method method = context.getTestMethod()
                .orElseThrow(() -> new SnapshotMatchException("Unable to locate Test class"));
        SnapshotMatcher.setTestMethod(method);
    }

    @Override
    public SnapshotConfig getSnapshotConfig() {
        return new JUnit5Config();
    }
}
