package au.com.origin.snapshots;

import au.com.origin.snapshots.exceptions.SnapshotExtensionException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

public class SnapshotExtensionUnusedTest {

    @Test
    public void shouldNotUseExtension() {
        Throwable exceptionThatWasThrown = Assertions.assertThrows(SnapshotExtensionException.class, () -> {
            SnapshotMatcher.expect("Should blow up").toMatchSnapshot();
        });
        assertThat(exceptionThatWasThrown.getMessage()).isEqualTo("setTestMethod() not called! Has SnapshotMatcher.start() been called?");
    }
}
