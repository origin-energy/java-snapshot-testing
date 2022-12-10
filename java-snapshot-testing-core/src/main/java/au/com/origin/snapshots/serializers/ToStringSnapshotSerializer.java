package au.com.origin.snapshots.serializers;

import au.com.origin.snapshots.Snapshot;
import au.com.origin.snapshots.SnapshotFile;
import au.com.origin.snapshots.SnapshotSerializerContext;
import lombok.extern.slf4j.Slf4j;

/**
 * This Serializer does a snapshot of the {@link Object#toString()} method
 *
 * <p>Will render each toString() on a separate line
 */
@Slf4j
public class ToStringSnapshotSerializer implements SnapshotSerializer {

  @Override
  public Snapshot apply(Object object, SnapshotSerializerContext gen) {
    String body = "[\n" + object.toString() + "\n]";
    if (body.contains(SnapshotFile.SPLIT_STRING)) {
      log.warn(
          "Found 3 consecutive lines in your snapshot \\n\\n\\n. This sequence is reserved as the snapshot separator - replacing with \\n.\\n.\\n");
      body = body.replaceAll(SnapshotFile.SPLIT_STRING, "\n.\n.\n");
    }
    return gen.toSnapshot(body);
  }

  @Override
  public String getOutputFormat() {
    return SerializerType.TEXT.name();
  }
}
