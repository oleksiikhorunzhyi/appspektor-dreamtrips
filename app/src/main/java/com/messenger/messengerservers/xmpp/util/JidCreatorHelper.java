package com.messenger.messengerservers.xmpp.util;

import com.messenger.messengerservers.entities.User;

import java.util.UUID;

public final class JidCreatorHelper {

    public static final String SERVICE_NAME = "worldventures.com";

    private JidCreatorHelper() {

    }

    public static String obtainUserJid(String userId) {
        return userId.isEmpty() ? null : userId + "@" + SERVICE_NAME;
    }

    public static String obtainGroupJid(String roomName) {
        return String.format("%s@conference.%s", roomName != null? roomName : UUID.randomUUID().toString(), SERVICE_NAME);
    }

    public static User obtainUser(String jid) {
        int pos = jid.indexOf("@");
        String userName = jid.substring(0, pos);
        return new User(userName);
    }

}
