package com.worldventures.dreamtrips.wallet.ui.common.helper2.success;

import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.view.View;

public class ActionSnackbarSuccessView<T> extends SnackbarSuccessView<T> {

   private final View view;
   private final int message;
   private final int action;
   private final View.OnClickListener onClickListener;
   private final int duration;

   public ActionSnackbarSuccessView(@NonNull View view, @StringRes int message,
         @StringRes int action, @NonNull View.OnClickListener onClickListener) {
      this(view, message, action, onClickListener, Snackbar.LENGTH_SHORT);
   }

   public ActionSnackbarSuccessView(@NonNull View view, @StringRes int message,
         @StringRes int action, @NonNull View.OnClickListener onClickListener, int duration) {
      this.view = view;
      this.message = message;
      this.action = action;
      this.onClickListener = onClickListener;
      this.duration = duration;
   }

   @Override
   Snackbar createSnackbar(T t) {
      return Snackbar.make(view, message, duration).setAction(action, onClickListener);
   }
}
