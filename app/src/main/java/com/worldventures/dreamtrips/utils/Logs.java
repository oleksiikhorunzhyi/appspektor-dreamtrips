package com.worldventures.dreamtrips.utils;


import android.util.Log;

public class Logs {

    public static final String TAG_LOGGER = "LOGGER";
    private static String defaultTag = TAG_LOGGER;
    private static boolean isDebug = true;

    public static void init(String defaultTag, boolean isDebug) {
        Logs.defaultTag = defaultTag;
        Logs.isDebug = isDebug;
    }


    private static void logHandledException(Throwable t) {
        //todo need added handled exception processing
    }

    public static void v(String message) {
        v(defaultTag, message);
    }

    public static void v(String tag, String message) {
        if (isDebug) Log.v(tag, message);
    }

    public static void d(String message) {
        d(defaultTag, message);
    }

    public static void d(Throwable t) {
        d(defaultTag, t);
    }

    public static void d(String tag, String message) {
        if (isDebug) Log.d(tag, message);
    }

    public static void d(String tag, Throwable t) {
        d(tag, "", t);
    }

    public static void d(String tag, String message, Throwable t) {
        logHandledException(t);
        if (isDebug) Log.d(tag, message == null ? "" : message, t);
    }

    public static void i(String message) {
        i(defaultTag, message);
    }

    public static void i(String tag, String message) {
        if (isDebug) Log.i(tag, message);
    }

    public static void w(String message) {
        w(defaultTag, message);
    }

    public static void w(String tag, String message) {
        if (isDebug) Log.w(tag, message);
    }

    public static void e(String message) {
        e(defaultTag, message);
    }

    public static void e(String tag, String message) {
        if (isDebug) Log.e(tag, message);
    }

    public static void e(String tag, String message, Throwable t) {
        logHandledException(t);
        if (isDebug && t != null) Log.e(tag, message == null ? "" : message, t);
    }

    public static void e(Throwable t) {
        e(defaultTag, t);
    }

    public static void e(String tag, Throwable t) {
        logHandledException(t);
        if (isDebug && t != null) {
            Log.e(tag, "", t);
            t.printStackTrace();
        }
    }

    public static void exception(RuntimeException e) {
        if (isDebug) {
            throw e;
        }
    }
}