package au.com.origin.snapshots;

import java.lang.reflect.Method;
import java.util.stream.Stream;


public class JUnit4Config implements SnapshotConfig {

  @Override
  public StackTraceElement findStacktraceElement() {
    StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
    int elementsToSkip = 1; // Start after stackTrace
    while (SnapshotMatcher.class
        .getName()
        .equals(stackTraceElements[elementsToSkip].getClassName())) {
      elementsToSkip++;
    }

    return Stream.of(stackTraceElements)
        .skip(elementsToSkip)
        .filter(
            stackTraceElement ->
                hasTestAnnotation(
                    ReflectionUtilities.getMethod(
                        getClassForName(stackTraceElement.getClassName()),
                        stackTraceElement.getMethodName())))
        .findFirst()
        .orElseThrow(
            () ->
                new SnapshotMatchException(
                    "Could not locate a method with one of supported test annotations"));
  }

  protected boolean hasTestAnnotation(Method method) {
    return
    // Junit 4
    method.isAnnotationPresent(org.junit.Test.class)
        || method.isAnnotationPresent(org.junit.BeforeClass.class);
  }

  private Class<?> getClassForName(String className) {
    try {
      return Class.forName(className);
    } catch (ClassNotFoundException e) {
      throw new IllegalArgumentException(e);
    }
  }
}
