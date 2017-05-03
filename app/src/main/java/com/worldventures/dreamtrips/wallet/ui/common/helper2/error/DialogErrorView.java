package com.worldventures.dreamtrips.wallet.ui.common.helper2.error;

import android.app.Dialog;
import android.content.Context;

import io.techery.janet.operationsubscriber.view.ErrorView;

public abstract class DialogErrorView<T> implements ErrorView<T> {

   protected final Context context;
   private Dialog dialog;

   protected DialogErrorView(Context context) {
      this.context = context;
   }

   protected abstract Dialog createDialog(T t, Throwable throwable, Context context);

   @Override
   public final void showError(T t, Throwable throwable) {
      dialog = createDialog(t, throwable, context);
      dialog.show();
   }

   @Override
   public final boolean isErrorVisible() {
      return dialog != null && dialog.isShowing();
   }

   @Override
   public final void hideError() {
      if (dialog != null) dialog.dismiss();
   }

}
