package au.com.origin.snapshots;

import au.com.origin.snapshots.jackson.serializers.DeterministicJacksonSnapshotSerializer;
import au.com.origin.snapshots.jackson.serializers.JacksonSnapshotSerializer;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * These classes are likely defined in snapshot.properties as a string.
 * <p>
 * The clients IDE will not complain if they change so ensure they don't
 */
public class NoNameChangeTest {

  @Test
  public void serializersApiShouldNotChange() {
    assertThat(JacksonSnapshotSerializer.class.getName()).isEqualTo("au.com.origin.snapshots.jackson.serializers.JacksonSnapshotSerializer");
    assertThat(DeterministicJacksonSnapshotSerializer.class.getName()).isEqualTo("au.com.origin.snapshots.jackson.serializers.DeterministicJacksonSnapshotSerializer");
  }
}
