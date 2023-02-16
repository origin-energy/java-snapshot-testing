package au.com.origin.snapshots;

import org.junit.jupiter.api.Test;

public class ExtendsSnapshotTestBaseTest extends SnapshotTestBase {
  @Test
  void test() {
    snapshot("OK");
  }
}
