package au.com.origin.snapshots.spock

import au.com.origin.snapshots.SnapshotConfig

import java.lang.reflect.Method

class SpockConfig implements SnapshotConfig {

    @Override
    String getOutputDir() {
        return "src/test/groovy/"
    }

    @Override
    Class<?> getTestClass() {
        throw new RuntimeException("You forgot to annotate your spec with @EnableSnapshots");
    }

    @Override
    Method getTestMethod(Class<?> testClass) {
        throw new RuntimeException("You forgot to annotate your spec with @EnableSnapshots");
    }
}