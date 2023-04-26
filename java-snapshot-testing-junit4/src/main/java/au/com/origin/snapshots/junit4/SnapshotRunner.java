package au.com.origin.snapshots.junit4;

import au.com.origin.snapshots.SnapshotVerifier;
import au.com.origin.snapshots.logging.LoggingHelper;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;

/**
 * Runner to enable java-snapshot-testing
 *
 * <p>If you are already using @RunWith for something else such as @RunWith(Parameterized.class) use
 * these Rules instead.
 *
 * @see SnapshotClassRule
 * @see SnapshotRule
 *     <pre>{@code
 * {@literal @}ClassRule
 * public static SnapshotClassRule snapshotClassRule = new SnapshotClassRule();
 *
 * {@literal @}Rule
 * public SnapshotRule snapshotRule = new SnapshotRule(snapshotClassRule);
 *
 * private Expect expect;
 * }</pre>
 *     Loosely based on:
 *     https://stackoverflow.com/questions/27745691/how-to-combine-runwith-with-runwithparameterized-class
 */
@Slf4j
public class SnapshotRunner extends BlockJUnit4ClassRunner {

  SnapshotVerifier snapshotVerifier;

  private SharedSnapshotHelpers helpers = new SharedSnapshotHelpers();

  public SnapshotRunner(Class<?> klass) throws InitializationError {
    super(klass);
  }

  @Override
  protected Statement methodInvoker(FrameworkMethod method, Object test) {
    boolean isTest = method.getMethod().isAnnotationPresent(Test.class);
    if (isTest) {
      helpers.injectExpectInstanceVariable(snapshotVerifier, method.getMethod(), test);
      boolean shouldInjectMethodArgument = helpers.hasExpectArgument(method);
      if (shouldInjectMethodArgument) {
        LoggingHelper.deprecatedV5(
            log,
            "Injecting 'Expect' via method a argument is no longer recommended. Consider using instance variable injection instead.");
        return helpers.injectExpectMethodArgument(snapshotVerifier, method, test);
      }
    }

    return super.methodInvoker(method, test);
  }

  @Override
  public void run(RunNotifier notifier) {
    snapshotVerifier = helpers.getSnapshotVerifier(getDescription());
    super.run(notifier);
    snapshotVerifier.validateSnapshots();
  }

  @Override
  protected void validateTestMethods(List<Throwable> errors) {
    // Disable as it checks for zero arguments
  }
}
