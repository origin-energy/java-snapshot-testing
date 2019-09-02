package au.com.origin.snapshots.junit4;

import au.com.origin.snapshots.SnapshotConfig;
import org.apache.commons.lang3.NotImplementedException;

import java.lang.reflect.Method;

public class JUnit4Config implements SnapshotConfig {

  @Override
  public Class<?> getTestClass() {
    throw new NotImplementedException("You forgot to implement the @ClassRule for SnapshotClassRule");
  }

  @Override
  public Method getTestMethod(Class<?> testClass) {
    throw new NotImplementedException("You forgot to implement the @Rule for SnapshotRule");
  }

}
