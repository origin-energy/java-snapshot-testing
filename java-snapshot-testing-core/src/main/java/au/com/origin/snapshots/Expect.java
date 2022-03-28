package au.com.origin.snapshots;

import au.com.origin.snapshots.annotations.SnapshotName;
import au.com.origin.snapshots.comparators.SnapshotComparator;
import au.com.origin.snapshots.reporters.SnapshotReporter;
import au.com.origin.snapshots.serializers.SnapshotSerializer;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
public class Expect {
  private final SnapshotVerifier snapshotVerifier;
  private final String snapshotName;

  private SnapshotSerializer snapshotSerializer;
  private SnapshotComparator snapshotComparator;
  private List<SnapshotReporter> snapshotReporters;
  private String scenario;

  public static Expect of(SnapshotVerifier snapshotVerifier,Method method) {
    return new Expect(snapshotVerifier, extractSnapshotName(method));
  }

  static String extractSnapshotName(Method testMethod) {
    SnapshotName snapshotName = testMethod.getAnnotation(SnapshotName.class);
    return  snapshotName == null ?
            testMethod.getDeclaringClass().getName() + "." + testMethod.getName() :
            snapshotName.value();
  }


  public static Expect of(SnapshotVerifier snapshotVerifier, String snapshotId) {
    return new Expect(snapshotVerifier, snapshotId);
  }



  /**
   * Make an assertion on the given input parameters against what already exists
   *
   * If you were previously using varargs and see an error - you can fix the error using
   * "toMatchSnapshotLegacy", however, a better approach is to use the ".scenario()"
   * feature as future versions of this library will most likely remove the legacy implementation completely.
   *
   * @param object snapshot object
   */
  public void toMatchSnapshot(Object object) {
    toMatchSnapshotLegacy(object);
  }

  /**
   * Make an assertion on the given input parameters against what already exists
   *
   * Previously called `toMatchSnapshot`, this varargs implementation will be removed
   * in future versions of this library.
   *
   * @param firstObject first snapshot object
   * @param objects     other snapshot objects
   */
  @Deprecated
  public void toMatchSnapshotLegacy(Object firstObject, Object... objects) {
    Snapshot snapshot = snapshotVerifier.expectCondition(snapshotName, firstObject, objects);
    if (snapshotSerializer != null) {
      snapshot.setSnapshotSerializer(snapshotSerializer);
    }
    if (snapshotComparator != null) {
      snapshot.setSnapshotComparator(snapshotComparator);
    }
    if (snapshotReporters != null) {
      snapshot.setSnapshotReporters(snapshotReporters);
    }
    if (scenario != null) {
      snapshot.setScenario(scenario);
    }
    snapshot.toMatchSnapshot();
  }


  /**
   * Normally a snapshot can be applied only once to a test method.
   * <p>
   * For Parameterized tests where the same method is executed multiple times you can supply
   * the scenario() to overcome this restriction.  Ensure each scenario is unique.
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
   * Apply a list of custom reporters for this snapshot
   * This will replace the default reporters defined in the config
   *
   * @param reporters your custom reporters
   * @return Snapshot
   */
  public Expect reporters(SnapshotReporter... reporters) {
    this.snapshotReporters = Arrays.asList(reporters);
    return this;
  }

  /**
   * Apply a custom serializer for this snapshot.
   *
   * @param serializer your custom serializer
   * @return this
   * @see au.com.origin.snapshots.serializers.SnapshotSerializer
   * <p>
   * Example implementations
   * @see au.com.origin.snapshots.serializers.ToStringSnapshotSerializer
   * @see au.com.origin.snapshots.serializers.Base64SnapshotSerializer
   */
  @SneakyThrows
  public Expect serializer(Class<? extends SnapshotSerializer> serializer) {
    this.snapshotSerializer = serializer.getConstructor().newInstance();
    return this;
  }

}
