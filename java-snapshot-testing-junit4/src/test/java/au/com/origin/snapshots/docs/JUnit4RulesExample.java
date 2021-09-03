package au.com.origin.snapshots.docs;

import au.com.origin.snapshots.Expect;
import au.com.origin.snapshots.annotations.SnapshotName;
import au.com.origin.snapshots.junit4.SnapshotClassRule;
import au.com.origin.snapshots.junit4.SnapshotRule;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

public class JUnit4RulesExample {

  @ClassRule
  public static SnapshotClassRule snapshotClassRule = new SnapshotClassRule();

  @Rule
  public SnapshotRule snapshotRule = new SnapshotRule(snapshotClassRule);

  private Expect expect;

  @SnapshotName("my first test")
  @Test
  public void myTest1() {
    // Verify your snapshot
    expect.toMatchSnapshot("Hello World");
  }
}
