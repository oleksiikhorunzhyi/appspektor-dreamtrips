package com.messenger.messengerservers.constant;

import android.support.annotation.IntDef;

public class TranslationStatus {

    public static final int ERROR = -1;
    public static final int NATIVE = 1;
    public static final int TRANSLATING = 2;
    public static final int TRANSLATED = 3;

    @IntDef({ERROR, NATIVE, TRANSLATING, TRANSLATED})
    public @interface Status {
    }
}
