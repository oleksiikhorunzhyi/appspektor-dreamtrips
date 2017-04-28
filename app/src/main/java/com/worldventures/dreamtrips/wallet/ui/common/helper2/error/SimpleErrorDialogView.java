package com.worldventures.dreamtrips.wallet.ui.common.helper2.error;

import android.content.Context;

import com.afollestad.materialdialogs.MaterialDialog;
import com.worldventures.dreamtrips.R;

public class SimpleErrorDialogView<T> extends DialogErrorView<T> {

   private int contentResId;
   private MaterialDialog.SingleButtonCallback defaultAction;

   public SimpleErrorDialogView(Context context, int contentResId) {
      super(context);
      this.contentResId = contentResId;
   }

   public SimpleErrorDialogView(Context context, int messageResId, MaterialDialog.SingleButtonCallback defaultAction) {
      this(context, messageResId);
      this.defaultAction = defaultAction;
   }

   @Override
   protected MaterialDialog createDialog(T t, Throwable throwable, Context context) {
      return new MaterialDialog.Builder(context)
            .content(contentResId)
            .positiveText(R.string.ok)
            .onPositive(defaultAction)
            .build();
   }
}
