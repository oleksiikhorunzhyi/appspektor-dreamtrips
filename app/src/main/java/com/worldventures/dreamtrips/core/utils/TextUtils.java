package com.worldventures.dreamtrips.core.utils;

import com.innahema.collections.query.functions.Predicate;
import com.innahema.collections.query.queriables.Queryable;

import java.util.Collections;
import java.util.List;

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

    /**
     * Give index of first occurence of subString in superString, all lowercased.
     *
     * @param superString where to search
     * @param subString   substring to search
     * @return index of first occurence, if any. Otherwise - returns {@link Integer#MAX_VALUE}
     */
    public static int substringLocation(String superString, String subString) {
        String superLowerCase = superString.toLowerCase();
        String subLowerCase = subString.toLowerCase();

        if (!superLowerCase.contains(subLowerCase)) return Integer.MAX_VALUE;

        return superLowerCase.indexOf(subLowerCase);
    }


    public static List<String> getListFromString(String temp) {
        return getListFromString(temp, ",");
    }

    public static List<String> getListFromString(String temp, String divider) {
        if (android.text.TextUtils.isEmpty(temp)) {
            return Collections.emptyList();
        } else {
            return Queryable.from(temp.split(divider))
                    .filter(element -> !android.text.TextUtils.isEmpty(element))
                    .map(String::trim)
                    .toList();
        }
    }
}
