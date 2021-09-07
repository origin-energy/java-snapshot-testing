package au.com.origin.snapshots.reporters;

import au.com.origin.snapshots.Snapshot;
import org.assertj.core.util.diff.DiffUtils;
import org.assertj.core.util.diff.Patch;
import org.opentest4j.AssertionFailedError;

import java.util.Arrays;
import java.util.function.Supplier;

public class PlainTextSnapshotReporter implements SnapshotReporter {

  private static final Supplier<IllegalStateException> NO_DIFF_EXCEPTION_SUPPLIER =
      () -> new IllegalStateException("No differences found. Potential mismatch between comparator and reporter");

  public static String getDiffString(Patch<String> patch) {
    return patch
        .getDeltas()
        .stream()
        .map(delta -> delta.toString() + "\n")
        .reduce(String::concat)
        .orElseThrow(NO_DIFF_EXCEPTION_SUPPLIER);
  }

  @Override
  public boolean supportsFormat(String outputFormat) {
    return true; // always true
  }

  @Override
  public void report(Snapshot previous, Snapshot current) {
    Patch<String> patch = DiffUtils.diff(
        Arrays.asList(previous.raw().split("\n")),
        Arrays.asList(current.raw().split("\n")));

    String message = "Error on: \n" + current.raw() + "\n\n" + getDiffString(patch);

    throw new AssertionFailedError(message, previous.raw(), current.raw());
  }
}
