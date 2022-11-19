package au.com.origin.snapshots.jackson.docs;

import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Data;

// Example base class used by all hibernate entities
@Data
@AllArgsConstructor
public class BaseEntity {
  private Long id;
  private Instant createdDate;
  private Instant lastModifiedDate;
  private String somethingElse;
}
