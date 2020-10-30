package au.com.origin.snapshots

import au.com.origin.snapshots.spock.EnableSnapshots
import spock.lang.Specification

@EnableSnapshots
class SpockExtensionUsedSpec extends Specification {

    def "Should use extension"() {
        when:
        SnapshotMatcher.expect("Hello World").toMatchSnapshot()

        then:
        true
    }

    def "Should use extension again"() {
        when:
        SnapshotMatcher.expect("Hello World", "Hello World Again").toMatchSnapshot()

        then:
        true
    }

}
