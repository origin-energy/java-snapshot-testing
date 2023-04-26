package au.com.origin.snapshots

import au.com.origin.snapshots.annotations.SnapshotName
import au.com.origin.snapshots.spock.EnableSnapshots
import spock.lang.Specification
import spock.lang.Unroll

@EnableSnapshots
class SpockExtensionUsedSpec extends Specification {

    Expect expect

    @SnapshotName("Should use extension")
    def "Should use extension"(Expect expect) {
        when:
        expect.toMatchSnapshot("Hello World")

        then:
        true
    }

    @SnapshotName("Should use extension again")
    def "Should use extension again"(Expect expect) {
        when:
        expect.toMatchSnapshot("Hello World")

        then:
        true
    }

    @SnapshotName("Should use extension via instance variable")
    def "Should use extension via instance variable"() {
        when:
        expect.toMatchSnapshot("Hello World")

        then:
        true
    }

    @SnapshotName("DataTable example 1")
    @Unroll
    def 'DataTable example 1: #letter'(def letter) {
        given: 'I use an @Unroll function'
        String result = letter.toUpperCase()

        when: 'I snapshot the letter'
        expect.scenario("letter $letter").toMatchSnapshot(result)

        then:
        true

        where:
        [letter] << [['A'],['B'],['C']]
    }


    @SnapshotName("DataTable example 2")
    def 'DataTable example 2: #scenario to uppercase'() {
        when: 'I convert to uppercase'
        String result = value.toUpperCase();
        then: 'Should convert letters to uppercase'
        // Check you snapshot against your output using a unique scenario
        expect.scenario(scenario).toMatchSnapshot(result)
        where:
        scenario | value
        'letter' | 'a'
        'number' | '1'
    }

    @SnapshotName("Can run a non snapshot test")
    def "Can run a non snapshot test"() {
        when:
        def isTrue = true

        then:
        isTrue
    }

}
