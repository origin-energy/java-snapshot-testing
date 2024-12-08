package au.com.origin.snapshots;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

import org.junit.jupiter.api.Test;

class SnapshotTest {

  @Test
  public void shouldParseSnapshot() {
    Snapshot snapshot =
        Snapshot.parse(
            Snapshot.builder().name("au.com.origin.snapshots.Test").body("body").build().raw());
    assertThat(snapshot.getIdentifier()).isEqualTo("au.com.origin.snapshots.Test");
    assertThat(snapshot.getName()).isEqualTo("au.com.origin.snapshots.Test");
    assertThat(snapshot.getHeader()).isEmpty();
    assertThat(snapshot.getScenario()).isBlank();
    assertThat(snapshot.getBody()).isEqualTo("body");
  }

  @Test
  public void shouldParseSnapshotWithHeaders() {
    SnapshotHeader header = new SnapshotHeader();
    header.put("header1", "value1");
    Snapshot snapshot =
        Snapshot.parse(
            Snapshot.builder()
                .name("au.com.origin.snapshots.Test")
                .header(header)
                .body("body")
                .build()
                .raw());
    assertThat(snapshot.getIdentifier()).isEqualTo("au.com.origin.snapshots.Test");
    assertThat(snapshot.getName()).isEqualTo("au.com.origin.snapshots.Test");
    assertThat(snapshot.getHeader()).containsExactly(entry("header1", "value1"));
    assertThat(snapshot.getScenario()).isBlank();
    assertThat(snapshot.getBody()).isEqualTo("body");
  }

  @Test
  public void shouldParseSnapshotWithScenario() {
    Snapshot snapshot =
        Snapshot.parse(
            Snapshot.builder()
                .name("au.com.origin.snapshots.Test")
                .scenario("scenario")
                .body("body")
                .build()
                .raw());
    assertThat(snapshot.getIdentifier()).isEqualTo("au.com.origin.snapshots.Test[scenario]");
    assertThat(snapshot.getName()).isEqualTo("au.com.origin.snapshots.Test");
    assertThat(snapshot.getHeader()).isEmpty();
    assertThat(snapshot.getScenario()).isEqualTo("scenario");
    assertThat(snapshot.getBody()).isEqualTo("body");
  }

  @Test
  public void shouldParseSnapshotWithScenarioAndHeaders() {
    SnapshotHeader header = new SnapshotHeader();
    header.put("header1", "value1");
    Snapshot snapshot =
        Snapshot.parse(
            Snapshot.builder()
                .name("au.com.origin.snapshots.Test")
                .scenario("scenario")
                .header(header)
                .body("body")
                .build()
                .raw());
    assertThat(snapshot.getIdentifier()).isEqualTo("au.com.origin.snapshots.Test[scenario]");
    assertThat(snapshot.getName()).isEqualTo("au.com.origin.snapshots.Test");
    assertThat(snapshot.getHeader()).containsExactly(entry("header1", "value1"));
    assertThat(snapshot.getScenario()).isEqualTo("scenario");
    assertThat(snapshot.getBody()).isEqualTo("body");
  }

  @Test
  public void
      shouldParseSnapshotWithScenarioAndBodyWithSomethingSimilarToAnScenarioToConfuseRegex() {
    Snapshot snapshot =
        Snapshot.parse(
            Snapshot.builder()
                .name("au.com.origin.snapshots.Test")
                .scenario("scenario")
                .body("[xxx]=yyy")
                .build()
                .raw());
    System.out.println(snapshot.raw());
    assertThat(snapshot.getIdentifier()).isEqualTo("au.com.origin.snapshots.Test[scenario]");
    assertThat(snapshot.getName()).isEqualTo("au.com.origin.snapshots.Test");
    assertThat(snapshot.getHeader()).isEmpty();
    assertThat(snapshot.getScenario()).isEqualTo("scenario");
    assertThat(snapshot.getBody()).isEqualTo("[xxx]=yyy");
  }
}
