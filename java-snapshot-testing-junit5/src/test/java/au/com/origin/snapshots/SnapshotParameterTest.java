package au.com.origin.snapshots;

import au.com.origin.snapshots.junit5.SnapshotExtension;
import au.com.origin.snapshots.serializers.JacksonSnapshotSerializer;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

@ExtendWith({SnapshotExtension.class})
class SnapshotParameterTest {

    @ParameterizedTest
    @MethodSource("au.com.origin.snapshots.SnapshotParameterTest#testData")
    void shouldSupportParameterizedTest(String scenario, String testInput) {
        SnapshotMatcher.expect("Duplicates are OK").toMatchSnapshot();
        SnapshotMatcher.expect("Duplicates are OK").toMatchSnapshot();
        SnapshotMatcher.expect("Additional snapshots need to include a scenario").scenario("Scenario1").toMatchSnapshot();
        SnapshotMatcher.expect(testInput).serializer(JacksonSnapshotSerializer.class).scenario(scenario).toMatchSnapshot();
    }

    static Stream<Arguments> testData() {

        return Stream.of(
                Arguments.of("Scenario2", "test input 1"),
                Arguments.of("Scenario2", "test input 1"),
                Arguments.of("Scenario2", "test input 1"),
                Arguments.of("Scenario3", "test input 2"),
                Arguments.of("Scenario3", "test input 2")
        );

    }
}
