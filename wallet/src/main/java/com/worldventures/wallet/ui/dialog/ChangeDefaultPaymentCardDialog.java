package com.worldventures.wallet.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;

import com.afollestad.materialdialogs.MaterialDialog;
import com.worldventures.wallet.R;

import rx.functions.Action0;

public class ChangeDefaultPaymentCardDialog {

   private Dialog dialog;
   private Action0 confirmAction;
   private Action0 cancelAction;

   public ChangeDefaultPaymentCardDialog(Context context, @NonNull String paymentCardName) {
      String contentText = context.getResources()
            .getString(R.string.wallet_add_card_details_default_card_exist_dialog_text, paymentCardName);
      dialog = new MaterialDialog.Builder(context)
            .content(contentText)
            .positiveText(R.string.wallet_ok)
            .negativeText(R.string.wallet_cancel_label)
            .onNegative((dialog, which) -> cancelAction.call())
            .onPositive((dialog, which) -> confirmAction.call())
            .cancelListener(dialog -> cancelAction.call())
            .build();
   }

   public ChangeDefaultPaymentCardDialog setOnConfirmAction(Action0 onConfirmAction) {
      this.confirmAction = onConfirmAction;
      return this;
   }

   public ChangeDefaultPaymentCardDialog setOnCancelAction(Action0 onCancelAction) {
      this.cancelAction = onCancelAction;
      return this;
   }

   public void show() {
      dialog.show();
   }

   public void dismiss() {
      dialog.dismiss();
   }

}
