package au.com.origin.snapshots;

import static org.junit.jupiter.api.Assertions.assertThrows;

import au.com.origin.snapshots.annotations.SnapshotName;
import au.com.origin.snapshots.config.BaseSnapshotConfig;
import au.com.origin.snapshots.exceptions.SnapshotExtensionException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

public class SnapshotNameAnnotationWithDuplicatesTest {

  @SnapshotName("hello_world")
  @Test
  void canUseSnapshotNameAnnotation(TestInfo testInfo) {
    assertThrows(
        SnapshotExtensionException.class,
        () -> new SnapshotVerifier(new BaseSnapshotConfig(), testInfo.getTestClass().get()),
        "Oops, looks like you set the same name of two separate snapshots @SnapshotName(\"hello_world\") in class au.com.origin.snapshots.SnapshotNameAnnotationTest");
  }

  @SnapshotName("hello_world")
  private void anotherMethodWithSameSnapshotName() {}
}
