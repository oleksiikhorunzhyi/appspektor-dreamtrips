package com.worldventures.dreamtrips.wallet.ui.common.helper2.progress;

import android.content.Context;

import com.afollestad.materialdialogs.MaterialDialog;

import io.techery.janet.operationsubscriber.view.ProgressView;

public abstract class DialogProgressView<T> implements ProgressView<T> {

   protected final Context context;
   private MaterialDialog progressDialog;

   public DialogProgressView(Context context) {
      this.context = context;
   }

   @Override
   public void showProgress(T t) {
      if (progressDialog == null) {
         progressDialog = createDialog(t, context);
      }
      progressDialog.show();
   }

   @Override
   public boolean isProgressVisible() {
      return progressDialog != null && progressDialog.isShowing();
   }

   @Override
   public void hideProgress() {
      if (progressDialog != null) {
         progressDialog.dismiss();
         progressDialog = null;
      }
   }

   protected abstract MaterialDialog createDialog(T t, Context context);

}
