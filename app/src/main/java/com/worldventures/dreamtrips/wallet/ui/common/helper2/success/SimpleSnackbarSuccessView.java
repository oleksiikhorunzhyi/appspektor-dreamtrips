package com.worldventures.dreamtrips.wallet.ui.common.helper2.success;

import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.view.View;

public class SimpleSnackbarSuccessView<T> extends SnackbarSuccessView<T> {

   private final View view;
   private final int message;
   private final int duration;

   public SimpleSnackbarSuccessView(@NonNull View view, @StringRes int message) {
      this(view, message, Snackbar.LENGTH_SHORT);
   }

   public SimpleSnackbarSuccessView(@NonNull View view, @StringRes int message, int duration) {
      this.view = view;
      this.message = message;
      this.duration = duration;
   }

   @Override
   Snackbar createSnackbar(T t) {
      return Snackbar.make(view, message, duration);
   }
}
