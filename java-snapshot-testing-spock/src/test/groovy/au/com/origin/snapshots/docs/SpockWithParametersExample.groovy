package au.com.origin.snapshots.docs

import au.com.origin.snapshots.Expect
import au.com.origin.snapshots.spock.EnableSnapshots
import spock.lang.Specification

@EnableSnapshots
class SpockWithParametersExample extends Specification {

    // Note: "expect" will get injected later so can remain begin as null in the data-table
    def 'Convert #scenario to uppercase'(def scenario, def value, Expect expect) {
        when: 'I convert to uppercase'
        String result = value.toUpperCase();
        then: 'Should convert letters to uppercase'
        // Check you snapshot against your output using a unique scenario
        expect.scenario(scenario).toMatchSnapshot(result)
        where:
        scenario | value | expect
        'letter' | 'a'   | null
        'number' | '1'   | null
    }
}