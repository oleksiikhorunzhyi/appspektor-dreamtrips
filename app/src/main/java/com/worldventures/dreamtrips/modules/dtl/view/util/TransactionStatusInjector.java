package com.worldventures.dreamtrips.modules.dtl.view.util;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.worldventures.dreamtrips.R;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class TransactionStatusInjector {

   private Context context;

   @InjectView(R.id.tv_transaction_status_title) TextView titleStatusLineTextView;
   @InjectView(R.id.tv_transaction_status_error_secondary_title) TextView titleStatusSecondaryErrorView;
   @InjectView(R.id.tv_accept_payment_status) TextView acceptStatusPaymentView;
   @InjectView(R.id.iv_status_payment) ImageView statusImage;
   @InjectView(R.id.tv_total_charged_text) TextView totalAmountChargedTextView;
   @InjectView(R.id.tv_show_to_server_text) TextView showToServerTextView;

   public TransactionStatusInjector(Context context, View view) {
      this.context = context;
      ButterKnife.inject(this, view);
   }

   public void showSuccessMessage() {
      titleStatusLineTextView.setText(getTextFromResource(R.string.thank_you_thrst_pilot));
      acceptStatusPaymentView.setText(getTextFromResource(R.string.payment_success_status_pilot));
      statusImage.setImageDrawable(getDrawableFromResource(R.drawable.check_succes_pilot));
      totalAmountChargedTextView.setText(getTextFromResource(R.string.total_amount_charged_pilot));
      showToServerTextView.setText(getTextFromResource(R.string.payment_resume_success_pilot));
   }

   public void showFailureMessage() {
      titleStatusLineTextView.setText(getTextFromResource(R.string.first_failure_text_thrst_pilot));
      titleStatusSecondaryErrorView.setVisibility(View.VISIBLE);
      acceptStatusPaymentView.setText(getTextFromResource(R.string.payment_error_status_pilot));
      statusImage.setImageDrawable(getDrawableFromResource(R.drawable.check_error_pilot));
      totalAmountChargedTextView.setText(getTextFromResource(R.string.payment_amount_due_pilot));
      showToServerTextView.setText(getTextFromResource(R.string.payment_resume_failure_pilot));
      showToServerTextView.setVisibility(View.VISIBLE);
   }

   public void showRefundedMessage() {
      showSuccessMessage();
      statusImage.setImageDrawable(getDrawableFromResource(R.drawable.check_refund_pilot));
   }

   private String getTextFromResource(int id) { return context.getString(id); }

   private Drawable getDrawableFromResource(int id) { return ContextCompat.getDrawable(context, id); }

}
