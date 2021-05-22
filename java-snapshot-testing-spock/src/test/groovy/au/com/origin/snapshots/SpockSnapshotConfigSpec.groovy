package au.com.origin.snapshots

import spock.lang.Specification

class SpockSnapshotConfigSpec extends Specification {

    def setupSpec() {
        System.clearProperty(SnapshotConfig.JVM_UPDATE_SNAPSHOTS_PARAMETER);
    }

    def "Should not update snapshot when not passed"() {
        when:
        def snapshotConfig = new PropertyResolvingSnapshotConfig()

        then:
        !snapshotConfig.updateSnapshot().isPresent()
    }

    def "Should update snapshot when true"() {
        given:
        System.setProperty(SnapshotConfig.JVM_UPDATE_SNAPSHOTS_PARAMETER, 'example')

        when:
        def snapshotConfig = new PropertyResolvingSnapshotConfig()

        then:
        snapshotConfig.updateSnapshot().get() == 'example'
    }

}
