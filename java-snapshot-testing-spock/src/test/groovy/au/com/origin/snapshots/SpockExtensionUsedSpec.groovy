package au.com.origin.snapshots

import au.com.origin.snapshots.spock.EnableSnapshots
import spock.lang.Specification

@EnableSnapshots
class SpockExtensionUsedSpec extends Specification {

    def "Should use extension"(Expect expect) {
        when:
        expect.toMatchSnapshot("Hello World")

        then:
        true
    }

    def "Should use extension again"(Expect expect) {
        when:
        expect.toMatchSnapshot("Hello World", "Hello World Again")

        then:
        true
    }

}
