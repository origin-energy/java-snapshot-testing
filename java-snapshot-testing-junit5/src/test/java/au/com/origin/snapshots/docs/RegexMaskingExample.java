package au.com.origin.snapshots.docs;

import au.com.origin.snapshots.Expect;
import au.com.origin.snapshots.junit5.SnapshotExtension;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(SnapshotExtension.class)
public class RegexMaskingExample {

  private Expect expect;

  @Test
  public void maskDates() {
    expect.mask("\\d{2}-\\d{2}-\\d{4}", "**-**-****").toMatchSnapshot("Created on 07-10-2026");
    expect.toMatchSnapshot("Created on 08-10-2026");
  }

  @Test
  public void maskMultipleValues() {
    expect
        .mask("\\d{2}-\\d{2}-\\d{4}", "**-**-****")
        .mask("ID: \\d+", "ID: MASKED")
        .toMatchSnapshot("Created on 07-10-2026, ID: 123");
    expect.toMatchSnapshot("Created on 08-10-2026, ID: 456");
  }

  @Test
  public void maskJson() {
    Map<String, Object> response = new HashMap<>();
    response.put("name", "John Doe");
    response.put("createdAt", "2026-10-07");

    expect.serializer("json").mask("\\d{4}-\\d{2}-\\d{2}", "<DATE>").toMatchSnapshot(response);

    response.put("createdAt", "2026-10-08");
    expect.toMatchSnapshot(response);
  }
}
