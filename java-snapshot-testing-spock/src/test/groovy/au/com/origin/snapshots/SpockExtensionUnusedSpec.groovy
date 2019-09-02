package au.com.origin.snapshots

import spock.lang.Specification

class SpockExtensionUnusedSpec extends Specification {

    def "Should fail with exception"() {
        when:
        SnapshotMatcher.expect("Hello Wolrd").toMatchSnapshot()

        then:
        def error = thrown(SnapshotMatchException.class)
        // FIXME getting different error on build server
        // "You can only call 'expect' once per test method. Try using array of arguments on a single 'expect' call"
        // error.message == 'Unable to locate snapshot - has SnapshotMatcher.start() been called?'
    }

}
