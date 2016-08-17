package com.messenger.messengerservers.xmpp.chats;

import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public final class Status {
   public static final String DISPLAYED = "displayed";
   public static final String UNREAD = "unread";
   public static final String DELETE = "delete";

   @Retention(RetentionPolicy.SOURCE)
   @StringDef({DISPLAYED, UNREAD, DELETE})
   public @interface MessageStatus {}
}
