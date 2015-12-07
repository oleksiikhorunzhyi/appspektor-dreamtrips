package com.messenger.messengerservers.xmpp.util;

import com.messenger.messengerservers.entities.User;

public final class JidCreatorHelper {

    public static final String SERVICE_NAME = "worldventures.com";

    private JidCreatorHelper(){

    }

    public static String obtainJid(User user){
        return user.getUserName() + "@" + SERVICE_NAME;
    }

    public static String obtainGroupJid(User user){
        return String.format("test3_room_%s@conference.%s", user.getUserName(), SERVICE_NAME/*, user.getUserName()*/);
    }

    public static User obtainUser(String jid){
        int pos = jid.indexOf("@");
        String userName = jid.substring(0, pos);
        return new User(userName);
    }

    public static String obtainThreadSingleChat(String user1Jid, String user2Jid){
        if (user1Jid.compareTo(user2Jid) == 0)
            throw new IllegalArgumentException("the users are the same");
        if (user1Jid.compareTo(user2Jid) < 0)
            return String.format("%s_%s", user1Jid, user2Jid);
        else
            return String.format("%s_%s", user2Jid, user1Jid);
    }
}
