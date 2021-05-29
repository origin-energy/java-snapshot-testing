package au.com.origin.snapshots.docs

import au.com.origin.snapshots.Expect
import au.com.origin.snapshots.annotations.SnapshotName
import au.com.origin.snapshots.spock.EnableSnapshots
import spock.lang.Specification

@EnableSnapshots
class SpockWithParametersExample extends Specification {

    private Expect expect

    @SnapshotName("convert_to_uppercase")
    def 'Convert #scenario to uppercase'() {
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
}