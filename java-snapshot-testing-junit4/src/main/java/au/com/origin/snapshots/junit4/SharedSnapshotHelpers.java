package au.com.origin.snapshots.junit4;

import au.com.origin.snapshots.*;
import org.junit.runner.Description;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;

import java.lang.reflect.Method;
import java.util.Arrays;

class SharedSnapshotHelpers implements SnapshotConfigInjector {

    public void injectExpectInstanceVariable(
            SnapshotVerifier snapshotVerifier,
            Method testMethod,
            Object testInstance) {
        Arrays.stream(testInstance.getClass().getDeclaredFields())
                .filter(it -> it.getType() == Expect.class)
                .findFirst()
                .ifPresent(field -> {
                    Expect expect = Expect.of(snapshotVerifier, testMethod);
                    field.setAccessible(true);
                    try {
                        field.set(testInstance, expect);
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    public Statement injectExpectMethodArgument(SnapshotVerifier snapshotVerifier, FrameworkMethod method, Object test) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                method.invokeExplosively(test, new Expect(snapshotVerifier, method.getMethod()));
            }
        };
    }

    public boolean hasExpectArgument(FrameworkMethod method) {
        return Arrays.asList(method.getMethod().getParameterTypes()).contains(Expect.class);
    }

    @Override
    public SnapshotConfig getSnapshotConfig() {
        return new PropertyResolvingSnapshotConfig();
    }

    public SnapshotVerifier getSnapshotVerifier(Description description) {
        // We don't want the orphan check to happen when the user runs a single test in their IDE
        boolean failOnOrphans = description.getChildren().size() > 1;

        return new SnapshotVerifier(getSnapshotConfig(), description.getTestClass(), failOnOrphans);
    }
}
