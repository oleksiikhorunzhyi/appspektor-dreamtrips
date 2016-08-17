package com.messenger.messengerservers.constant;

import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class ConversationStatus {
   public static final String PRESENT = "present";
   public static final String KICKED = "kicked";
   public static final String LEFT = "left";

   @Retention(RetentionPolicy.SOURCE)
   @StringDef({PRESENT, KICKED, LEFT})
   public @interface Status {}
}