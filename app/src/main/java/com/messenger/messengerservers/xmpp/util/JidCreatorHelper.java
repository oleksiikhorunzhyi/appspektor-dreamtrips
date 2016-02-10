package com.messenger.messengerservers.xmpp.util;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.messenger.messengerservers.model.User;

import java.util.UUID;

public final class JidCreatorHelper {

    public static final String SERVICE_NAME = "worldventures.com";
    public static final String GROUP_SERVICE_NAME = "conference.worldventures.com";

    private JidCreatorHelper() {

    }

    public static String obtainUserJid(String userId) {
        return TextUtils.isEmpty(userId) ? null : userId + "@" + SERVICE_NAME;
    }

    public static String obtainGroupJid(String roomName) {
        return (roomName != null ? roomName : UUID.randomUUID().toString()) + "@" + GROUP_SERVICE_NAME;
    }

    @Deprecated
    public static User obtainUser(String jid) {
        return new User(obtainId(jid));
    }

    public static String obtainId(@NonNull String jid) {
        int pos = jid.indexOf("@");
        return jid.substring(0, pos);
    }

    public static String obtainUserIdFromGroupJid(String roomJidWithResource) {
        int pos = roomJidWithResource.lastIndexOf("/");
        return pos == -1 ? null : roomJidWithResource.substring(pos + 1);
    }


    public static boolean isGroupJid(String jid) {
        return !TextUtils.isEmpty(jid) && jid.contains(GROUP_SERVICE_NAME);
    }

}
