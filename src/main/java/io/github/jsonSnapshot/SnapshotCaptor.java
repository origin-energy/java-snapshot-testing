package io.github.jsonSnapshot;

import java.lang.reflect.Field;

public class SnapshotCaptor {

    private Class<?> parameterClass;

    private Class<?> argumentClass;

    private String[] ignore;

    public SnapshotCaptor(Class<?> parameterClass, String... ignore) {
        this.parameterClass = parameterClass;
        this.argumentClass = parameterClass;
        this.ignore = ignore;
    }

    public SnapshotCaptor(Class<?> parameterClass, Class<?> argumentClass, String... ignore) {
        this.parameterClass = parameterClass;
        this.argumentClass = argumentClass;
        this.ignore = ignore;
    }

    public Class<?> getParameterClass() {
        return parameterClass;
    }

    public Object removeIgnored(Object value) {
        Object newValue = value;
        if (ignore != null && ignore.length > 0) {
            newValue = shallowCopy(value);
            for (String each : ignore) {
                try {
                    Field field = this.argumentClass.getDeclaredField(each);
                    field.setAccessible(true);
                    field.set(newValue, null);
                } catch (IllegalAccessException | NoSuchFieldException e) {
                    throw new SnapshotMatchException("Invalid Ignore value " + each, e.getCause());
                }
            }
        }
        return newValue;
    }

    private Object shallowCopy(Object value) {
        try {
            Object newValue = this.argumentClass.newInstance();
            Field[] fields = this.argumentClass.getDeclaredFields();

            for (Field field: fields) {
                field.setAccessible(true);
                try {
                    field.set(newValue, field.get(value));
                }
                catch(Exception e) {
                    //ignore
                }
            }
            return newValue;
        }
        catch (Exception e) {
            throw new SnapshotMatchException("Class "+ this.argumentClass.getSimpleName() + " must have a default empty constructor!");
        }
    }
}
