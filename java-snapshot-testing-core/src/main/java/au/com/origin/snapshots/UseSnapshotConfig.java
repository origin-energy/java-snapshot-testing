package au.com.origin.snapshots;

import java.lang.annotation.*;

@Documented
@Target(ElementType.TYPE)
@Inherited
@Retention(RetentionPolicy.RUNTIME)
public @interface UseSnapshotConfig {
    Class<? extends SnapshotConfig> value();
}
