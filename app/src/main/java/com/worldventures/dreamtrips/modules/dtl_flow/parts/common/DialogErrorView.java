package com.worldventures.dreamtrips.modules.dtl_flow.parts.common;

import android.app.Dialog;
import android.content.Context;

import cn.pedant.SweetAlert.SweetAlertDialog;
import io.techery.janet.operationsubscriber.view.ErrorView;

public abstract class DialogErrorView<T> implements ErrorView<T> {

   protected final Context context;
   private Dialog dialog;

   protected DialogErrorView(Context context) {
      this.context = context;
   }

   protected abstract SweetAlertDialog createDialog(T t, Throwable throwable, Context context);

   @Override
   public void showError(T t, Throwable throwable) {
      dialog = createDialog(t, throwable, context);
      dialog.show();
   }

   @Override
   public final boolean isErrorVisible() {
      return dialog != null && dialog.isShowing();
   }

   @Override
   public final void hideError() {
      if (dialog != null) {
         dialog.dismiss();
      }
   }
}
