package com.worldventures.dreamtrips.modules.dtl_flow.parts.common;

import android.content.Context;

import com.worldventures.wallet.ui.common.helper2.error.ErrorViewProvider;

import org.jetbrains.annotations.Nullable;

import io.techery.janet.operationsubscriber.view.ErrorView;

public class SweetDialogErrorViewProvider<T> implements ErrorViewProvider<T> {

   private final Class<? extends Throwable> throwable;
   private final SimpleSweetDialogErrorView<T> errorView;

   public SweetDialogErrorViewProvider(Context context, Class<? extends Throwable> throwable, SweetDialogParams<T> params) {
      this.throwable = throwable;
      this.errorView = new SimpleSweetDialogErrorView<>(context, params);
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
