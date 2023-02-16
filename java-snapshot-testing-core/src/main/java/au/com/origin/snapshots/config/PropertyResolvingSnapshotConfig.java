package au.com.origin.snapshots.config;

import au.com.origin.snapshots.SnapshotProperties;
import au.com.origin.snapshots.comparators.SnapshotComparator;
import au.com.origin.snapshots.exceptions.MissingSnapshotPropertiesKeyException;
import au.com.origin.snapshots.logging.LoggingHelper;
import au.com.origin.snapshots.reporters.SnapshotReporter;
import au.com.origin.snapshots.serializers.SnapshotSerializer;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PropertyResolvingSnapshotConfig implements SnapshotConfig {

  @Override
  public String getOutputDir() {
    return SnapshotProperties.getOrThrow("output-dir");
  }

  @Override
  public String getSnapshotDir() {
    return SnapshotProperties.getOrThrow("snapshot-dir");
  }

  @Override
  public Optional<String> updateSnapshot() {
    // This was the original way to update snapshots
    Optional<String> legacyFlag =
        Optional.ofNullable(System.getProperty(JVM_UPDATE_SNAPSHOTS_PARAMETER));
    if (legacyFlag.isPresent()) {
      LoggingHelper.deprecatedV5(
          log,
          "Passing -PupdateSnapshot will be removed in a future release. Consider using snapshot.properties 'update-snapshot' toggle instead");
      if ("false".equals(legacyFlag.get())) {
        return Optional.empty();
      }
      return legacyFlag;
    }

    try {
      String updateSnapshot = SnapshotProperties.getOrThrow("update-snapshot");
      if ("all".equals(updateSnapshot)) {
        return Optional.of("");
      } else if ("none".equals(updateSnapshot)) {
        return Optional.empty();
      }
      return Optional.of(updateSnapshot);
    } catch (MissingSnapshotPropertiesKeyException ex) {
      LoggingHelper.deprecatedV5(
          log,
          "You do not have 'update-snapshot=none' defined in your snapshot.properties - consider adding it now");
      return Optional.empty();
    }
  }

  @Override
  public SnapshotSerializer getSerializer() {
    return SnapshotProperties.getInstance("serializer");
  }

  @Override
  public SnapshotComparator getComparator() {
    return SnapshotProperties.getInstance("comparator");
  }

  @Override
  public List<SnapshotReporter> getReporters() {
    return SnapshotProperties.getInstances("reporters");
  }

  @Override
  public boolean isCI() {
    String envVariable = SnapshotProperties.getOrThrow("ci-env-var");
    return System.getenv(envVariable) != null;
  }
}
