package au.com.origin.snapshots.spock

import au.com.origin.snapshots.PropertyResolvingSnapshotConfig
import au.com.origin.snapshots.SnapshotConfig
import au.com.origin.snapshots.SnapshotConfigInjector
import au.com.origin.snapshots.SnapshotVerifier
import org.spockframework.runtime.extension.AbstractAnnotationDrivenExtension
import org.spockframework.runtime.model.SpecInfo

class SnapshotExtension extends AbstractAnnotationDrivenExtension<EnableSnapshots> implements SnapshotConfigInjector {

    SnapshotVerifier snapshotVerifier;

    void visitSpecAnnotation(EnableSnapshots annotation, SpecInfo spec) {
        this.snapshotVerifier = new SnapshotVerifier(getSnapshotConfig(), spec.reflection, false)
    }

    void visitSpec(SpecInfo spec) {
        def snapshotMethodInterceptor = new SnapshotMethodInterceptor(snapshotVerifier)
        spec.allFeatures.featureMethod*.addInterceptor(snapshotMethodInterceptor)
        spec.addCleanupSpecInterceptor(snapshotMethodInterceptor)
    }

    @Override
    SnapshotConfig getSnapshotConfig() {
        return new PropertyResolvingSnapshotConfig()
    }
}