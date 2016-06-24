package com.messenger.messengerservers.constant;

import android.support.annotation.StringDef;

public class MessageType {
    public static final String USER = "user";
    public static final String SYSTEM_JOIN = "join";
    public static final String SYSTEM_LEAVE = "leave";
    public static final String SYSTEM_KICK = "kick";

    @StringDef({USER, SYSTEM_JOIN, SYSTEM_LEAVE, SYSTEM_KICK})
    public @interface Type {
    }
}