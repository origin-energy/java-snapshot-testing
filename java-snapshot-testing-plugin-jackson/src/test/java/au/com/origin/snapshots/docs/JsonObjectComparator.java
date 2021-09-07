package au.com.origin.snapshots.docs;

import au.com.origin.snapshots.Snapshot;
import au.com.origin.snapshots.comparators.SnapshotComparator;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;

public class JsonObjectComparator implements SnapshotComparator {
  @Override
  public boolean matches(Snapshot previous, Snapshot current) {
    return asObject(previous.getName(), previous.getBody()).equals(asObject(current.getName(), current.getBody()));
  }

  @SneakyThrows
  private static Object asObject(String snapshotName, String json) {
    return new ObjectMapper().readValue(json.replaceFirst(snapshotName + "=", ""), Object.class);
  }
}
