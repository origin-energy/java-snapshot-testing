package au.com.origin.snapshots.junit5;

import au.com.origin.snapshots.Expect;
import au.com.origin.snapshots.SnapshotVerifier;
import au.com.origin.snapshots.config.PropertyResolvingSnapshotConfig;
import au.com.origin.snapshots.config.SnapshotConfig;
import au.com.origin.snapshots.config.SnapshotConfigInjector;
import au.com.origin.snapshots.exceptions.SnapshotMatchException;
import au.com.origin.snapshots.logging.LoggingHelper;

import java.lang.reflect.Field;
import java.util.Arrays;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.extension.*;
import org.junit.jupiter.engine.descriptor.ClassBasedTestDescriptor;
import org.junit.jupiter.engine.descriptor.ClassTestDescriptor;

@Slf4j
public class SnapshotExtension
        implements AfterAllCallback,
        BeforeAllCallback,
        SnapshotConfigInjector,
        ParameterResolver,
        BeforeEachCallback {

    private SnapshotVerifier snapshotVerifier;

    @Override
    public void beforeAll(ExtensionContext context) {
        // don't fail if a test is run alone from the IDE for example
        boolean failOnOrphans = shouldFailOnOrphans();
        Class<?> testClass =
                context
                        .getTestClass()
                        .orElseThrow(() -> new SnapshotMatchException("Unable to locate Test class"));
        this.snapshotVerifier = new SnapshotVerifier(getSnapshotConfig(), testClass, failOnOrphans);
    }

    @Override
    public void afterAll(ExtensionContext context) {
        this.snapshotVerifier.validateSnapshots();
    }

    @Override
    public SnapshotConfig getSnapshotConfig() {
        return new PropertyResolvingSnapshotConfig();
    }

    private boolean shouldFailOnOrphans() {
        return false;
    }

    @Override
    public boolean supportsParameter(
            ParameterContext parameterContext, ExtensionContext extensionContext)
            throws ParameterResolutionException {
        boolean supports = parameterContext.getParameter().getType() == Expect.class;
        if (supports) {
            LoggingHelper.deprecatedV5(
                    log,
                    "Injecting 'Expect' via method a argument is no longer recommended. Consider using instance variable injection instead.");
        }
        return supports;
    }

    @Override
    public Object resolveParameter(
            ParameterContext parameterContext, ExtensionContext extensionContext)
            throws ParameterResolutionException {
        return new Expect(
                snapshotVerifier,
                extensionContext
                        .getTestMethod()
                        .orElseThrow(() -> new RuntimeException("getTestMethod() is missing")));
    }

    @Override
    public void beforeEach(ExtensionContext context) {
        if (context.getTestInstance().isPresent() && context.getTestMethod().isPresent()) {
            Arrays.stream(context.getTestClass().get().getDeclaredFields())
                    .filter(it -> it.getType() == Expect.class)
                    .findFirst()
                    .ifPresent(
                            field -> {
                                Expect expect = Expect.of(snapshotVerifier, context.getTestMethod().get());
                                field.setAccessible(true);
                                try {
                                    field.set(context.getTestInstance().get(), expect);
                                } catch (IllegalAccessException e) {
                                    throw new RuntimeException(e);
                                }
                            });
        }
    }
}
