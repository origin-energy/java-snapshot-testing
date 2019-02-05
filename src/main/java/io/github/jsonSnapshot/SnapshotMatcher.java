package io.github.jsonSnapshot;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.PrettyPrinter;
import com.fasterxml.jackson.core.util.DefaultIndenter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter.Indenter;
import com.fasterxml.jackson.core.util.Separators;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.assertj.core.util.Arrays;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.jupiter.api.BeforeAll;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class SnapshotMatcher {

    private static Logger log = LoggerFactory.getLogger(SnapshotMatcher.class);

    private static Class clazz = null;
    private static SnapshotFile snapshotFile = null;
    private static List<Snapshot> calledSnapshots = new ArrayList<>();
    private static Function<Object, String> jsonFunction;

    public static void start() {
        start(new DefaultConfig(), defaultJsonFunction());
    }

    public static void start(SnapshotConfig config) {
        start(config, defaultJsonFunction());
    }

    public static void start(Function<Object, String> jsonFunction) {
        start(new DefaultConfig(), jsonFunction);
    }

    public static void start(SnapshotConfig config, Function<Object, String> jsonFunction) {
        SnapshotMatcher.jsonFunction = jsonFunction;
        try {
            StackTraceElement stackElement = findStackElement();
            clazz = Class.forName(stackElement.getClassName());
            snapshotFile = new SnapshotFile(config.getFilePath(), stackElement.getClassName().replaceAll("\\.", "/") + ".snap");
        } catch (ClassNotFoundException | IOException e) {
            throw new SnapshotMatchException(e.getMessage());
        }
    }

    public static void validateSnapshots() {
        Set<String> rawSnapshots = snapshotFile.getRawSnapshots();
        List<String> snapshotNames = calledSnapshots.stream().map(Snapshot::getSnapshotName).collect(Collectors.toList());
        List<String> unusedRawSnapshots = new ArrayList<>();

        for (String rawSnapshot : rawSnapshots) {
            boolean foundSnapshot = false;
            for (String snapshotName : snapshotNames) {
                if (rawSnapshot.contains(snapshotName)) {
                    foundSnapshot = true;
                }
            }
            if (!foundSnapshot) {
                unusedRawSnapshots.add(rawSnapshot);
            }
        }
        if (unusedRawSnapshots.size() > 0) {
            log.warn("All unused Snapshots: " + StringUtils.join(unusedRawSnapshots, "\n") + ". Consider deleting the snapshot file to recreate it!");
        }
    }

    public static Snapshot expect(Object firstObject, Object... others) {

        if (clazz == null) {
            throw new SnapshotMatchException("SnapshotTester not yet started! Start it on @BeforeClass with SnapshotMatcher.start()");
        }
        try {
            Object[] objects = mergeObjects(firstObject, others);
            StackTraceElement stackElement = findStackElement();
            Method method = getMethod(stackElement, clazz);
            Snapshot snapshot = new Snapshot(snapshotFile, clazz, method, jsonFunction, objects);
            validateExpectCall(snapshot);
            calledSnapshots.add(snapshot);
            return snapshot;
        } catch (ClassNotFoundException e) {
            throw new SnapshotMatchException(e.getMessage());
        }
    }

    static Function<Object, String> defaultJsonFunction() {

        ObjectMapper objectMapper = buildObjectMapper();

        PrettyPrinter pp = buildDefaultPrettyPrinter();

        return (object) -> {
            try {
                return objectMapper.writer(pp).writeValueAsString(object);
            } catch (Exception e) {
                throw new SnapshotMatchException(e.getMessage());
            }
        };
    }

    private static PrettyPrinter buildDefaultPrettyPrinter() {
        DefaultPrettyPrinter pp = new DefaultPrettyPrinter(""){
            @Override
            public DefaultPrettyPrinter withSeparators(Separators separators) {
                this._separators = separators;
                this._objectFieldValueSeparatorWithSpaces = separators.getObjectFieldValueSeparator() + " ";
                return this;
            }
        };
        Indenter lfOnlyIndenter = new DefaultIndenter("  ", "\n");
        pp.indentArraysWith(lfOnlyIndenter);
        pp.indentObjectsWith(lfOnlyIndenter);
        return pp;
    }

    private static ObjectMapper buildObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        objectMapper.setVisibility(objectMapper.getSerializationConfig().getDefaultVisibilityChecker()
                .withFieldVisibility(JsonAutoDetect.Visibility.ANY)
                .withGetterVisibility(JsonAutoDetect.Visibility.NONE)
                .withSetterVisibility(JsonAutoDetect.Visibility.NONE)
                .withCreatorVisibility(JsonAutoDetect.Visibility.NONE));
        return objectMapper;
    }

    private static void validateExpectCall(Snapshot snapshot) {
        for (Snapshot eachSnapshot : calledSnapshots) {
            if (eachSnapshot.getSnapshotName().equals(snapshot.getSnapshotName())) {
                throw new SnapshotMatchException("You can only call 'expect' once per test method. Try using array of arguments on a single 'expect' call");
            }
        }
    }

    private static Method getMethod(StackTraceElement testClass, Class clazz) {
        Method method;
        try {
            method = clazz.getMethod(testClass.getMethodName());
        } catch (NoSuchMethodException e) {
            throw new SnapshotMatchException("Please annotate your test method with @Test and make it without any parameters!");
        }
        return method;
    }

    private static StackTraceElement findStackElement() throws ClassNotFoundException {
        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();

        int i = 1; // Start after stackTrace
        while (i < stackTraceElements.length &&
                SnapshotMatcher.class.equals(Class.forName(stackTraceElements[i].getClassName()))) { //TODO
            i++;
        }

        for (int j = i; j < stackTraceElements.length; j++) {

            try {
                Class clazz = Class.forName(stackTraceElements[j].getClassName());
                Method method = clazz.getMethod(stackTraceElements[j].getMethodName());

                // Navigate into stack until Test class/method level
                if (method.isAnnotationPresent(Test.class) || method.isAnnotationPresent(BeforeClass.class) ||
                        method.isAnnotationPresent(org.junit.jupiter.api.Test.class) || method.isAnnotationPresent(BeforeAll.class)) {
                    i = j;
                    break;
                }
            }
            catch(NoSuchMethodException ignored) {

            }
        }

        return stackTraceElements[i];
    }

    private static Object[] mergeObjects(Object firstObject, Object[] others) {
        Object[] objects = new Object[1];
        objects[0] = firstObject;
        if (!Arrays.isNullOrEmpty(others)) {
            objects = ArrayUtils.addAll(objects, others);
        }
        return objects;
    }
}
