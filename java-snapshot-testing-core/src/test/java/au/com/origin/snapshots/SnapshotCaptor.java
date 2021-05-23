package au.com.origin.snapshots;

import au.com.origin.snapshots.exceptions.SnapshotExtensionException;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

class SnapshotCaptor {

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
                    if (field.getType().isPrimitive()) {
                        field.setByte(newValue, Integer.valueOf(0).byteValue());
                    } else {
                        field.set(newValue, null);
                    }
                } catch (IllegalAccessException | NoSuchFieldException e) {
                    throw new SnapshotExtensionException("Invalid Ignore value " + each, e.getCause());
                }
            }
        }
        return newValue;
    }

    private Object shallowCopy(Object value) {
        try {
            Object newValue = constructCopy(this.argumentClass);

            Field[] fields = this.argumentClass.getDeclaredFields();

            for (Field field : fields) {
                field.setAccessible(true);
                try {
                    field.set(newValue, field.get(value));
                } catch (Exception e) {
                    // ignore
                }
            }
            return newValue;
        } catch (Exception e) {
            throw new SnapshotExtensionException(
                    "Class "
                            + this.argumentClass.getSimpleName()
                            + " must have a default empty constructor!");
        }
    }

    private Object constructCopy(Class<?> argumentClass)
            throws InstantiationException, IllegalAccessException,
            java.lang.reflect.InvocationTargetException, NoSuchMethodException {

        try {
            return argumentClass.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            // Ignore - should log
        }

        Constructor[] constructors = argumentClass.getDeclaredConstructors();

        if (constructors.length == 0) {
            return argumentClass.getDeclaredConstructor().newInstance();
        }

        int i = 0;
        Class[] types = constructors[i].getParameterTypes();
        Object[] paramValues = new Object[types.length];

        for (int j = 0; j < types.length; j++) {
            if (types[j].isPrimitive()) {
                paramValues[j] = Integer.valueOf(0).byteValue();
            } else {
                paramValues[j] = constructCopy(types[j]);
            }
        }
        return constructors[i].newInstance(paramValues);
    }
}
