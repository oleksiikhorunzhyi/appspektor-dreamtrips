package com.worldventures.dreamtrips.modules.feed.view.util;

public class HashtagSuggestionUtil {

    public static String generateText(String descriptionText, String suggestion, int cursorPos) {
        int startReplace = calcStartPosBeforeReplace(descriptionText, cursorPos);
        String toReplace = replaceableText(suggestion);
        int endIndex = (cursorPos < descriptionText.length() && descriptionText.charAt(cursorPos) == ' ') ? cursorPos + 1 : cursorPos;
        return new StringBuffer(descriptionText).replace(startReplace, endIndex, toReplace).toString();
    }

    public static int calcStartPosBeforeReplace(String descriptionText, int cursorPos) {
        int dashPos = descriptionText.substring(0, cursorPos).lastIndexOf("#");
        if (dashPos != -1) return dashPos;
        else return cursorPos;
    }

    public static String replaceableText(String suggestion) {
        suggestion = suggestion.contains("#") ? suggestion : "#" + suggestion;
        String toReplace = String.format("%s ", suggestion);
        return toReplace;
    }
}
