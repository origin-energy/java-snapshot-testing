package au.com.origin.snapshots;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

@RequiredArgsConstructor
public class SnapshotHeader extends HashMap<String, String> {

  //
  // Manual JSON serialization/deserialization as I don't want to
  // include another dependency for it
  //
  @SneakyThrows
  public String toJson() {
    StringBuilder b = new StringBuilder();
    b.append("{\n");
    final int lastIndex = this.size();
    int currIndex = 0;
    for (Map.Entry entry : this.entrySet()) {
      currIndex++;
      String format = currIndex == lastIndex ? "  \"%s\": \"%s\"\n" : "  \"%s\": \"%s\",\n";
      b.append(String.format(format, entry.getKey(), entry.getValue()));
    }
    b.append("}");
    return b.toString();
  }

  @SneakyThrows
  public static SnapshotHeader fromJson(String json) {
    SnapshotHeader snapshotHeader = new SnapshotHeader();

    if (json == null) {
      return snapshotHeader;
    }

    String regex = "\\\"(?<key>.*)\\\": \\\"(?<value>.*)\\\"";
    Pattern p = Pattern.compile(regex);
    Matcher m = p.matcher(json);
    while (m.find()) {
      snapshotHeader.put(m.group("key"), m.group("value"));
    }
    return snapshotHeader;
  }
}
