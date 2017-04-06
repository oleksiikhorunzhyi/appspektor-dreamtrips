package com.worldventures.dreamtrips.api.tests.util;

import com.worldventures.dreamtrips.api.api_common.BaseHttpAction;
import com.worldventures.dreamtrips.api.api_common.error.ErrorResponse;

import java.util.Arrays;

public class ServerUtil {
    private ServerUtil() {}

    public static void waitForServerLag() {
        waitForServerLag(500L);
    }

    public static void waitForServerLag(long delay) {
        synchronized (Thread.currentThread()) {
            try {
                Thread.currentThread().wait(delay);
            } catch (InterruptedException e) {
            }
        }
    }

    public static boolean hasError(BaseHttpAction action, String error) {
        ErrorResponse errorResponse = action.errorResponse();
        if (errorResponse == null) return false;
        //
        for (String[] reasons : errorResponse.errors().values()) {
            if (Arrays.binarySearch(reasons, error) != -1) return true;
        }
        return false;
    }
}
