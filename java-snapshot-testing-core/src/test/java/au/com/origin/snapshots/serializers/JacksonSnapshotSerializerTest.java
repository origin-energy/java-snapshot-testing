package au.com.origin.snapshots.serializers;

import au.com.origin.snapshots.SnapshotConfig;
import au.com.origin.snapshots.SnapshotMatchException;
import au.com.origin.snapshots.config.BaseSnapshotConfig;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.*;

import static au.com.origin.snapshots.SnapshotMatcher.*;

public class JacksonSnapshotSerializerTest {

    private static final SnapshotConfig DEFAULT_CONFIG = new BaseSnapshotConfig() {
        @Override
        public SnapshotSerializer getSerializer() {
            return new JacksonSnapshotSerializer();
        }
    };

    @Test
    public void shouldSerializeDifferentTypesDeterministically() {
        start(DEFAULT_CONFIG);
        expect(new TypeDummy()).toMatchSnapshot();
        validateSnapshots();
    }

    private static enum AnEnum {
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
        private final ZonedDateTime zonedDateTime = ZonedDateTime.parse("2020-10-19T22:21:07.103+10:00[Australia/Melbourne]");
        private final AnEnum anEnum = AnEnum.A;
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
        private final List<String> arrayList = new ArrayList<String>() {{
            add("f");
            add("a");
            add("d");
            add("e");
            add("g");
            add("b");
            add("c");
        }};
        private final Set<String> hashSet = new HashSet<String>() {{
            add("f");
            add("a");
            add("d");
            add("e");
            add("g");
            add("b");
            add("c");
        }};
        private final Set<String> treeSet = new TreeSet<String>() {{
            add("f");
            add("a");
            add("d");
            add("e");
            add("g");
            add("b");
            add("c");
        }};
        private final Map<String, Integer> hashMap = new HashMap<String, Integer>() {{
            put("f", 6);
            put("a", 1);
            put("d", 4);
            put("e", 5);
            put("g", 7);
            put("b", 2);
            put("c", 3);
        }};
        private final Optional<String> presentOptional = Optional.of("Hello World");
        private final Optional<String> emptyOptional = Optional.empty();
    }
}
