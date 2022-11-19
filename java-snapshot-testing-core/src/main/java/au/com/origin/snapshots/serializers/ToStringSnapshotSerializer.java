package au.com.origin.snapshots.serializers;

import au.com.origin.snapshots.Snapshot;
import au.com.origin.snapshots.SnapshotFile;
import au.com.origin.snapshots.SnapshotSerializerContext;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
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
    List<Object> objects = Arrays.asList(object);
    String body =
        "[\n"
            + objects.stream()
                .map(Object::toString)
                .map(
                    it -> {
                      if (it.contains(SnapshotFile.SPLIT_STRING)) {
                        log.warn(
                            "Found 3 consecutive lines in your snapshot \\n\\n\\n. This sequence is reserved as the snapshot separator - replacing with \\n.\\n.\\n");
                        return it.replaceAll(SnapshotFile.SPLIT_STRING, "\n.\n.\n");
                      }
                      return it;
                    })
                .collect(Collectors.joining("\n"))
            + "\n]";
    return gen.toSnapshot(body);
  }

  @Override
  public String getOutputFormat() {
    return SerializerType.TEXT.name();
  }
}
