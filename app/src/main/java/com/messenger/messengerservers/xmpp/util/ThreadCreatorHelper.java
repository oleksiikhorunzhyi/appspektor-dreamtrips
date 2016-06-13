package com.messenger.messengerservers.xmpp.util;

public final class ThreadCreatorHelper {

    private ThreadCreatorHelper() {
    }

    public static String obtainThreadSingleChat(String userId1, String userId2) {
        if (userId1.compareTo(userId2) < 0)
            return String.format("%s_%s", userId1, userId2);
        else
            return String.format("%s_%s", userId2, userId1);
    }

    public static String obtainCompanionIdFromSingleChatId(String conversationId, String userId) {
        return conversationId.replace(userId, "").replace("_", "");
    }

}
