package au.com.origin.snapshots;

import au.com.origin.snapshots.comparators.SnapshotComparator;
import au.com.origin.snapshots.reporters.SnapshotReporter;
import au.com.origin.snapshots.serializers.SnapshotSerializer;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

@RequiredArgsConstructor
public class Expect {
  private final SnapshotVerifier snapshotVerifier;
  private final Method testMethod;

  private SnapshotSerializer snapshotSerializer;
  private SnapshotComparator snapshotComparator;
  private List<SnapshotReporter> snapshotReporters;
  private String scenario;

  private final Map<String, String> headers = new HashMap<>();
  private final List<UnaryOperator<String>> masks = new ArrayList<>();

  public static Expect of(SnapshotVerifier snapshotVerifier, Method method) {
    return new Expect(snapshotVerifier, method);
  }

  /**
   * Make an assertion on the given input parameters against what already exists
   *
   * <p>If you were previously using varargs and see an error - you can fix the error using
   * "toMatchSnapshotLegacy", however, a better approach is to use the ".scenario()" feature as
   * future versions of this library will most likely remove the legacy implementation completely.
   *
   * @param object snapshot object
   */
  public void toMatchSnapshot(Object object) {
    SnapshotContext snapshotContext = snapshotVerifier.expectCondition(testMethod, object);
    if (snapshotSerializer != null) {
      snapshotContext.setSnapshotSerializer(snapshotSerializer);
    }
    if (snapshotComparator != null) {
      snapshotContext.setSnapshotComparator(snapshotComparator);
    }
    if (snapshotReporters != null) {
      snapshotContext.setSnapshotReporters(snapshotReporters);
    }
    if (scenario != null) {
      snapshotContext.setScenario(scenario);
    }
    snapshotContext.header.putAll(headers);
    snapshotContext.setMasks(new ArrayList<>(masks));

    snapshotContext.checkValidContext();

    snapshotContext.toMatchSnapshot();
  }

  /**
   * Mask all matches of a regular expression in the serialized snapshot body.
   *
   * <p>Masks are applied in registration order, after serialization and before saving or comparing
   * the snapshot. Snapshot names, scenarios and headers are not masked.
   *
   * <p>The replacement follows {@link java.util.regex.Matcher#replaceAll(String)} semantics,
   * including capture-group references. Use {@link
   * java.util.regex.Matcher#quoteReplacement(String)} for literal replacements containing dollar
   * signs or backslashes.
   *
   * @param regex regular expression to match
   * @param replacement replacement for each match
   * @return this
   * @throws java.util.regex.PatternSyntaxException if the regular expression is invalid
   * @throws NullPointerException if regex or replacement is null
   */
  public Expect mask(String regex, String replacement) {
    Pattern pattern = Pattern.compile(regex);
    Objects.requireNonNull(replacement, "replacement");
    masks.add(body -> pattern.matcher(body).replaceAll(replacement));
    return this;
  }

  /**
   * Normally a snapshot can be applied only once to a test method.
   *
   * <p>For Parameterized tests where the same method is executed multiple times you can supply the
   * scenario() to overcome this restriction. Ensure each scenario is unique.
   *
   * @param scenario - unique scenario description
   * @return Snapshot
   */
  public Expect scenario(String scenario) {
    this.scenario = scenario;
    return this;
  }

  /**
   * Apply a custom serializer for this snapshot
   *
   * @param serializer your custom serializer
   * @return Snapshot
   */
  public Expect serializer(SnapshotSerializer serializer) {
    this.snapshotSerializer = serializer;
    return this;
  }

  /**
   * Apply a custom serializer for this snapshot
   *
   * @param name - the {name} attribute serializer.{name} from snapshot.properties
   * @return Snapshot
   */
  public Expect serializer(String name) {
    this.snapshotSerializer = SnapshotProperties.getInstance("serializer." + name);
    return this;
  }

  /**
   * Apply a custom comparator for this snapshot
   *
   * @param comparator your custom comparator
   * @return Snapshot
   */
  public Expect comparator(SnapshotComparator comparator) {
    this.snapshotComparator = comparator;
    return this;
  }

  /**
   * Apply a custom comparator for this snapshot
   *
   * @param name the {name} attribute comparator.{name} from snapshot.properties
   * @return Snapshot
   */
  public Expect comparator(String name) {
    this.snapshotComparator = SnapshotProperties.getInstance("comparator." + name);
    return this;
  }

  /**
   * Apply a list of custom reporters for this snapshot This will replace the default reporters
   * defined in the config
   *
   * @param reporters your custom reporters
   * @return Snapshot
   */
  public Expect reporters(SnapshotReporter... reporters) {
    this.snapshotReporters = Arrays.asList(reporters);
    return this;
  }

  /**
   * Apply a list of custom reporters for this snapshot This will replace the default reporters
   * defined in the config
   *
   * @param name the {name} attribute reporters.{name} from snapshot.properties
   * @return Snapshot
   */
  public Expect reporters(String name) {
    this.snapshotReporters = SnapshotProperties.getInstances("reporters." + name);
    return this;
  }

  /**
   * Apply a custom serializer for this snapshot.
   *
   * @param serializer your custom serializer
   * @return this
   * @see au.com.origin.snapshots.serializers.SnapshotSerializer
   *     <p>Example implementations
   * @see au.com.origin.snapshots.serializers.ToStringSnapshotSerializer
   * @see au.com.origin.snapshots.serializers.Base64SnapshotSerializer
   */
  @SneakyThrows
  public Expect serializer(Class<? extends SnapshotSerializer> serializer) {
    this.snapshotSerializer = serializer.getConstructor().newInstance();
    return this;
  }

  /**
   * Add anything you like to the snapshot header.
   *
   * <p>These custom headers can be used in serializers, comparators or reporters to change how they
   * behave.
   *
   * @param key key
   * @param value value
   * @return Expect
   */
  public Expect header(String key, String value) {
    headers.put(key, value);
    return this;
  }
}
