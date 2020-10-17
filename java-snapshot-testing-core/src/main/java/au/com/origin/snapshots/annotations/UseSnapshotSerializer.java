package au.com.origin.snapshots.annotations;

import au.com.origin.snapshots.serializers.SnapshotSerializer;

import java.lang.annotation.*;

@Documented
@Target({ElementType.METHOD, ElementType.TYPE})
@Inherited
@Retention(RetentionPolicy.RUNTIME)
public @interface UseSnapshotSerializer {
    Class<? extends SnapshotSerializer> value();
}
