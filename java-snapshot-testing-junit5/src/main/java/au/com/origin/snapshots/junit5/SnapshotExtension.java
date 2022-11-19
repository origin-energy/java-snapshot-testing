package au.com.origin.snapshots.junit5;

import au.com.origin.snapshots.Expect;
import au.com.origin.snapshots.SnapshotVerifier;
import au.com.origin.snapshots.config.PropertyResolvingSnapshotConfig;
import au.com.origin.snapshots.config.SnapshotConfig;
import au.com.origin.snapshots.config.SnapshotConfigInjector;
import au.com.origin.snapshots.exceptions.SnapshotMatchException;
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
    boolean failOnOrphans = shouldFailOnOrphans(context);
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

  /**
   * FIXME This is a hack until I find the correct way to determine if a test run is individual or
   * as part of a class
   *
   * @param context
   * @return
   */
  private boolean shouldFailOnOrphans(ExtensionContext context) {
    try {
      Field field = context.getClass().getSuperclass().getDeclaredField("testDescriptor");
      field.setAccessible(true);
      Object testDescriptor = field.get(context);
      if (testDescriptor instanceof ClassTestDescriptor) { // Junit 5.3.2
        ClassTestDescriptor classTestDescriptor = (ClassTestDescriptor) testDescriptor;
        return classTestDescriptor.getChildren().size() > 1;
      } else if (testDescriptor instanceof ClassBasedTestDescriptor) { // Junit 5.7.2
        ClassBasedTestDescriptor classTestDescriptor = (ClassBasedTestDescriptor) testDescriptor;
        return classTestDescriptor.getChildren().size() > 1;
      }
    } catch (Exception e) {
      log.error(
          "FAILED: (Java Snapshot Testing) Unable to get JUnit5 ClassTestDescriptor or ClassBasedTestDescriptor!\n"
              + "Ensure you are using Junit5 >= 5.3.2\n"
              + "This may be due to JUnit5 changing their private api as we use reflection to access it\n"
              + "Log a support ticket https://github.com/origin-energy/java-snapshot-testing/issues and supply your JUnit5 version\n"
              + "Setting failOnOrphans=true as this is the safest option."
              + "This means that running a test alone (say from the IDE) will fail the snapshot, you need to run the entire class.",
          e);
    }
    return true;
  }

  @Override
  public boolean supportsParameter(
      ParameterContext parameterContext, ExtensionContext extensionContext)
      throws ParameterResolutionException {
    return parameterContext.getParameter().getType() == Expect.class;
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
