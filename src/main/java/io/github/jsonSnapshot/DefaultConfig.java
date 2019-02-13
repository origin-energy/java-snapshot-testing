package io.github.jsonSnapshot;

import lombok.Getter;

public class DefaultConfig implements SnapshotConfig {
  @Getter private String filePath = "src/test/java/";
}
