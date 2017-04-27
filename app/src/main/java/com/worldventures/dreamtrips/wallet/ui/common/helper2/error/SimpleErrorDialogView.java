package com.worldventures.dreamtrips.wallet.ui.common.helper2.error;

import android.content.Context;

import com.afollestad.materialdialogs.MaterialDialog;
import com.worldventures.dreamtrips.R;

import rx.functions.Action1;

public class SimpleErrorDialogView<T> extends DialogErrorView<T> {

   private int contentResId;
   private Action1<T> defaultAction;

   public SimpleErrorDialogView(Context context, int contentResId) {
      this(context, contentResId, c -> {});
   }

   public SimpleErrorDialogView(Context context, int messageResId, Action1<T> defaultAction) {
      super(context);
      this.defaultAction = defaultAction;
      this.contentResId = messageResId;
   }

   @Override
   protected MaterialDialog createDialog(T t, Throwable throwable, Context context) {
      return new MaterialDialog.Builder(context)
            .content(contentResId)
            .positiveText(R.string.ok)
            .onPositive((dialog, which) -> defaultAction.call(t))
            .build();
   }
}
