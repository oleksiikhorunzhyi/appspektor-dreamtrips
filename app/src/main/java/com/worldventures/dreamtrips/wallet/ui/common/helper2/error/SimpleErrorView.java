package com.worldventures.dreamtrips.wallet.ui.common.helper2.error;

import android.content.Context;

import com.afollestad.materialdialogs.MaterialDialog;
import com.worldventures.dreamtrips.R;

import rx.functions.Action1;

public class SimpleErrorView<T> extends DialogErrorView<T> {

   private final Action1<T> action;
   private final String message;

   public SimpleErrorView(Context context, Action1<T> action, String message) {
      super(context);
      this.action = action;
      this.message = message;
   }

   @Override
   protected MaterialDialog createDialog(T t, Throwable throwable, Context context) {
      return new MaterialDialog.Builder(context)
            .content(message)
            .cancelable(false)
            .negativeText(R.string.ok)
            .onNegative((dialog1, which) -> action.call(t))
            .build();
   }
}
