package au.com.origin.snapshots.junit4;

import au.com.origin.snapshots.exceptions.SnapshotExtensionException;
import lombok.RequiredArgsConstructor;
import org.junit.rules.MethodRule;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;

@RequiredArgsConstructor
public class SnapshotRule implements MethodRule {

    private final SnapshotClassRule snapshotClassRule;

    @Override
    public Statement apply(Statement base, FrameworkMethod method, Object test) {
        final SharedSnapshotHelpers helpers = snapshotClassRule.getHelpers();
        helpers.injectExpectInstanceVariable(
                snapshotClassRule.getSnapshotVerifier(),
                method.getMethod(),
                test);
        if (helpers.hasExpectArgument(method)) {
            throw new SnapshotExtensionException(
                    "Sorry, we don't support 'Expect' as a method argument for @Rule or @ClassRule. " +
                    "Please use an instance variable or @RunWith(SnapshotRunner.class) instead.");
        }

        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                base.evaluate();
            }
        };
    }
}
