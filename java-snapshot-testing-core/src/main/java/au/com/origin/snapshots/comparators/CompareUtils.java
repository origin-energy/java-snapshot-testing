package au.com.origin.snapshots.comparators;

import lombok.experimental.UtilityClass;

@UtilityClass
public class CompareUtils {
    public static <T> CompareResult<T> success() {
        return new CompareResult<T>(true, null);
    }

    public static <T> CompareResult<T> failure(T result) {
        return new CompareResult<T>(false, result);
    }
}
