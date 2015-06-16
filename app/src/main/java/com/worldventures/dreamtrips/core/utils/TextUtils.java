package com.worldventures.dreamtrips.core.utils;

public class TextUtils {

    public static String joinWithFirstUpperCase(String[] groups) {
        String result = "";
        for (String group : groups) {
            result = result + ", " + convertToFirstUpperCase(group);
        }
        return result.substring(result.indexOf(",") + 1);
    }

    public static String convertToFirstUpperCase(String text) {
        if (text.length() > 1) {
            text = text.substring(0, 1).toUpperCase() + text.substring(1).toLowerCase();
        }
        return text;
    }
}
