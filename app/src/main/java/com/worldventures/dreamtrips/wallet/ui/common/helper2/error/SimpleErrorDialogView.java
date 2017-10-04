package com.worldventures.dreamtrips.wallet.ui.common.helper2.error;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.afollestad.materialdialogs.MaterialDialog;
import com.worldventures.dreamtrips.R;

import rx.functions.Action0;
import rx.functions.Action1;

public class SimpleErrorDialogView<T> extends DialogErrorView<T> {

   private int contentResId;
   private int positiveResId;
   private int negativeResId;
   private Action1<T> positiveAction;
   @Nullable private Action1<T> negativeAction;
   @Nullable private Action0 dismissAction;

   public SimpleErrorDialogView(Context context, int contentResId) {
      this(context, contentResId, c -> {/*nothing*/});
   }

   public SimpleErrorDialogView(Context context, int messageResId, @NonNull Action1<T> positiveAction) {
      this(context, messageResId, positiveAction, null);
   }

   public SimpleErrorDialogView(Context context, int messageResId, @NonNull Action1<T> positiveAction, @Nullable Action1<T> negativeAction) {
      this(context, messageResId, positiveAction, negativeAction, null);
   }

   public SimpleErrorDialogView(Context context, int messageResId, @NonNull Action1<T> positiveAction, @Nullable Action1<T> negativeAction, @Nullable Action0 dismissAction) {
      super(context);
      this.positiveResId = R.string.ok;
      this.negativeResId = R.string.cancel;
      this.positiveAction = positiveAction;
      this.negativeAction = negativeAction;
      this.contentResId = messageResId;
      this.dismissAction = dismissAction;
   }

   @Override
   protected MaterialDialog createDialog(T t, Throwable throwable, Context context) {
      final MaterialDialog.Builder builder = new MaterialDialog.Builder(context)
            .content(contentResId)
            .positiveText(positiveResId)
            .onPositive((dialog, which) -> {
               if (positiveAction != null) positiveAction.call(t);
            });
      if (negativeAction != null) {
         builder.negativeText(negativeResId).onNegative((dialog, which) -> negativeAction.call(t));
      }
      if (dismissAction != null) {
         builder.cancelListener(dialog -> dismissAction.call());
      }
      return builder.build();
   }

   public void setPositiveText(int positiveResId) {
      this.positiveResId = positiveResId;
   }

   public void setNegativeText(int negativeResId) {
      this.negativeResId = negativeResId;
   }
}
