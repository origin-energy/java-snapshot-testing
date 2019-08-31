package au.com.origin.snapshots

import org.spockframework.runtime.extension.AbstractAnnotationDrivenExtension
import org.spockframework.runtime.model.SpecInfo

class SnapshotExtension extends AbstractAnnotationDrivenExtension<EnableSnapshots> {

    void visitSpecAnnotation(EnableSnapshots annotation, SpecInfo spec) {
        SnapshotMatcher.start(new SpockConfig(), spec.reflection)
    }

    void visitSpec(SpecInfo spec) {
        spec.addListener(new SnapshotSpecListener())
    }
}