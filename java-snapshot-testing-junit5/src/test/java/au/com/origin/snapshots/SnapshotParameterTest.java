package au.com.origin.snapshots;

import au.com.origin.snapshots.junit5.SnapshotExtension;
import au.com.origin.snapshots.jackson.serializers.JacksonSnapshotSerializer;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

@ExtendWith({SnapshotExtension.class})
class SnapshotParameterTest {

  private Expect expect;

  static Stream<Arguments> testData() {

    return Stream.of(
        Arguments.of("Scenario2", "test input 1"),
        Arguments.of("Scenario2", "test input 1"),
        Arguments.of("Scenario2", "test input 1"),
        Arguments.of("Scenario3", "test input 2"),
        Arguments.of("Scenario3", "test input 2")
    );

  }

  @ParameterizedTest
  @MethodSource("au.com.origin.snapshots.SnapshotParameterTest#testData")
  void shouldSupportParameterizedTest(String scenario, String testInput, Expect expect) {
    expect.toMatchSnapshot("Duplicates are OK");
    expect.toMatchSnapshot("Duplicates are OK");
    expect.scenario("Scenario1").toMatchSnapshot("Additional snapshots need to include a scenario");
    expect.serializer(JacksonSnapshotSerializer.class).scenario(scenario).toMatchSnapshot(testInput);
  }

  @ParameterizedTest
  @MethodSource("au.com.origin.snapshots.SnapshotParameterTest#testData")
  void shouldSupportParameterizedTestViaInstanceVariable(String scenario, String testInput) {
    this.expect.toMatchSnapshot("Duplicates are OK");
    this.expect.toMatchSnapshot("Duplicates are OK");
    this.expect.scenario("Scenario1").toMatchSnapshot("Additional snapshots need to include a scenario");
    this.expect.serializer(JacksonSnapshotSerializer.class).scenario(scenario).toMatchSnapshot(testInput);
  }
}
