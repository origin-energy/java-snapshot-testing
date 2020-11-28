package au.com.origin.snapshots

import au.com.origin.snapshots.exceptions.SnapshotExtensionException
import spock.lang.Ignore
import spock.lang.Specification

class SpockExtensionUnusedSpec extends Specification {

    @Ignore
    def "Should fail with exception"() {
        when:
        SnapshotMatcher.expect("Hello World").toMatchSnapshot()

        then:
        def error = thrown(SnapshotExtensionException.class)
        // FIXME getting different error on build server
        // "You can only call 'expect' once per test method. Try using array of arguments on a single 'expect' call"
        // error.message == 'Unable to locate snapshot - has SnapshotMatcher.start() been called?'
    }

}
