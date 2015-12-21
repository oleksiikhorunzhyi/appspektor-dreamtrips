package com.messenger.messengerservers.xmpp.util;

import com.messenger.messengerservers.entities.Conversation;
import com.messenger.messengerservers.entities.User;

public final class ThreadCreatorHelper {

    private ThreadCreatorHelper() {
    }

    public static String obtainThreadSingleChat(String user1Jid, String user2Jid) {
        String user1JidBare = user1Jid.substring(0, user1Jid.indexOf("@"));
        String user2JidBare = user2Jid.substring(0, user2Jid.indexOf("@"));

        if (user1JidBare.compareTo(user2JidBare) == 0)
            throw new IllegalArgumentException("the users are the same");
        if (user1JidBare.compareTo(user2JidBare) < 0)
            return String.format("%s_%s", user1JidBare, user2JidBare);
        else
            return String.format("%s_%s", user2JidBare, user1JidBare);
    }

    public static String obtainThreadSingleChat(User user1, User user2) {
        String user1Jid = JidCreatorHelper.obtainUserJid(user1.getUserName());
        String user2Jid = JidCreatorHelper.obtainUserJid(user2.getUserName());

        return obtainThreadSingleChat(user1Jid, user2Jid);
    }

    public static String obtainCompanionFromSingleChat(Conversation conversation, String userJid) {
        String userId = conversation.getId()
                .replace(userJid.split("@")[0], "")
                .replace("_", "")
                //// TODO: 12/15/15  remove after implemented social graph
                .replace("yu", "y_u");
        return JidCreatorHelper.obtainUserJid(userId);
    }
}
