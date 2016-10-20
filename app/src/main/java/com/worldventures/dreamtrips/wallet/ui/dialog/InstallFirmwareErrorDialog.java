package com.worldventures.dreamtrips.wallet.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.text.util.Linkify;

import com.afollestad.materialdialogs.MaterialDialog;
import com.worldventures.dreamtrips.R;

import rx.functions.Action0;

public class InstallFirmwareErrorDialog {

   private Dialog dialog;
   private Action0 retryAction;
   private Action0 cancelAction;

   public InstallFirmwareErrorDialog(Context context) {
      dialog = new MaterialDialog.Builder(context)
            .title(R.string.wallet_firmware_install_error_alert_title)
            .content(createDialogContentText(context))
            .positiveText(R.string.wallet_firmware_install_error_retry_action)
            .onPositive((dialog, which) -> retryAction.call())
            .negativeText(R.string.wallet_firmware_install_error_cancel_action)
            .onNegative((dialog, which) -> cancelAction.call())
            .cancelable(false)
            .build();
   }

   public InstallFirmwareErrorDialog setOnRetryction(Action0 onConfirmAction) {
      this.retryAction = onConfirmAction;
      return this;
   }

   public InstallFirmwareErrorDialog setOnCancelAction(Action0 onCancelAction) {
      this.cancelAction = onCancelAction;
      return this;
   }

   public void show() {
      dialog.show();
   }

   public void dismiss() {
      dialog.dismiss();
   }

   public boolean isShowing() {
      return dialog.isShowing();
   }

   private static CharSequence createDialogContentText(Context context) {
      SpannableString supportPhoneNumber = new SpannableString(context.getString(R.string.wallet_firmware_install_customer_support_phone_number));
      supportPhoneNumber.setSpan(new StyleSpan(Typeface.BOLD), 0, supportPhoneNumber.length(), 0);
      supportPhoneNumber.setSpan(new ForegroundColorSpan(ContextCompat.getColor(context, R.color.wallet_alert_phone_number_color)), 0, supportPhoneNumber
            .length(), 0);
      Linkify.addLinks(supportPhoneNumber, Linkify.PHONE_NUMBERS);

      return new SpannableStringBuilder()
            .append(context.getString(R.string.wallet_firmware_install_error_alert_content))
            .append("\n\n")
            .append(context.getString(R.string.wallet_firmware_install_error_alert_content_customer_support))
            .append("\n")
            .append(supportPhoneNumber);
   }
}
