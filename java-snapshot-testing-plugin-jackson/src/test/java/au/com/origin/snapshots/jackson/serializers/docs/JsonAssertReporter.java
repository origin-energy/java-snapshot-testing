package au.com.origin.snapshots.jackson.serializers.docs;

import au.com.origin.snapshots.reporters.SnapshotReporter;
import au.com.origin.snapshots.serializers.SerializerType;
import lombok.SneakyThrows;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

public class JsonAssertReporter implements SnapshotReporter {
  @Override
  public boolean supportsFormat(String outputFormat) {
    return SerializerType.JSON.name().equalsIgnoreCase(outputFormat);
  }

  @Override
  @SneakyThrows
  public void report(String snapshotName, String rawSnapshot, String currentObject) {
    JSONAssert.assertEquals(rawSnapshot, currentObject, JSONCompareMode.STRICT);
  }
}