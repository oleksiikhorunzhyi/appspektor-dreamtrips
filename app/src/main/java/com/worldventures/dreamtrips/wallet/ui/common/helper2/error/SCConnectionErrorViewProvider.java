package com.worldventures.dreamtrips.wallet.ui.common.helper2.error;

import android.content.Context;

import com.worldventures.dreamtrips.R;

import io.techery.janet.operationsubscriber.view.ErrorView;
import io.techery.janet.smartcard.exception.NotConnectedException;

public class SCConnectionErrorViewProvider<T> implements ErrorViewProvider<T> {

   private final Context context;

   public SCConnectionErrorViewProvider(Context context) {
      this.context = context;
   }

   @Override
   public Class<? extends Throwable> forThrowable() {
      return NotConnectedException.class;
   }

   @Override
   public ErrorView<T> create(T t, Throwable throwable) {
      return new SimpleErrorView<>(context, t1 -> {
      }, context.getString(R.string.wallet_smart_card_is_disconnected));
   }
}
