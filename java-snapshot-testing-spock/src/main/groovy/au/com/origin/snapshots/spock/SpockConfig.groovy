package au.com.origin.snapshots.spock

import au.com.origin.snapshots.SnapshotConfig
import org.apache.commons.lang3.NotImplementedException

import java.lang.reflect.Method

class SpockConfig implements SnapshotConfig {

    @Override
    String getTestSrcDir() {
        return "src/test/groovy/"
    }

    @Override
    Class<?> getTestClass() {
        throw new NotImplementedException("You forgot to annotate your spec with @EnableSnapshots");
    }

    @Override
    Method getTestMethod(Class<?> testClass) {
        throw new NotImplementedException("You forgot to annotate your spec with @EnableSnapshots");
    }
}