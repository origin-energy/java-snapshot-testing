package au.com.origin.snapshots

import spock.lang.*

class SpockConfigSpec extends Specification {

    def setupSpec() {
        System.setProperty(SnapshotConfig.JVM_UPDATE_SNAPSHOTS_PARAMETER, "")
    }

    def "Should not update snapshot when not passed"() {
        when:
        def snapshotConfig = new SpockConfig()

        then:
        !snapshotConfig.shouldUpdateSnapshot()
    }

    def "Should update snapshot when true"() {
        given:
        System.setProperty(SnapshotConfig.JVM_UPDATE_SNAPSHOTS_PARAMETER, "true")

        when:
        def snapshotConfig = new SpockConfig()

        then:
        snapshotConfig.shouldUpdateSnapshot()
    }

    void "Should update snapshot when false"() {
        given:
        System.setProperty(SnapshotConfig.JVM_UPDATE_SNAPSHOTS_PARAMETER, "false")

        when:
        def snapshotConfig = new SpockConfig()

        then:
        !snapshotConfig.shouldUpdateSnapshot()
    }
}
