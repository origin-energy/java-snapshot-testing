package au.com.origin.snapshots.docs

import au.com.origin.snapshots.annotations.SnapshotName
import au.com.origin.snapshots.spock.EnableSnapshots
import spock.lang.Specification

import au.com.origin.snapshots.Expect;

// Ensure you enable snapshot testing support
@EnableSnapshots
class SpockExample extends Specification {

    // Option 1: inject Expect as an instance variable
    private Expect expect

    // With spock tests you should always use @SnapshotName - otherwise they become coupled to test order
    @SnapshotName("should_use_extension")
    def "Should use extension"() {
        when:
        expect.toMatchSnapshot("Hello World")

        then:
        true
    }

    @SnapshotName("should_use_extension_as_mehod_argument")
    // Option 2: inject Expect into the method signature
    def "Should use extension as method argument"(Expect expect) {
        when:
        expect.toMatchSnapshot("Hello World")

        then:
        true
    }
}
