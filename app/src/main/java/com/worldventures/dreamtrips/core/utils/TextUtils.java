package com.worldventures.dreamtrips.core.utils;

public class TextUtils {

    public static String joinWithFirstUpperCase(Object[] groups) {
        String result = "";
        for (Object group : groups) {
            result = result + ", " + convertToFirstUpperCase(group.toString());
        }
        return result.substring(result.indexOf(",") + 1);
    }

    public static String convertToFirstUpperCase(String text) {
        if (text.length() > 1) {
            text = text.substring(0, 1).toUpperCase() + text.substring(1).toLowerCase();
        }
        return text;
    }

    /**
     * Returns a string containing the tokens joined by delimiters.
     *
     * @param tokens varargs to be joined. Strings will be formed from
     *               the objects by calling object.toString().
     */
    public static String join(CharSequence delimiter, Object... tokens) {
        StringBuilder sb = new StringBuilder();
        boolean firstTime = true;
        for (Object token : tokens) {
            if (firstTime) {
                firstTime = false;
            } else {
                sb.append(delimiter);
            }
            sb.append(token);
        }
        return sb.toString();
    }
}
