package com.messenger.messengerservers.constant;

import android.support.annotation.IntDef;

public class MessageStatus {
   public static final int ERROR = -1;
   public static final int SENDING = 0;
   public static final int SENT = 1;
   public static final int READ = 2;

   @IntDef({ERROR, SENDING, SENT, READ})
   public @interface Status {}
}