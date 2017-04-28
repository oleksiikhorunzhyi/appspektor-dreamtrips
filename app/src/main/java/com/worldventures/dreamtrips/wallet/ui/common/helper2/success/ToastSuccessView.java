package com.worldventures.dreamtrips.wallet.ui.common.helper2.success;

import android.widget.Toast;

import io.techery.janet.operationsubscriber.view.SuccessView;

public abstract class ToastSuccessView<T> implements SuccessView<T> {

   private Toast toast;

   @Override
   public void showSuccess(T t) {
      if(toast == null) {
         toast = createToast(t);
      }
      toast.show();
   }

   @Override
   public boolean isSuccessVisible() {
      return toast != null;
   }

   @Override
   public void hideSuccess() {
      toast.cancel();
      toast = null;
   }

   public abstract Toast createToast(T t);
}
