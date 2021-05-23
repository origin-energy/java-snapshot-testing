package au.com.origin.snapshots.serializers;

import au.com.origin.snapshots.SnapshotFile;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * This Serializer does a snapshot of the {@link Object#toString()} method
 *
 * Will render each toString() on a separate line
 */
@Slf4j
public class ToStringSnapshotSerializer implements SnapshotSerializer {

    @Override
    public String apply(Object[] objects) {
        return "[\n" + Arrays.stream(objects)
            .map(Object::toString)
            .map(it -> {
                if (it.contains(SnapshotFile.SPLIT_STRING)) {
                    log.warn("Found 3 consecutive lines in your snapshot \\n\\n\\n. This sequence is reserved as the snapshot separator - replacing with \\n.\\n.\\n");
                    return it.replaceAll(SnapshotFile.SPLIT_STRING, "\n.\n.\n");
                }
                return it;
            })
            .collect(Collectors.joining("\n")) +
            "\n]";
    }

    @Override
    public String getOutputFormat() {
        return SerializerType.TEXT.name();
    }
}
