package au.com.origin.snapshots;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

public class SnapshotRule implements TestRule {
    @Override
    public Statement apply(Statement base, Description description) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                SnapshotMatcher.start(new JUnit4Config(), description.getTestClass());
                try {
                    base.evaluate();
                } finally {
                    SnapshotMatcher.validateSnapshots();
                }
            }
        };
    }
}
