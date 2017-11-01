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

   @InjectView(R.id.tv_thank_you_pilot) TextView titleStatusLineTextView;
   @InjectView(R.id.tv_thank_you_pilot2) TextView titleStatusViewErrorView;
   @InjectView(R.id.tv_payment_status_pilot) TextView acceptStatusPaymentView;
   @InjectView(R.id.iv_status_payment_pilot) ImageView statusImage;
   @InjectView(R.id.tv_total_charged_text_pilot) TextView totalAmountChargedTextView;
   @InjectView(R.id.tv_payment_sub_thank_you_message_pilot) TextView serverTextView;

   public TransactionStatusInjector(Context context, View view) {
      this.context = context;
      ButterKnife.inject(this, view);
   }

   public void showSuccessMessage() {
      titleStatusLineTextView.setText(getTextFromResource(R.string.thank_you_thrst_pilot));
      acceptStatusPaymentView.setText(getTextFromResource(R.string.payment_success_status_pilot));
      statusImage.setImageDrawable(getDrawableFromResource(R.drawable.check_succes_pilot));
      totalAmountChargedTextView.setText(getTextFromResource(R.string.total_amount_charged_pilot));
      serverTextView.setText(getTextFromResource(R.string.payment_resume_success_pilot));
   }

   public void showFailureMessage() {
      titleStatusLineTextView.setText(getTextFromResource(R.string.first_failure_text_thrst_pilot));
      titleStatusViewErrorView.setVisibility(View.VISIBLE);
      acceptStatusPaymentView.setText(getTextFromResource(R.string.payment_error_status_pilot));
      statusImage.setImageDrawable(getDrawableFromResource(R.drawable.check_error_pilot));
      totalAmountChargedTextView.setText(getTextFromResource(R.string.payment_amount_due_pilot));
      serverTextView.setText(getTextFromResource(R.string.payment_resume_failure_pilot));
      serverTextView.setVisibility(View.VISIBLE);
   }

   private String getTextFromResource(int id) { return context.getString(id);}

   private Drawable getDrawableFromResource(int id) { return ContextCompat.getDrawable(context, id);}

}
