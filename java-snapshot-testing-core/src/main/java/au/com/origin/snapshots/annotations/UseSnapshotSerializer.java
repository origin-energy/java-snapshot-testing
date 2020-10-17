package au.com.origin.snapshots.annotations;

import au.com.origin.snapshots.serializers.SnapshotSerializer;

import java.lang.annotation.*;

@Documented
@Target({ElementType.METHOD})
@Inherited
@Retention(RetentionPolicy.RUNTIME)
public @interface UseSnapshotSerializer {
    Class<? extends SnapshotSerializer> value();
}
