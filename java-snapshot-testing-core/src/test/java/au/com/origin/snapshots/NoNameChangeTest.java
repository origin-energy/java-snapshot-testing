package au.com.origin.snapshots;

import au.com.origin.snapshots.comparators.PlainTextEqualsComparator;
import au.com.origin.snapshots.reporters.PlainTextSnapshotReporter;
import au.com.origin.snapshots.serializers.Base64SnapshotSerializer;
import au.com.origin.snapshots.serializers.SerializerType;
import au.com.origin.snapshots.serializers.ToStringSnapshotSerializer;
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
    assertThat(Base64SnapshotSerializer.class.getName()).isEqualTo("au.com.origin.snapshots.serializers.Base64SnapshotSerializer");
    assertThat(ToStringSnapshotSerializer.class.getName()).isEqualTo("au.com.origin.snapshots.serializers.ToStringSnapshotSerializer");
    assertThat(SerializerType.class.getName()).isEqualTo("au.com.origin.snapshots.serializers.SerializerType");
  }

  @Test
  public void reportersApiShouldNotChange() {
    assertThat(PlainTextSnapshotReporter.class.getName()).isEqualTo("au.com.origin.snapshots.reporters.PlainTextSnapshotReporter");
  }

  @Test
  public void comparatorsApiShouldNotChange() {
    assertThat(PlainTextEqualsComparator.class.getName()).isEqualTo("au.com.origin.snapshots.comparators.PlainTextEqualsComparator");
  }

}
