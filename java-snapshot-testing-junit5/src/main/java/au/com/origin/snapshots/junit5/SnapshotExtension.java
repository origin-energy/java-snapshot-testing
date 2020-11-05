package au.com.origin.snapshots.junit5;

import au.com.origin.snapshots.SnapshotConfig;
import au.com.origin.snapshots.SnapshotConfigInjector;
import au.com.origin.snapshots.SnapshotMatcher;
import au.com.origin.snapshots.exceptions.SnapshotMatchException;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.engine.descriptor.ClassTestDescriptor;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class SnapshotExtension implements AfterAllCallback, BeforeAllCallback, BeforeEachCallback, SnapshotConfigInjector {

    @Override
    public void beforeAll(ExtensionContext context) {
        // don't fail if a test is run alone from the IDE for example
        boolean failOnOrphans = shouldFailOnOrphans(context);
        Class<?> testClass = context.getTestClass()
                .orElseThrow(() -> new SnapshotMatchException("Unable to locate Test class"));
        SnapshotMatcher.start(getSnapshotConfig(), failOnOrphans, testClass);
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
        return new JUnit5SnapshotConfig();
    }


    /**
     * FIXME This is a hack until I find the correct way to determine if a test run is individual or as part of a class
     * @param context
     * @return
     */
    private boolean shouldFailOnOrphans(ExtensionContext context) {
        try {
            Field field = context.getClass().getSuperclass().getDeclaredField("testDescriptor");
            field.setAccessible(true);
            ClassTestDescriptor classTestDescriptor = (ClassTestDescriptor) field.get(context);
            return classTestDescriptor.getChildren().size() > 1;
        } catch (NoSuchFieldException | IllegalAccessException | ClassCastException e) {
            e.printStackTrace();
            System.err.println(
                    "FAILED: (Java Snapshot Testing) Unable to get JUnit5 ClassTestDescriptor!\n" +
                    "Ensure you are using Junit5 >= 5.3.2\n"+
                    "This may be due to JUnit5 changing their private api as we use reflection to access it\n" +
                    "Log a support ticket https://github.com/origin-energy/java-snapshot-testing/issues and supply your JUnit5 version\n" +
                    "Setting failOnOrphans=true as this is the safest option." +
                    "This means that running a test alone (say from the IDE) will fail the snapshot, you need to run the entire class.");
            return true;
        }
    }
}
