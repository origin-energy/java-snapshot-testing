package au.com.origin.snapshots.annotations;

import au.com.origin.snapshots.SnapshotConfig;

import java.lang.annotation.*;

@Documented
@Target({ElementType.TYPE})
@Inherited
@Retention(RetentionPolicy.RUNTIME)
public @interface UseSnapshotConfig {
  Class<? extends SnapshotConfig> value();
}
