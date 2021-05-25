package au.com.origin.snapshots.docs;

import au.com.origin.snapshots.junit5.SnapshotExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.HashMap;
import java.util.Map;
import au.com.origin.snapshots.Expect;

@ExtendWith({SnapshotExtension.class})
public class MyFirstSnapshotTest {

  @Test
  public void toStringSerializationTest(Expect expect) {
    expect.toMatchSnapshot("Hello World");
  }

  @Test
  public void jsonSerializationTest(Expect expect) {
    Map<String, Object> map = new HashMap<>();
    map.put("name", "John Doe");
    map.put("age", 40);

    expect
        .serializer("json")
        .toMatchSnapshot(map);
  }
}
