package com.worldventures.wallet.ui.common.helper2.error;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;

import com.afollestad.materialdialogs.MaterialDialog;
import com.worldventures.wallet.R;

import rx.functions.Action1;

public class RetryDialogErrorView<T> extends DialogErrorView<T> {

   private final String message;
   private final Action1<T> retryAction;
   @Nullable
   private final Action1<T> cancelAction;

   public RetryDialogErrorView(Context context, @StringRes int message, @NonNull Action1<T> retryAction) {
      this(context, message, retryAction, null);
   }

   public RetryDialogErrorView(Context context, @StringRes int message, @NonNull Action1<T> retryAction, @Nullable Action1<T> cancelAction) {
      super(context);
      this.message = context.getString(message);
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
            .onNegative((dialog, which) -> {
               if (cancelAction != null) cancelAction.call(t);
            })
            .build();
   }
}
