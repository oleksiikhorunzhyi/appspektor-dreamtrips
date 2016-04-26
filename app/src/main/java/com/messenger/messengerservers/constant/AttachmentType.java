package com.messenger.messengerservers.constant;

import android.support.annotation.StringDef;

public class AttachmentType {
    public static final String IMAGE = "image";
    public static final String LOCATION = "location";
    public static final String UNSUPPORTED = "unsupported";

    @StringDef({IMAGE, LOCATION, UNSUPPORTED})
    public @interface Type {
    }
}
