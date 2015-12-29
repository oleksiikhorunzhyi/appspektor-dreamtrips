package com.messenger.synchmechanism;


import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class Status {

    public static final int CONNECTED = 1;
    public static final int CONNECTING = 2;
    public static final int DISCONNECTED = 3;
    public static final int ERROR = 4;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({CONNECTED, CONNECTING, DISCONNECTED, ERROR})
    public @interface MessengerConnectorStatus{

    }

}
