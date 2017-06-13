package com.worldventures.dreamtrips.wallet.ui.common.helper2.error;

import android.content.Context;
import android.support.annotation.Nullable;

import com.afollestad.materialdialogs.MaterialDialog;
import com.worldventures.dreamtrips.R;

import io.techery.janet.smartcard.exception.SmartCardRequestException;
import rx.functions.Action1;

public class SmartCardErrorViewProvider<T> implements ErrorViewProvider<T> {

   private final Context context;
   @Nullable
   private final Action1<T> retryAction;
   @Nullable
   private final Action1<T> cancelAction;

   public SmartCardErrorViewProvider(Context context) {
      this(context, null, null);
   }

   public SmartCardErrorViewProvider(Context context, @Nullable Action1<T> retryAction) {
      this(context, retryAction, null);
   }

   public SmartCardErrorViewProvider(Context context, @Nullable Action1<T> retryAction, @Nullable Action1<T> cancelAction) {
      this.context = context;
      this.retryAction = retryAction;
      this.cancelAction = cancelAction;
   }

   @Override
   public Class<? extends Throwable> forThrowable() {
      return SmartCardRequestException.class;
   }

   @Override
   public ErrorView<T> create(Object o, Throwable throwable) {
      return new ErrorView<>(context, ((SmartCardRequestException) throwable).getRequestName(), retryAction, cancelAction);
   }

   static class ErrorView<M> extends DialogErrorView<M> {

      private final String message;
      @Nullable
      private final Action1<M> retryAction;
      @Nullable
      private final Action1<M> cancelAction;

      public ErrorView(Context context, String requestName, @Nullable Action1<M> retryAction, @Nullable Action1<M> cancelAction) {
         super(context);
         this.retryAction = retryAction;
         this.cancelAction = cancelAction;
         this.message = context.getString(R.string.wallet_sc_operation_could_not_be_completed, requestName);
      }

      @Override
      protected MaterialDialog createDialog(M t, Throwable throwable, Context context) {
         MaterialDialog.Builder builder = new MaterialDialog.Builder(context)
               .title(R.string.wallet_label_we_are_sorry)
               .content(message)
               .cancelable(cancelAction == null);

         if (retryAction != null) {
            builder.positiveText(R.string.retry).onPositive((dialog, which) -> retryAction.call(t));
         }
         if (cancelAction != null) {
            builder.negativeText(R.string.cancel).onNegative((dialog, which) -> cancelAction.call(t));
         } else {
            builder.negativeText(R.string.ok);
         }

         return builder.build();
      }

   }
}
