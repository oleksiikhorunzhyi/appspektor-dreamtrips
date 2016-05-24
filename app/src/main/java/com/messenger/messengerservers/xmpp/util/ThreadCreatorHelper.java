package com.messenger.messengerservers.xmpp.util;

public final class ThreadCreatorHelper {

    private ThreadCreatorHelper() {
    }

    public static String obtainThreadSingleChatFromJids(String user1Jid, String user2Jid) {
        String user1JidBare = user1Jid.substring(0, user1Jid.indexOf("@"));
        String user2JidBare = user2Jid.substring(0, user2Jid.indexOf("@"));

        return obtainThreadSingleChat(user1JidBare, user2JidBare);
    }

    public static String obtainThreadSingleChat(String userId1, String userId2) {
        if (userId1.compareTo(userId2) < 0)
            return String.format("%s_%s", userId1, userId2);
        else
            return String.format("%s_%s", userId2, userId1);
    }

    public static String obtainCompanionFromSingleChat(String conversationId, String userJid) {
        String userId = conversationId
                .replace(userJid.split("@")[0], "")
                .replace("_", "")
                //// TODO: 12/15/15  remove after implemented social graph
                .replace("yu", "y_u");
        return JidCreatorHelper.obtainUserJid(userId);
    }
}
