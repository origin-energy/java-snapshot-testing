package au.com.origin.snapshots;

import au.com.origin.snapshots.junit5.SnapshotExtension;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static au.com.origin.snapshots.SnapshotMatcher.expect;

@ExtendWith({SnapshotExtension.class})
public class NestedClassTest {

    @Nested
    class NestedClass {

        @Test
        public void helloWorldTest() {
            expect("Hello World").toMatchSnapshot();
        }
    }
}
