package com.messenger.messengerservers.xmpp.util;

import android.text.TextUtils;

import com.messenger.messengerservers.entities.User;

import java.util.UUID;

public final class JidCreatorHelper {

    public static final String SERVICE_NAME = "worldventures.com";

    private JidCreatorHelper() {

    }

    public static String obtainUserJid(String userId) {
        return TextUtils.isEmpty(userId) ? null : userId + "@" + SERVICE_NAME;
    }

    public static String obtainGroupJid(String roomName) {
        return String.format("%s@conference.%s", roomName != null ? roomName : UUID.randomUUID().toString(), SERVICE_NAME);
    }

    @Deprecated
    public static User obtainUser(String jid) {
        return new User(obtainId(jid));
    }

    public static String obtainId(String jid) {
        int pos = jid.indexOf("@");
        return jid.substring(0, pos);
    }

}
