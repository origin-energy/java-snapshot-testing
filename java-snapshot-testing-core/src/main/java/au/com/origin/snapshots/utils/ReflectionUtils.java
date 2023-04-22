package au.com.origin.snapshots.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Optional;
import java.util.function.Predicate;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ReflectionUtils {

  /**
   * Find {@link Field} by given predicate.
   *
   * <p>Invoke the given predicate on all fields in the target class, going up the class hierarchy
   * to get all declared fields.
   *
   * @param clazz the target class to analyze
   * @param predicate the predicate
   * @return the field or empty optional
   */
  public static Optional<Field> findFieldByPredicate(
      final Class<?> clazz, final Predicate<Field> predicate) {
    Class<?> targetClass = clazz;

    do {
      final Field[] fields = targetClass.getDeclaredFields();
      for (final Field field : fields) {
        if (!predicate.test(field)) {
          continue;
        }
        return Optional.of(field);
      }
      targetClass = targetClass.getSuperclass();
    } while (targetClass != null && targetClass != Object.class);

    return Optional.empty();
  }

  public static void makeAccessible(final Field field) {
    if ((!Modifier.isPublic(field.getModifiers())
            || !Modifier.isPublic(field.getDeclaringClass().getModifiers())
            || Modifier.isFinal(field.getModifiers()))
        && !field.isAccessible()) {
      field.setAccessible(true);
    }
  }
}
