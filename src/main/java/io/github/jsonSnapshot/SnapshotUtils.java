package io.github.jsonSnapshot;

import org.mockito.ArgumentCaptor;

import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

public class SnapshotUtils {

    public static <T> HashMap<String, List<LinkedHashMap<String, Object>>> extractArgs(T object, String methodName,
                                                                                       Class... classes) {
        List<ArgumentCaptor> captors = new ArrayList<>();
        HashMap<String, List<LinkedHashMap<String, Object>>> result = new HashMap<>();

        for (Class clazz : classes) {
            captors.add(ArgumentCaptor.forClass(clazz));
        }

        try {
            Parameter[] parameters = object.getClass().getMethod(methodName, classes).getParameters();

            object.getClass()
                    .getMethod(methodName, classes)
                    .invoke(verify(object, atLeastOnce()), captors.stream().map(ArgumentCaptor::capture).toArray());


            List<LinkedHashMap<String, Object>> extractedObjects = new ArrayList<>();

            int numberOfCall;

            if (captors.size() > 0) {
                numberOfCall = captors.get(0).getAllValues().size();

                for (int i = 0; i < numberOfCall; i++) {
                    LinkedHashMap<String, Object> objectMap = new LinkedHashMap<>();

                    int j = 0;
                    for (ArgumentCaptor captor : captors) {
                        objectMap.put(parameters[j].getName(), captor.getAllValues().get(i));
                        j++;
                    }
                    extractedObjects.add(objectMap);
                }
            }

            result.put(object.getClass().getSuperclass().getSimpleName() + "." + methodName, extractedObjects);
        } catch (Exception e) {
            throw new SnapshotMatchException(e.getMessage());
        }

        return result;
    }
}
