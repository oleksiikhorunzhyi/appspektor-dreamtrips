package com.worldventures.dreamtrips.wallet.util;

import android.content.Context;
import android.support.annotation.Nullable;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.wallet.ui.common.helper.OperationSubscriberWrapper;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.techery.janet.command.exception.CommandServiceException;
import io.techery.janet.helper.JanetActionException;
import io.techery.janet.smartcard.exception.NotConnectedException;
import io.techery.janet.smartcard.exception.SmartCardServiceException;
import rx.functions.Action1;
import rx.functions.Func1;

@Singleton
public class ThrowableHelper {

   private final Context appContext;

   @Inject
   public ThrowableHelper(Context appContext) {
      this.appContext = appContext;
   }

   public <T> Func1<Throwable, OperationSubscriberWrapper.MessageActionHolder<T>> provideMessageHolder() {
      return provideMessageHolder(null);
   }

   public <T> Func1<Throwable, OperationSubscriberWrapper.MessageActionHolder<T>> provideMessageHolder(@Nullable Action1<T> action) {
      return throwable -> new OperationSubscriberWrapper.MessageActionHolder<>(provideErrorMessage(throwable), action);
   }

   public String provideErrorMessage(Throwable throwable) {
      if (throwable instanceof CommandServiceException) {
         return provideErrorMessage(throwable.getCause());
      }
      if (throwable instanceof JanetActionException) {
         return provideErrorMessage(throwable.getCause());
      }
      if (throwable instanceof SmartCardServiceException) {
         return handleSmartCardThrowable(throwable.getCause());
      }
      return appContext.getString(R.string.error_something_went_wrong);
   }

   private String handleSmartCardThrowable(Throwable throwable) {
      if (throwable instanceof NotConnectedException) {
         return appContext.getString(R.string.wallet_smart_card_is_disconnected);
      }
      return appContext.getString(R.string.error_something_went_wrong);
   }
}
