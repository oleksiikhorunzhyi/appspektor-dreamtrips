package com.worldventures.dreamtrips.util;

public class ThrowableUtils {

    public static <T> T getCauseByType(Class<T> causeType, Throwable exception) {
        if (causeType.isInstance(exception)) {
            return (T) exception;
        }
        if (exception.getCause() != null) {
            return getCauseByType(causeType, exception.getCause());
        }
        return null;
    }

}
