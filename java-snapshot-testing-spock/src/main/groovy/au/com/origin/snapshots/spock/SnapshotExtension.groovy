package au.com.origin.snapshots.spock

import au.com.origin.snapshots.SnapshotVerifier
import au.com.origin.snapshots.config.PropertyResolvingSnapshotConfig
import au.com.origin.snapshots.config.SnapshotConfig
import au.com.origin.snapshots.config.SnapshotConfigInjector
import org.spockframework.runtime.extension.AbstractAnnotationDrivenExtension
import org.spockframework.runtime.model.SpecInfo

class SnapshotExtension extends AbstractAnnotationDrivenExtension<EnableSnapshots> implements SnapshotConfigInjector {

    SnapshotVerifier snapshotVerifier;

    void visitSpecAnnotation(EnableSnapshots annotation, SpecInfo spec) {
        this.snapshotVerifier = new SnapshotVerifier(getSnapshotConfig(), spec.reflection, false)
    }

    void visitSpec(SpecInfo spec) {
        spec.allFeatures.featureMethod*.addInterceptor(new SnapshotMethodInterceptor(snapshotVerifier))
    }



    @Override
    SnapshotConfig getSnapshotConfig() {
        return new PropertyResolvingSnapshotConfig()
    }
}