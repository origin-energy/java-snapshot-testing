package au.com.origin.snapshots.junit4;

import au.com.origin.snapshots.SnapshotVerifier;
import lombok.Getter;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

public class SnapshotClassRule implements TestRule {

  @Getter private SnapshotVerifier snapshotVerifier;

  @Getter private SharedSnapshotHelpers helpers = new SharedSnapshotHelpers();

  @Override
  public Statement apply(Statement base, Description description) {
    return new Statement() {
      @Override
      public void evaluate() throws Throwable {
        snapshotVerifier = helpers.getSnapshotVerifier(description);
        try {
          base.evaluate();
        } finally {
          snapshotVerifier.validateSnapshots();
        }
      }
    };
  }
}
