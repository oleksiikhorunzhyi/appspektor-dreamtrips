package com.messenger.messengerservers.chat;

import android.support.annotation.StringDef;

public class ChatState {
    public static final String PAUSE = "pause";
    public static final String COMPOSING = "composing";

    @StringDef({PAUSE, COMPOSING})
    public @interface State {}
}
