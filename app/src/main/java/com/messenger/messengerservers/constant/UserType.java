package com.messenger.messengerservers.constant;

import android.support.annotation.StringDef;

public class UserType {
    public static final String FRIEND = "friend";
    public static final String CLOSE_FRIEND = "close_friend";
//    public static final String OTHER = "other";

    @StringDef({FRIEND, CLOSE_FRIEND})
    public @interface Type {}
}
