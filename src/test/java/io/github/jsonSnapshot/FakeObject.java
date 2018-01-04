package io.github.jsonSnapshot;

import lombok.Builder;
import lombok.Setter;

@Builder
public class FakeObject {

    private String id;

    private Integer value;

    private String name;

    @Setter
    private FakeObject fakeObject;

}
