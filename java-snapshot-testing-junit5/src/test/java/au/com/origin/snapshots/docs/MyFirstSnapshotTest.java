package au.com.origin.snapshots.docs;

import au.com.origin.snapshots.annotations.SnapshotName;
import au.com.origin.snapshots.junit5.SnapshotExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.HashMap;
import java.util.Map;
import au.com.origin.snapshots.Expect;

@ExtendWith({SnapshotExtension.class})
public class MyFirstSnapshotTest {

  private Expect expect;

  @SnapshotName("i_can_give_custom_names_to_my_snapshots")
  @Test
  public void toStringSerializationTest() {
    expect.toMatchSnapshot("Hello World");
  }

  @Test
  public void jsonSerializationTest() {
    Map<String, Object> map = new HashMap<>();
    map.put("name", "John Doe");
    map.put("age", 40);

    expect
        .serializer("json")
        .toMatchSnapshot(map);
  }

}
