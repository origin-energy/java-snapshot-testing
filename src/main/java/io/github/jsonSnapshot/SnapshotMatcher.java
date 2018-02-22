package io.github.jsonSnapshot;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.assertj.core.util.Arrays;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.verification.VerificationMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

public class SnapshotMatcher {

    private static Logger log = LoggerFactory.getLogger(SnapshotMatcher.class);

    private static Class clazz = null;
    private static SnapshotFile snapshotFile = null;
    private static List<Snapshot> calledSnapshots = new ArrayList<>();

    public static void start() {
        start(new DefaultConfig());
    }

    public static void start(SnapshotConfig config) {
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
            Snapshot snapshot = new Snapshot(snapshotFile, clazz, method, objects);
            validateExpectCall(snapshot);
            calledSnapshots.add(snapshot);
            return snapshot;
        } catch (ClassNotFoundException e) {
            throw new SnapshotMatchException(e.getMessage());
        }
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
                if (method.isAnnotationPresent(Test.class) || method.isAnnotationPresent(BeforeClass.class)) {
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
