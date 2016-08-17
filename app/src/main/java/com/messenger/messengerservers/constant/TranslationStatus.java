package com.messenger.messengerservers.constant;

import android.support.annotation.IntDef;

public class TranslationStatus {

   public static final int ERROR = -1;
   public static final int TRANSLATING = 0;
   public static final int TRANSLATED = 1;
   public static final int REVERTED = 2;

   @IntDef({ERROR, TRANSLATING, TRANSLATED, REVERTED})
   public @interface Status {}
}
