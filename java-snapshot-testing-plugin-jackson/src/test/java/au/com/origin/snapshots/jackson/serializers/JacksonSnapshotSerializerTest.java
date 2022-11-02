package au.com.origin.snapshots.jackson.serializers;

import au.com.origin.snapshots.*;
import au.com.origin.snapshots.config.PropertyResolvingSnapshotConfig;
import au.com.origin.snapshots.config.SnapshotConfig;
import au.com.origin.snapshots.serializers.SerializerType;
import au.com.origin.snapshots.serializers.SnapshotSerializer;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.*;

public class JacksonSnapshotSerializerTest {

  private static final SnapshotConfig DEFAULT_CONFIG = new PropertyResolvingSnapshotConfig() {
    @Override
    public SnapshotSerializer getSerializer() {
      return new JacksonSnapshotSerializer();
    }
  };

  private SnapshotSerializerContext gen = new SnapshotSerializerContext(
    "test",
    null,
    new SnapshotHeader(),
    JacksonSnapshotSerializerTest.class,
    null
  );

  @Test
  public void shouldSerializeMap() {
    Map<String, Object> map = new HashMap<>();
    map.put("name", "John Doe");
    map.put("age", 40);

    SnapshotSerializer serializer = new JacksonSnapshotSerializer();
    Snapshot result = serializer.apply(map, gen);
    Assertions.assertThat(result.getBody()).isEqualTo("[\n" +
        "  {\n" +
        "    \"age\": 40,\n" +
        "    \"name\": \"John Doe\"\n" +
        "  }\n" +
        "]");
  }

  @Test
  public void shouldSerializeDifferentTypes(TestInfo testInfo) {
    SnapshotVerifier snapshotVerifier = new SnapshotVerifier(DEFAULT_CONFIG, testInfo.getTestClass().get());
    Expect expect = Expect.of(snapshotVerifier, testInfo.getTestMethod().get());
    expect.toMatchSnapshot(new TypeDummy());
    snapshotVerifier.validateSnapshots();
  }

  @Test
  void shouldSupportJsonFormat() {
    Assertions.assertThat(new JacksonSnapshotSerializer().getOutputFormat()).isEqualTo(SerializerType.JSON.name());
  }

  private Map<String, Integer> deterministicMap(Map<String, Integer> target) {
    final List<String> items = new ArrayList<String>() {{
      add("f");
      add("a");
      add("d");
      add("e");
      add("g");
      add("b");
      add("c");
    }};
    items.forEach(it -> target.put(it, (int) it.charAt(0)));
    return target;
  }

  private Collection<String> deterministicCollection(Collection<String> target) {
    final List<String> items = new ArrayList<String>() {{
      add("f");
      add("a");
      add("d");
      add("e");
      add("g");
      add("b");
      add("c");
    }};
    target.addAll(items);
    return target;
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
    private final Map<String, Integer> hashMap = deterministicMap(new HashMap<>());
    private final Map<String, Integer> treeMap = deterministicMap(new TreeMap<>());
    private final Map<String, Integer> linkedHashMap = deterministicMap(new LinkedHashMap<>());

    // Sets
    private final Collection<String> linkedHashSet = deterministicCollection(new LinkedHashSet<>());
    private final Collection<String> hashSet = deterministicCollection(new HashSet<>());
    private final Collection<String> treeSet = deterministicCollection(new TreeSet<>());

    // Lists
    private final Collection<String> arrayList = deterministicCollection(new ArrayList<>());
    private final Collection<String> linkedList = deterministicCollection(new LinkedList<>());

    // Mixed Maps, Sets, Lists
    private final Collection<Object> listOfCollections = new ArrayList<Object>() {{
      add(deterministicMap(new LinkedHashMap<>()));
      add(deterministicCollection(new LinkedHashSet<>()));
      add(deterministicCollection(new LinkedList<>()));
    }};
  }
}
