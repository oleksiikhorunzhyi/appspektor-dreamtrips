package com.worldventures.dreamtrips.wallet.ui.common.helper2.error;

import android.content.Context;
import android.support.annotation.StringRes;

import org.jetbrains.annotations.Nullable;

import io.techery.janet.operationsubscriber.view.ErrorView;
import rx.functions.Action0;
import rx.functions.Action1;

public class SimpleDialogErrorViewProvider<T> implements ErrorViewProvider<T> {

   private Class<? extends Throwable> throwable;
   private SimpleErrorDialogView<T> errorView;

   public SimpleDialogErrorViewProvider(Context context, Class<? extends Throwable> throwable, @StringRes int messageResId) {
      this.throwable = throwable;
      this.errorView = new SimpleErrorDialogView<>(context, messageResId);
   }

   public SimpleDialogErrorViewProvider(Context context, Class<? extends Throwable> throwable, @StringRes int messageResId, Action1<T> defaultAction) {
      this.throwable = throwable;
      this.errorView = new SimpleErrorDialogView<>(context, messageResId, defaultAction);
   }

   public SimpleDialogErrorViewProvider(Context context, Class<? extends Throwable> throwable, @StringRes int messageResId, Action1<T> positiveAction, Action1<T> negativeAction) {
      this.throwable = throwable;
      this.errorView = new SimpleErrorDialogView<>(context, messageResId, positiveAction, negativeAction);
   }

   public SimpleDialogErrorViewProvider(Context context, Class<? extends Throwable> throwable, @StringRes int messageResId,
         Action1<T> positiveAction, Action1<T> negativeAction, Action0 dismissAction) {
      this.throwable = throwable;
      this.errorView = new SimpleErrorDialogView<>(context, messageResId, positiveAction, negativeAction, dismissAction);
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

   public void setPositiveText(@StringRes int positiveResId) {
      errorView.setPositiveText(positiveResId);
   }

   public void setNegativeText(@StringRes int negativeResId) {
      errorView.setNegativeText(negativeResId);
   }
}
