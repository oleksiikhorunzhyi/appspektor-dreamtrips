package com.worldventures.wallet.ui.common.helper2.error;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;

import com.afollestad.materialdialogs.MaterialDialog;
import com.worldventures.wallet.R;

import rx.functions.Action1;

public class SimpleErrorView<T> extends DialogErrorView<T> {

   @Nullable private final Action1<T> posAction;
   @NonNull private final Action1<T> negAction;
   @StringRes private final int posText;
   @StringRes private final int negText;
   private final String message;

   public SimpleErrorView(Context context, String message, @NonNull Action1<T> action) {
      this(context, message, action, R.string.wallet_ok);
   }

   public SimpleErrorView(Context context, String message, @NonNull Action1<T> negAction, @StringRes int negText) {
      this(context, message, negAction, negText, null, 0);
   }

   public SimpleErrorView(Context context, String message,
         @NonNull Action1<T> negAction, @StringRes int negText,
         @Nullable Action1<T> posAction, @StringRes int posText) {
      super(context);
      this.message = message;
      this.posAction = posAction;
      this.posText = posText;
      this.negAction = negAction;
      this.negText = negText;
   }

   @Override
   protected MaterialDialog createDialog(T t, Throwable throwable, Context context) {
      MaterialDialog.Builder builder = new MaterialDialog.Builder(context)
            .content(message)
            .cancelable(false)
            .negativeText(negText)
            .onNegative((dialog1, which) -> negAction.call(t));

      if (posAction != null) {
         builder.positiveText(posText)
               .onPositive((dialog, which) -> posAction.call(t));
      }
      return builder.build();
   }
}
