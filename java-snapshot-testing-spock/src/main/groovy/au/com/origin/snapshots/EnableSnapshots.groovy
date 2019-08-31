package au.com.origin.snapshots

import java.lang.annotation.*
import org.spockframework.runtime.extension.ExtensionAnnotation

@Retention(RetentionPolicy.RUNTIME)
@Target([ElementType.TYPE, ElementType.METHOD])
@ExtensionAnnotation(SnapshotExtension)
@interface EnableSnapshots {}
