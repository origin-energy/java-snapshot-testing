package au.com.origin.snapshots

import au.com.origin.snapshots.annotations.SnapshotName
import au.com.origin.snapshots.spock.EnableSnapshots

@EnableSnapshots
class TestBaseSpec extends SpecificationBase {

    @SnapshotName("Should use extension")
    def "Should use extension"() {
        when:
        expect.toMatchSnapshot("Hello World")

        then:
        true
    }

}
