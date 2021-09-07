package au.com.origin.snapshots.serializers;

import au.com.origin.snapshots.Expect;
import au.com.origin.snapshots.SnapshotVerifier;
import au.com.origin.snapshots.config.PropertyResolvingSnapshotConfig;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.*;

public class DeterministicJacksonSnapshotSerializerTest {

  @Test
  public void shouldSerializeDifferentTypes(TestInfo testInfo) {
    SnapshotVerifier snapshotVerifier = new SnapshotVerifier(new PropertyResolvingSnapshotConfig(), testInfo.getTestClass().get(), false);
    Expect expect = Expect.of(snapshotVerifier, testInfo.getTestMethod().get());
    expect
        .serializer("orderedJson")
        .toMatchSnapshot(new TypeDummy());
    snapshotVerifier
        .validateSnapshots();
  }

  @Test
  void shouldSupportJsonFormat() {
    Assertions.assertThat(new DeterministicJacksonSnapshotSerializer().getOutputFormat()).isEqualTo(SerializerType.JSON.name());
  }

  private Map<String, Integer> nonDeterministicMap(Map<String, Integer> target) {
    final List<String> items = new ArrayList<String>() {{
      add("f");
      add("a");
      add("d");
      add("e");
      add("g");
      add("b");
      add("c");
    }};

    int size = items.size();
    for (int i = 0; i < size; i++) {
      String random = pluckRandom(items);
      target.put(random, (int) random.charAt(0));
    }
    return target;
  }

  private Collection<String> nonDeterministicCollection(Collection<String> target) {
    final List<String> items = new ArrayList<String>() {{
      add("f");
      add("a");
      add("d");
      add("e");
      add("g");
      add("b");
      add("c");
    }};

    int size = items.size();
    for (int i = 0; i < size; i++) {
      target.add(pluckRandom(items));
    }

    return target;
  }

  private String pluckRandom(List<String> array) {
    int rnd = new Random().nextInt(array.size());
    return array.remove(rnd);
  }

  private enum AnEnum {
    F, A, D, E, G, B, C;
  }

  private final class TypeDummy {
    private final Void aNull = null;
    private final Object anObject = new Object();
    private final byte aByte = "A".getBytes()[0];
    private final short aShort = 32767;
    private final int anInt = 2147483647;
    private final long aLong = 9223372036854775807L;
    private final float aFloat = 0.1234567F;
    private final double aDouble = 1.123456789123456D;
    private final boolean aBoolean = true;
    private final char aChar = 'A';
    private final String string = "Hello World";
    private final Date date = Date.from(Instant.parse("2020-10-19T22:21:07.103Z"));
    private final LocalDate localDate = LocalDate.parse("2020-10-19");
    private final LocalDateTime localDateTime = LocalDateTime.parse("2020-10-19T22:21:07.103");
    private final ZonedDateTime zonedDateTime = ZonedDateTime.parse("2020-04-19T22:21:07.103+10:00[Australia/Melbourne]");
    private final AnEnum anEnum = AnEnum.A;
    private final Optional<String> presentOptional = Optional.of("Hello World");
    private final Optional<String> emptyOptional = Optional.empty();
    private final String[] stringArray = {
        "f",
        "a",
        "d",
        "e",
        "g",
        "b",
        "c"
    };
    private final Object[] anEnumArray = Arrays.stream(AnEnum.values()).toArray();

    // Maps
    private final Map<String, Integer> hashMap = nonDeterministicMap(new HashMap<>());
    private final Map<String, Integer> treeMap = nonDeterministicMap(new TreeMap<>());
    private final Map<String, Integer> linkedHashMap = nonDeterministicMap(new LinkedHashMap<>());

    // Sets
    private final Collection<String> linkedHashSet = nonDeterministicCollection(new LinkedHashSet<>());
    private final Collection<String> hashSet = nonDeterministicCollection(new HashSet<>());
    private final Collection<String> treeSet = nonDeterministicCollection(new TreeSet<>());

    // Lists
    private final Collection<String> arrayList = nonDeterministicCollection(new ArrayList<>());
    private final Collection<String> linkedList = nonDeterministicCollection(new LinkedList<>());

    // Mixed Maps, Sets, Lists
    private final Collection<Object> listOfCollections = new ArrayList<Object>() {{
      add(nonDeterministicMap(new LinkedHashMap<>()));
      add(nonDeterministicCollection(new LinkedHashSet<>()));
      add(nonDeterministicCollection(new LinkedList<>()));
    }};
  }
}
