package au.com.origin.snapshots

import spock.lang.Specification

@EnableSnapshots
class SpockExtensionUsedSpec extends Specification {

    def "Should use extension"() {
        when:
        SnapshotMatcher.expect("Hello Wolrd").toMatchSnapshot()

        then:
        true
    }

    def "Should use extension again"() {
        when:
        SnapshotMatcher.expect("Hello Wolrd Again").toMatchSnapshot()

        then:
        true
    }

}
