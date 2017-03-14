package com.worldventures.dreamtrips.wallet.ui.common.helper2.progress;

import android.content.Context;

import com.afollestad.materialdialogs.MaterialDialog;

import io.techery.janet.operationsubscriber.view.ProgressView;

public abstract class DialogProgressView<T> implements ProgressView<T> {

   protected final Context context;
   private MaterialDialog progressDialog;

   protected DialogProgressView(Context context) {
      this.context = context;
   }

   @Override
   public void showProgress(T t) {
      if (progressDialog == null) {
         progressDialog = createDialog(t, context);
      }
      if (!progressDialog.isShowing()) progressDialog.show();
   }

   @Override
   public boolean isProgressVisible() {
      return progressDialog != null && progressDialog.isShowing();
   }

   @Override
   public void hideProgress() {
      if (progressDialog != null) progressDialog.dismiss();
   }

   protected abstract MaterialDialog createDialog(T t, Context context);

}
