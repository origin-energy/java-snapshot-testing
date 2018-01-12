package io.github.jsonSnapshot;

import lombok.Builder;
import lombok.Setter;

import java.util.List;

@Builder
public class FakeObject {

    private String id;

    private Integer value;

    private String name;

    @Setter
    private FakeObject fakeObject;


    public void fakeMethod(String fakeName, Long fakeNumber, List<String> fakeList) {

    }

}
