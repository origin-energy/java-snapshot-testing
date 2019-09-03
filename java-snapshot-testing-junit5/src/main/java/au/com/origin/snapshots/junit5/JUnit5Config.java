package au.com.origin.snapshots.junit5;

import au.com.origin.snapshots.SnapshotConfig;

import java.lang.reflect.Method;

public class JUnit5Config implements SnapshotConfig {

  @Override
  public Class<?> getTestClass() {
    throw new RuntimeException("You forgot to implement the JUnit5 SnapshotExtension");
  }

  @Override
  public Method getTestMethod(Class<?> testClass) {
    throw new RuntimeException("You forgot to implement the JUnit5 SnapshotExtension");
  }

}
