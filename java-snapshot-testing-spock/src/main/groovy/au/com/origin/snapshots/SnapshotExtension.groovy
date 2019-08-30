package au.com.origin.snapshots

import org.spockframework.runtime.extension.AbstractAnnotationDrivenExtension
import org.spockframework.runtime.model.SpecInfo
import org.spockframework.runtime.model.FeatureInfo
import org.spockframework.runtime.model.MethodInfo

class SnapshotExtensionAnnotation extends AbstractAnnotationDrivenExtension<Time> {

    void visitSpecAnnotation(Time annotation, SpecInfo spec) {
        SnapshotMatcher.start(new SpockConfig())
    }

    void visitFeatureAnnotation(Time annotation, FeatureInfo feature) {
        timedFeatures << feature.name
    }

    void visitSpec(SpecInfo spec) {
        spec.addListener(new TimingRunListener(timeSpec, timedFeatures)
    }
}