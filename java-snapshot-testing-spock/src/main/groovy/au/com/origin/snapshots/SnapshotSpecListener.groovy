package au.com.origin.snapshots

import org.spockframework.runtime.AbstractRunListener;
import org.spockframework.runtime.model.*;

class SnapshotSpecListener extends AbstractRunListener {

    void beforeFeature(FeatureInfo feature) {
        SnapshotMatcher.testMethod = feature.featureMethod.reflection
    }

}
