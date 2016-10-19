package com.worldventures.dreamtrips.wallet.ui.common.helper;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import rx.functions.Action1;

public final class MessageActionHolder<T> {

   @Nullable protected final Action1<T> action;
   protected final MessageProvider<T> message;

   public MessageActionHolder(String message, @Nullable Action1<T> action) {
      this(t -> message, action);
   }

   public MessageActionHolder(@NonNull MessageProvider<T> message, @Nullable Action1<T> action) {
      this.message = message;
      this.action = action;
   }
}
