package au.com.origin.snapshots.junit4;

import au.com.origin.snapshots.SnapshotConfig;
import au.com.origin.snapshots.SnapshotConfigInjector;
import au.com.origin.snapshots.SnapshotMatcher;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

public class SnapshotClassRule implements TestRule, SnapshotConfigInjector {
    @Override
    public Statement apply(Statement base, Description description) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                // don't fail if a test is run alone from the IDE for example
                boolean failOnOrphans = description.getChildren().size() > 1;
                SnapshotMatcher.start(getSnapshotConfig(), failOnOrphans, description.getTestClass());
                try {
                    base.evaluate();
                } finally {
                    SnapshotMatcher.validateSnapshots();
                }
            }
        };
    }

    @Override
    public SnapshotConfig getSnapshotConfig() {
        return new JUnit4SnapshotConfig();
    }
}
