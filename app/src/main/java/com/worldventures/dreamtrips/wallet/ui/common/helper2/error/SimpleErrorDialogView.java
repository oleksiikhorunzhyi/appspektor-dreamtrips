package com.worldventures.dreamtrips.wallet.ui.common.helper2.error;

import android.content.Context;

import com.afollestad.materialdialogs.MaterialDialog;
import com.worldventures.dreamtrips.R;

import rx.functions.Action1;

public class SimpleErrorDialogView<T> extends DialogErrorView<T> {

   private int contentResId;
   private int positiveResId;
   private Action1<T> defaultAction;
   private Action1<T> negativeAction;

   public SimpleErrorDialogView(Context context, int contentResId) {
      this(context, contentResId, c -> {});
   }

   public SimpleErrorDialogView(Context context, int messageResId, Action1<T> defaultAction) {
      this(context, messageResId, defaultAction, null);
   }

   public SimpleErrorDialogView(Context context, int messageResId, Action1<T> defaultAction, Action1<T> negativeAction) {
      super(context);
      this.positiveResId = R.string.ok;
      this.defaultAction = defaultAction;
      this.negativeAction = negativeAction;
      this.contentResId = messageResId;
   }

   @Override
   protected MaterialDialog createDialog(T t, Throwable throwable, Context context) {
      return new MaterialDialog.Builder(context)
            .content(contentResId)
            .positiveText(positiveResId)
            .onPositive((dialog, which) -> {
               if (defaultAction != null) defaultAction.call(t);
            })
            .onNegative((dialog, which) -> {
               if (negativeAction != null) negativeAction.call(t);
            })
            .build();
   }

   public void setPositiveText(int positiveResIdLabel) {
      this.positiveResId = positiveResIdLabel;
   }
}
