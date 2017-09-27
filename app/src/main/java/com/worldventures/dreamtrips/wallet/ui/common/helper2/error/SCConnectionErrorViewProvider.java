package com.worldventures.dreamtrips.wallet.ui.common.helper2.error;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.worldventures.dreamtrips.R;

import io.techery.janet.operationsubscriber.view.ErrorView;
import io.techery.janet.smartcard.exception.NotConnectedException;
import rx.functions.Action1;

public class SCConnectionErrorViewProvider<T> implements ErrorViewProvider<T> {

   private final Context context;
   private final Action1<T> posAction;
   private final Action1<T> negAction;

   public SCConnectionErrorViewProvider(Context context) {
      this(context, null, t -> {
      });
   }

   public SCConnectionErrorViewProvider(Context context, @Nullable Action1<T> posAction, @NonNull Action1<T> negAction) {
      this.context = context;
      this.posAction = posAction;
      this.negAction = negAction;
   }

   @Override
   public Class<? extends Throwable> forThrowable() {
      return NotConnectedException.class;
   }

   @Override
   public ErrorView<T> create(T t, Throwable parentThrowable, Throwable throwable) {
      return new SimpleErrorView<>(context, context.getString(R.string.wallet_smart_card_is_disconnected),
            negAction, R.string.ok, posAction, R.string.wallet_retry_label);
   }
}
