package au.com.origin.snapshots

import org.junit.runner.RunWith
import org.spockframework.runtime.Sputnik
import spock.lang.Specification

@RunWith(Sputnik.class)
class SpecificationBase extends Specification {
    Expect expect;
}
