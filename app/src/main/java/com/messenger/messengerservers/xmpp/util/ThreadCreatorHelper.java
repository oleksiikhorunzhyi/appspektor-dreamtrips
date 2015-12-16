package com.messenger.messengerservers.xmpp.util;

public final class ThreadCreatorHelper {

    private ThreadCreatorHelper() {
    }

    public static String obtainThreadSingleChat(String user1Jid, String user2Jid) {
        if (user1Jid.compareTo(user2Jid) == 0)
            throw new IllegalArgumentException("the users are the same");
        if (user1Jid.compareTo(user2Jid) < 0)
            return String.format("%s_%s", user1Jid, user2Jid);
        else
            return String.format("%s_%s", user2Jid, user1Jid);
    }


}
