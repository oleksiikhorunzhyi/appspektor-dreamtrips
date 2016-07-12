package com.worldventures.dreamtrips.modules.feed.view.util;

public class HashtagSuggestionUtil {

    public static String generateText(String descriptionText, String suggestion, int cursorPos) {
        int startReplace = calcStartPosBeforeReplace(descriptionText, cursorPos);
        int endIndex = calEndPosBeforeReplace(descriptionText, cursorPos) + 1;
        String toReplace = replaceableText(suggestion);
        return new StringBuffer(descriptionText).replace(startReplace, endIndex, toReplace).toString();
    }

    public static int calcStartPosBeforeReplace(String descriptionText, int cursorPos) {
        String replaceableText = descriptionText.substring(0, cursorPos);
        int hashPosition = replaceableText.lastIndexOf("#");
        int spacePosition = replaceableText.lastIndexOf(" ");
        if (hashPosition != -1 && spacePosition != -1) {
            return hashPosition > spacePosition ? hashPosition : spacePosition + 1;
        } else {
            if (hashPosition != -1) return hashPosition;
            else if (spacePosition != -1) return spacePosition + 1;
            else return 0;
        }
    }

    public static int calEndPosBeforeReplace(String descriptionText, int cursorPos) {
        String replaceableText = descriptionText.substring(cursorPos);
        int spacePosition = replaceableText.indexOf(" ");
        if (spacePosition != -1) {
            return cursorPos + spacePosition;
        } else {
            return descriptionText.length() - 1;
        }
    }

    public static String replaceableText(String suggestion) {
        suggestion = suggestion.contains("#") ? suggestion : "#" + suggestion;
        String toReplace = String.format("%s ", suggestion);
        return toReplace;
    }
}
