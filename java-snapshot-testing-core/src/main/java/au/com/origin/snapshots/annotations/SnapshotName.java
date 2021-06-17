package au.com.origin.snapshots.annotations;

import java.lang.annotation.*;

@Deprecated
@Documented
@Target({ElementType.METHOD})
@Inherited
@Retention(RetentionPolicy.RUNTIME)
public @interface SnapshotName {
  String value();
}
