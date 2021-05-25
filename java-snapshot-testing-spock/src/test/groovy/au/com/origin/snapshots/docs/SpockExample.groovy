package au.com.origin.snapshots.docs

import au.com.origin.snapshots.spock.EnableSnapshots
import spock.lang.Specification

import au.com.origin.snapshots.Expect;

// Ensure you enable snapshot testing support
@EnableSnapshots
class SpockExample extends Specification {
    def "Should use extension"(Expect expect) {
        when:
        expect.toMatchSnapshot("Hello World")

        then:
        true
    }
}
