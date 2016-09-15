package com.messenger.messengerservers.constant;

import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class ConversationType {
   public static final String CHAT = "chat";
   public static final String GROUP = "group";
   public static final String TRIP = "trip";
   public static final String RINK = "rink";
   public static final String RANK = "rank";

   @Retention(RetentionPolicy.SOURCE)
   @StringDef({CHAT, GROUP, TRIP, RINK, RANK})
   public @interface Type {}
}
