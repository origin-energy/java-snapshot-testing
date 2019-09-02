package au.com.origin.snapshots.junit4;

import au.com.origin.snapshots.SnapshotMatcher;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

public class SnapshotRule implements TestRule {
    @Override
    public Statement apply(Statement base, Description description) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                SnapshotMatcher.setTestMethod(description.getTestClass().getMethod(description.getMethodName()));
                base.evaluate();
            }
        };
    }
}
