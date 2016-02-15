package com.messenger.messengerservers.constant;

import android.support.annotation.StringDef;

public class AttachmentType {
    public static final String IMAGE = "image";
    public static final String UNSUPPORTED = "unsupported";

    @StringDef({IMAGE, UNSUPPORTED})
    public @interface Type {
    }
}
