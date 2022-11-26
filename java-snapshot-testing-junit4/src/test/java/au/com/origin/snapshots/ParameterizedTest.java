package au.com.origin.snapshots;

import au.com.origin.snapshots.junit4.SnapshotClassRule;
import au.com.origin.snapshots.junit4.SnapshotRule;
import java.util.Arrays;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class ParameterizedTest {

  @ClassRule public static SnapshotClassRule snapshotClassRule = new SnapshotClassRule();
  @Rule public SnapshotRule snapshotRule = new SnapshotRule(snapshotClassRule);
  @Rule public TestName testName = new TestName();

  private Expect expect;

  @Parameters(name = "letter is {0}")
  public static Iterable<Object[]> data() {
    return Arrays.asList(new Object[][] {{"a"}, {"b"}, {"c"}});
  }

  private String input;

  public ParameterizedTest(String input) {
    this.input = input;
  }

  @Test
  public void test() {
    expect.scenario(input).toMatchSnapshot(input);
  }
}
