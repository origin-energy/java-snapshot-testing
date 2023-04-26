package au.com.origin.snapshots.jackson.docs;

import au.com.origin.snapshots.jackson.serializers.DeterministicJacksonSnapshotSerializer;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreType;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Instant;
import java.util.List;
import java.util.Set;

public class HibernateSnapshotSerializer extends DeterministicJacksonSnapshotSerializer {

  @Override
  public void configure(ObjectMapper objectMapper) {
    super.configure(objectMapper);

    // Ignore Hibernate Lists to prevent infinite recursion
    objectMapper.addMixIn(List.class, IgnoreTypeMixin.class);
    objectMapper.addMixIn(Set.class, IgnoreTypeMixin.class);

    // Ignore Fields that Hibernate generates for us automatically
    objectMapper.addMixIn(BaseEntity.class, IgnoreHibernateEntityFields.class);
  }

  @JsonIgnoreType
  class IgnoreTypeMixin {}

  abstract class IgnoreHibernateEntityFields {
    @JsonIgnore
    abstract Long getId();

    @JsonIgnore
    abstract Instant getCreatedDate();

    @JsonIgnore
    abstract Instant getLastModifiedDate();
  }
}
