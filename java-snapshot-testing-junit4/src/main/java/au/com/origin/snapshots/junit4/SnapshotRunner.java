package au.com.origin.snapshots.junit4;

import au.com.origin.snapshots.*;
import org.junit.Test;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;

import java.util.Arrays;
import java.util.List;

/**
 * Loosely based on: https://stackoverflow.com/questions/27745691/how-to-combine-runwith-with-runwithparameterized-class
 * <p>
 * This implementation is restricted because it appears to not support Parameterized tests
 */
public class SnapshotRunner extends BlockJUnit4ClassRunner implements SnapshotConfigInjector {

  SnapshotVerifier snapshotVerifier;

  public SnapshotRunner(Class<?> klass) throws InitializationError {
    super(klass);
  }

  @Override
  protected Statement methodInvoker(FrameworkMethod method, Object test) {
    boolean isSnapshotText = Arrays.asList(method.getMethod().getParameterTypes()).contains(Expect.class);
    boolean isTest = method.getMethod().isAnnotationPresent(Test.class);
    if (isTest && isSnapshotText) {
      return new Statement() {
        @Override
        public void evaluate() throws Throwable {
          method.invokeExplosively(test, new Expect(snapshotVerifier, method.getMethod()));
        }
      };
    } else {
      return super.methodInvoker(method, test);
    }
  }

  @Override
  public void run(RunNotifier notifier) {
    // We don't want the orphan check to happen when the user runs a single test in their IDE
    boolean failOnOrphans = getDescription().getChildren().size() > 1;
    snapshotVerifier = new SnapshotVerifier(getSnapshotConfig(), getTestClass().getJavaClass(), failOnOrphans);
    super.run(notifier);
    snapshotVerifier.validateSnapshots();
  }

  @Override
  protected void validateTestMethods(List<Throwable> errors) {
    // Disable as it checks for zero arguments
  }

  @Override
  public SnapshotConfig getSnapshotConfig() {
    return new PropertyResolvingSnapshotConfig();
  }

}

