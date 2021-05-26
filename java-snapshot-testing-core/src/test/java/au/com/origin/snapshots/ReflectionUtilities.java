package au.com.origin.snapshots;

import au.com.origin.snapshots.exceptions.SnapshotMatchException;

import java.lang.reflect.Method;
import java.util.Optional;
import java.util.stream.Stream;

public class ReflectionUtilities {

  // FIXME consider guava reflection instead
  public static Method getMethod(Class<?> clazz, String methodName) {
    try {
      return Stream.of(clazz.getDeclaredMethods())
          .filter(method -> method.getName().equals(methodName))
          .findFirst()
          .orElseThrow(() -> new NoSuchMethodException("Not Found"));
    } catch (NoSuchMethodException e) {
      return Optional.ofNullable(clazz.getSuperclass())
          .map(superclass -> getMethod(superclass, methodName))
          .orElseThrow(
              () ->
                  new SnapshotMatchException(
                      "Could not find method "
                          + methodName
                          + " on class "
                          + clazz
                          + "\nPlease annotate your test method with @Test and make it without any parameters!"));
    }
  }
}
