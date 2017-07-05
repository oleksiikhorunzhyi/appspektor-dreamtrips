package com.worldventures.dreamtrips.wallet.ui.common.helper2.error;

import android.content.Context;

import com.worldventures.dreamtrips.R;

import io.techery.janet.operationsubscriber.view.ErrorView;
import io.techery.janet.smartcard.exception.NotConnectedException;
import rx.functions.Action1;

public class SCConnectionErrorViewProvider<T> implements ErrorViewProvider<T> {

   private final Context context;
   private final Action1<T> action;

   public SCConnectionErrorViewProvider(Context context) {
      this(context, t -> {});
   }

   public SCConnectionErrorViewProvider(Context context, Action1<T> action) {
      this.context = context;
      this.action = action;
   }

   @Override
   public Class<? extends Throwable> forThrowable() {
      return NotConnectedException.class;
   }

   @Override
   public ErrorView<T> create(T t, Throwable throwable) {
      return new SimpleErrorView<>(context, action, context.getString(R.string.wallet_smart_card_is_disconnected));
   }
}
