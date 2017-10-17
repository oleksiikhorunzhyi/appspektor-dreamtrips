package com.worldventures.dreamtrips.wallet.ui.common.helper2.error;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;

import org.jetbrains.annotations.Nullable;

import io.techery.janet.operationsubscriber.view.ErrorView;
import rx.functions.Action1;

public class RetryDialogErrorViewProvider<T> implements ErrorViewProvider<T> {

   private final Class<? extends Throwable> throwable;
   private final ErrorView<T> errorView;

   public RetryDialogErrorViewProvider(Context context, Class<? extends Throwable> throwable, @StringRes int message, @NonNull Action1<T> retryAction) {
      this.throwable = throwable;
      this.errorView = new RetryDialogErrorView<>(context, message, retryAction);
   }

   public RetryDialogErrorViewProvider(Context context, Class<? extends Throwable> throwable, @StringRes int message, @NonNull Action1<T> retryAction, @Nullable Action1<T> cancelAction) {
      this.throwable = throwable;
      this.errorView = new RetryDialogErrorView<>(context, message, retryAction, cancelAction);
   }

   @Override
   public Class<? extends Throwable> forThrowable() {
      return throwable;
   }

   @Nullable
   @Override
   public ErrorView<T> create(T t, Throwable parentThrowable, Throwable throwable) {
      return errorView;
   }
}
