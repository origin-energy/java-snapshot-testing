package au.com.origin.snapshots.junit4;

import au.com.origin.snapshots.SnapshotConfig;

import java.lang.reflect.Method;

public class JUnit4SnapshotConfig implements SnapshotConfig {

  @Override
  public Class<?> getTestClass() {
    throw new RuntimeException("You forgot to implement the @ClassRule for SnapshotClassRule");
  }

  @Override
  public Method getTestMethod(Class<?> testClass) {
    throw new RuntimeException("You forgot to implement the @Rule for SnapshotRule");
  }

}
