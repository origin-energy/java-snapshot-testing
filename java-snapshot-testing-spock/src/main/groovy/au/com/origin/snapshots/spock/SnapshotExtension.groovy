package au.com.origin.snapshots.spock

import au.com.origin.snapshots.SnapshotConfig
import au.com.origin.snapshots.SnapshotConfigInjector
import au.com.origin.snapshots.SnapshotMatcher
import org.spockframework.runtime.extension.AbstractAnnotationDrivenExtension
import org.spockframework.runtime.model.SpecInfo

class SnapshotExtension extends AbstractAnnotationDrivenExtension<EnableSnapshots> implements SnapshotConfigInjector {

    void visitSpecAnnotation(EnableSnapshots annotation, SpecInfo spec) {
        SnapshotMatcher.start(new SpockConfig(), spec.reflection)
    }

    void visitSpec(SpecInfo spec) {
        spec.addListener(new SnapshotSpecListener())
    }

    @Override
    SnapshotConfig getSnapshotConfig() {
        return new SpockConfig()
    }
}