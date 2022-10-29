package au.com.origin.snapshots.jackson.serializers.docs;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Instant;

// Example base class used by all hibernate entities
@Data
@AllArgsConstructor
public class BaseEntity {
  private Long id;
  private Instant createdDate;
  private Instant lastModifiedDate;
  private String somethingElse;
}
