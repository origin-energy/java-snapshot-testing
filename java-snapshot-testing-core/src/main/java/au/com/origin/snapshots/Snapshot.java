package au.com.origin.snapshots;

import au.com.origin.snapshots.exceptions.LogGithubIssueException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@EqualsAndHashCode
@Builder
@Getter
@RequiredArgsConstructor
public class Snapshot implements Comparable<Snapshot> {

  private final String name;
  private final String scenario;
  private final SnapshotHeader header;
  private final String body;

  @Override
  public int compareTo(Snapshot other) {
    return (name + scenario).compareTo(other.name + other.scenario);
  }

  public String getIdentifier() {
    return scenario == null ? name : String.format("%s[%s]", name, scenario);
  }

  public static Snapshot parse(String rawText) {
    String regex = "^(?<name>.*?)(\\[(?<scenario>.*)\\])?=(?<header>\\{.*\\})?(?<snapshot>(.*)$)";
    Pattern p = Pattern.compile(regex, Pattern.DOTALL);
    Matcher m = p.matcher(rawText);
    boolean found = m.find();
    if (!found) {
      throw new LogGithubIssueException(
          "Corrupt Snapshot (REGEX matches = 0): possibly due to manual editing or our REGEX failing\n"
              + "Possible Solutions\n"
              + "1. Ensure you have not accidentally manually edited the snapshot file!\n"
              + "2. Compare the snapshot with GIT history");
    }

    String name = m.group("name");
    String scenario = m.group("scenario");
    String header = m.group("header");
    String snapshot = m.group("snapshot");

    if (name == null || snapshot == null) {
      throw new LogGithubIssueException(
          "Corrupt Snapshot (REGEX name or snapshot group missing): possibly due to manual editing or our REGEX failing\n"
              + "Possible Solutions\n"
              + "1. Ensure you have not accidentally manually edited the snapshot file\n"
              + "2. Compare the snapshot with your version control history");
    }

    return Snapshot.builder()
        .name(name)
        .scenario(scenario)
        .header(SnapshotHeader.fromJson(header))
        .body(snapshot)
        .build();
  }

  /**
   * The raw string representation of the snapshot as it would appear in the *.snap file.
   *
   * @return raw snapshot
   */
  public String raw() {
    String headerJson = (header == null) || (header.size() == 0) ? "" : header.toJson();
    return getIdentifier() + "=" + headerJson + body;
  }

  @Override
  public String toString() {
    return raw();
  }
}
