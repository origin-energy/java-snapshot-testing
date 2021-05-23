package au.com.origin.snapshots;

import au.com.origin.snapshots.comparators.PlainTextEqualsComparator;
import au.com.origin.snapshots.reporters.PlainTextSnapshotReporter;
import au.com.origin.snapshots.serializers.*;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * These classes are likely defined in snapshot.properties as a string.
 *
 * The clients IDE will not complain if they change so ensure they don't
 */
public class NoNameChangeTest {

  @Test
  public void serializersApiShouldNotChange() {
    assertThat(JacksonSnapshotSerializer.class.getName()).isEqualTo("au.com.origin.snapshots.serializers.JacksonSnapshotSerializer");
    assertThat(DeterministicJacksonSnapshotSerializer.class.getName()).isEqualTo("au.com.origin.snapshots.serializers.DeterministicJacksonSnapshotSerializer");
  }
}
