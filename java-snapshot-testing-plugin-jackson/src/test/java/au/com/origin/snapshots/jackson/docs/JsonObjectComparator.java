package au.com.origin.snapshots.jackson.docs;

import au.com.origin.snapshots.comparators.SnapshotComparator;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;

public class JsonObjectComparator implements SnapshotComparator {
  @Override
  public boolean matches(String snapshotName, String rawSnapshot, String currentObject) {
    return asObject(snapshotName, rawSnapshot).equals(asObject(snapshotName, currentObject));
  }

  @SneakyThrows
  private static Object asObject(String snapshotName, String json) {
    return new ObjectMapper().readValue(json.replaceFirst(snapshotName, ""), Object.class);
  }
}
