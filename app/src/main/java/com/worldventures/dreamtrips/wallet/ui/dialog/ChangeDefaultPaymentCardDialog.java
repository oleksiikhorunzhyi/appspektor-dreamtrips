package com.worldventures.dreamtrips.wallet.ui.dialog;

import android.content.Context;
import android.support.annotation.NonNull;

import com.worldventures.dreamtrips.R;

import cn.pedant.SweetAlert.SweetAlertDialog;
import rx.functions.Action0;

public class ChangeDefaultPaymentCardDialog {

   private SweetAlertDialog dialog;
   private Action0 confirmAction;
   private Action0 cancelAction;

   public ChangeDefaultPaymentCardDialog(Context context, @NonNull String paymentCardName) {
      String contentText = context.getResources()
            .getString(R.string.wallet_add_card_details_default_card_exist_dialog_text, paymentCardName);
      dialog = new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE);
      dialog.setTitleText("")
            .setContentText(contentText)
            .setConfirmClickListener(dialog -> setEventActionAndDismiss(dialog, confirmAction))
            .setCancelClickListener(dialog -> setEventActionAndDismiss(dialog, cancelAction))
            .setCancelable(false);
   }

   private void setEventActionAndDismiss(SweetAlertDialog dialog, Action0 eventAction) {
      dialog.dismissWithAnimation();
      if (eventAction != null) eventAction.call();
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
      dialog.showCancelButton(true);
   }

   public void dismiss() {
      dialog.dismissWithAnimation();
   }

}
