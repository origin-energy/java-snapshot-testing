package au.com.origin.snapshots.junit5;

import au.com.origin.snapshots.SnapshotConfig;
import org.apache.commons.lang3.NotImplementedException;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class JUnit5Config implements SnapshotConfig {

  @Override
  public Class<?> getTestClass() {
    throw new NotImplementedException("You forgot to implement the JUnit5 SnapshotExtension");
  }

  @Override
  public Method getTestMethod(Class<?> testClass) {
    throw new NotImplementedException("You forgot to implement the JUnit5 SnapshotExtension");
  }

}
