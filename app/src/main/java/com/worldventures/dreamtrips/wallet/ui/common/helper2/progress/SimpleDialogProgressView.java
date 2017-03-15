package com.worldventures.dreamtrips.wallet.ui.common.helper2.progress;

import android.content.Context;
import android.content.DialogInterface;

import com.afollestad.materialdialogs.MaterialDialog;

public class SimpleDialogProgressView<T> extends DialogProgressView<T> {

   private int resIdMessage;
   private boolean cancelable;

   private DialogInterface.OnCancelListener cancelListener;

   public SimpleDialogProgressView(Context context, int resIdMessage, boolean cancelable) {
      super(context);
      this.resIdMessage = resIdMessage;
      this.cancelable = cancelable;
   }

   public SimpleDialogProgressView(Context context, int resIdMessage, boolean cancelable, DialogInterface.OnCancelListener cancelListener) {
      super(context);
      this.resIdMessage = resIdMessage;
      this.cancelable = cancelable;
      this.cancelListener = cancelListener;
   }

   @Override
   protected MaterialDialog createDialog(T t, Context context) {
      return new MaterialDialog.Builder(context)
            .progress(true, 0)
            .content(resIdMessage)
            .cancelable(cancelable)
            .canceledOnTouchOutside(cancelable)
            .cancelListener(cancelListener)
            .build();
   }
}
