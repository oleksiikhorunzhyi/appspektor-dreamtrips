package com.worldventures.dreamtrips.wallet.ui.common.helper2.progress;

import android.content.Context;

import com.afollestad.materialdialogs.MaterialDialog;

import static com.raizlabs.android.dbflow.config.FlowManager.getContext;

public class SimpleDialogProgressView<T> extends DialogProgressView<T> {

   private int resIdMessage;
   private boolean cancelable;

   public SimpleDialogProgressView(Context context, int resIdMessage, boolean cancelable) {
      super(context);
      this.resIdMessage = resIdMessage;
      this.cancelable = cancelable;
   }

   @Override
   protected MaterialDialog createDialog(T t, Context context) {
      return new MaterialDialog.Builder(getContext())
            .progress(true, 0)
            .content(resIdMessage)
            .cancelable(cancelable)
            .canceledOnTouchOutside(cancelable)
            .build();
   }
}
