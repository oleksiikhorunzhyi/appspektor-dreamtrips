package com.worldventures.dreamtrips.wallet.ui.common.helper2.error;

import android.content.Context;
import android.support.annotation.StringRes;

import com.afollestad.materialdialogs.MaterialDialog;

import org.jetbrains.annotations.Nullable;

import io.techery.janet.operationsubscriber.view.ErrorView;

public class SimpleDialogErrorViewProvider<T> implements ErrorViewProvider<T> {

   private Class<? extends Throwable> throwable;
   private ErrorView<T> errorView;

   public SimpleDialogErrorViewProvider(Context context, Class<? extends Throwable> throwable, @StringRes int messageResId) {
      this.throwable = throwable;
      this.errorView = new SimpleErrorDialogView<>(context, messageResId);
   }

   public SimpleDialogErrorViewProvider(Context context, Class<? extends Throwable> throwable, @StringRes int messageResId, MaterialDialog.SingleButtonCallback defaultAction) {
      this.throwable = throwable;
      this.errorView = new SimpleErrorDialogView<>(context, messageResId, defaultAction);
   }

   @Override
   public Class<? extends Throwable> forThrowable() {
      return throwable;
   }

   @Nullable
   @Override
   public ErrorView<T> create(T t, Throwable throwable) {
      return errorView;
   }
}
