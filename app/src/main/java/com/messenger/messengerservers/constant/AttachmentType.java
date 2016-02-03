package com.messenger.messengerservers.constant;

import android.support.annotation.StringDef;

public class AttachmentType {
    public static final String IMAGE = "image";
    public static final String LOCATION = "location";
    public static final String BUCKET_LIST = "bucket_list";
    public static final String MERCHAN = "merchan";

    @StringDef({IMAGE, LOCATION, BUCKET_LIST, MERCHAN})
    public @interface Type{}
}
