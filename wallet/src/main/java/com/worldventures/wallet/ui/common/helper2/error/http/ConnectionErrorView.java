package com.worldventures.wallet.ui.common.helper2.error.http;

import android.content.Context;

import com.afollestad.materialdialogs.MaterialDialog;
import com.worldventures.wallet.R;
import com.worldventures.wallet.ui.common.helper2.error.DialogErrorView;

import rx.functions.Action1;

public class ConnectionErrorView<T> extends DialogErrorView<T> {

   private final String message;
   private final Action1<T> retryAction;
   private final Action1<T> cancelAction;

   protected ConnectionErrorView(Context context, String message, Action1<T> retryAction, Action1<T> cancelAction) {
      super(context);
      this.message = message;
      this.retryAction = retryAction;
      this.cancelAction = cancelAction;
   }

   @Override
   protected MaterialDialog createDialog(T t, Throwable throwable, Context context) {
      return new MaterialDialog.Builder(context)
            .content(message)
            .cancelable(false)
            .positiveText(R.string.wallet_retry_label)
            .onPositive((dialog, which) -> retryAction.call(t))
            .negativeText(R.string.wallet_cancel_label)
            .onNegative((dialog, which) -> cancelAction.call(t))
            .build();
   }
}
