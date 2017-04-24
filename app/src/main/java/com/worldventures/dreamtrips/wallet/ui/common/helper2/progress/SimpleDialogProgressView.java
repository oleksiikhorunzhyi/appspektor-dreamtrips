package com.worldventures.dreamtrips.wallet.ui.common.helper2.progress;

import android.content.Context;
import android.content.DialogInterface;

import com.afollestad.materialdialogs.MaterialDialog;

public class SimpleDialogProgressView<T> extends DialogProgressView<T> {

   private CharSequence content;
   private boolean cancelable;

   private DialogInterface.OnCancelListener cancelListener;

   public SimpleDialogProgressView(Context context, int resIdMessage, boolean cancelable) {
      this(context, context.getString(resIdMessage), cancelable, null);
   }

   public SimpleDialogProgressView(Context context, CharSequence content, boolean cancelable) {
      this(context, content, cancelable, null);
   }

   public SimpleDialogProgressView(Context context, int resIdMessage, boolean cancelable, DialogInterface.OnCancelListener cancelListener) {
      this(context, context.getString(resIdMessage), cancelable, cancelListener);
   }

   public SimpleDialogProgressView(Context context, CharSequence content, boolean cancelable, DialogInterface.OnCancelListener cancelListener) {
      super(context);
      this.content = content;
      this.cancelable = cancelable;
      this.cancelListener = cancelListener;
   }

   @Override
   protected MaterialDialog createDialog(T t, Context context) {
      return new MaterialDialog.Builder(context)
            .progress(true, 0)
            .content(content)
            .cancelable(cancelable)
            .canceledOnTouchOutside(cancelable)
            .cancelListener(cancelListener)
            .build();
   }
}
