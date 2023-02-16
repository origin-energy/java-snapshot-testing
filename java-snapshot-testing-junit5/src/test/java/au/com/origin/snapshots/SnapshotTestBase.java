package au.com.origin.snapshots;

import au.com.origin.snapshots.junit5.SnapshotExtension;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith({SnapshotExtension.class})
public class SnapshotTestBase {
  protected Expect expect;

  protected void snapshot(Object value) {
    expect.toMatchSnapshot(value);
  }
}
