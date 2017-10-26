package com.worldventures.wallet.ui.common.helper2.success;

import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;

import io.techery.janet.operationsubscriber.view.SuccessView;

abstract class SnackbarSuccessView<T> implements SuccessView<T> {

   @Nullable
   private Snackbar snackbar;

   @Override
   public void showSuccess(T t) {
      if (snackbar == null) {
         snackbar = createSnackbar(t);
      }
      snackbar.show();
   }

   @Override
   public boolean isSuccessVisible() {
      return snackbar != null && snackbar.isShown();
   }

   @Override
   public void hideSuccess() {
      if (snackbar != null) {
         snackbar.dismiss();
         snackbar = null;
      }
   }

   abstract Snackbar createSnackbar(T t);
}
