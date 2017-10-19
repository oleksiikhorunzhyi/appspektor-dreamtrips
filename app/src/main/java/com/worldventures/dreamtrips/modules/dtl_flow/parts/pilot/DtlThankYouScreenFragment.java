package com.worldventures.dreamtrips.modules.dtl_flow.parts.pilot;

import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.worldventures.core.ui.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.rx.RxBaseFragmentWithArgs;
import com.worldventures.dreamtrips.modules.dtl.bundle.ThrstPaymentBundle;
import com.worldventures.dreamtrips.modules.dtl.event.DtlThrstTransactionSucceedEvent;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.Merchant;
import com.worldventures.dreamtrips.modules.dtl.presenter.DtlThrstThankYouScreenPresenter;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.utils.CurrencyUtils;
import com.worldventures.dreamtrips.social.ui.activity.SocialComponentActivity;

import butterknife.InjectView;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;

@Layout(R.layout.include_detail_transaction)
public class DtlThankYouScreenFragment extends RxBaseFragmentWithArgs<DtlThrstThankYouScreenPresenter, ThrstPaymentBundle> implements DtlThrstThankYouScreenPresenter.View {
   @InjectView(R.id.tv_thank_you_pilot) TextView mThankYouView;
   @InjectView(R.id.tv_thank_you_pilot2) TextView mThankYouView2;
   @InjectView(R.id.tv_payment_status_pilot) TextView mPaymentStatusView;
   @InjectView(R.id.iv_status_payment_pilot) ImageView mPaymentImage;
   @InjectView(R.id.tv_total_charged_text_pilot) TextView mPaymentCharged;
   @InjectView(R.id.tv_total) TextView mMoneyCharged;
   @InjectView(R.id.tv_payment_sub_thank_you_message_pilot) TextView mSubThankYouMessage;
   @InjectView(R.id.payment_done_button) Button mButtonDone;
   @InjectView(R.id.tv_earned_points) TextView tvEarnedPoints;
   @InjectView(R.id.tv_subtotal) TextView tvSubTotal;
   @InjectView(R.id.tv_tip) TextView tvTip;
   @InjectView(R.id.tv_tax) TextView tvTax;
   @InjectView(R.id.tv_receipt) TextView tvReceipt;
   @InjectView(R.id.tv_review_merchant) TextView tvReviewMerchant;

   private Merchant merchant;

   @Override
   public void onActivityCreated(Bundle savedInstanceState) {
      super.onActivityCreated(savedInstanceState);
      ThrstPaymentBundle thrstPaymentBundle = getArgs();
      merchant = thrstPaymentBundle.getMerchant();
      ((SocialComponentActivity) getActivity()).getSupportActionBar().setTitle(merchant.displayName());
   }

   @Override
   public void afterCreateView(View rootView) {
      super.afterCreateView(rootView);
   }

   @Override
   protected DtlThrstThankYouScreenPresenter createPresenter(Bundle savedInstanceState) {
      return new DtlThrstThankYouScreenPresenter(getArgs());
   }

   @OnClick(R.id.payment_done_button)
   public void onDoneClick(){
      getPresenter().onDoneClick();
   }

   @Override
   public void goBack(boolean isPaid, String earnedPoints, String totalPoints) {
      if (isPaid) {
         EventBus.getDefault().postSticky(new DtlThrstTransactionSucceedEvent(earnedPoints, totalPoints));
      }
      getActivity().onBackPressed();
   }

   @Override
   public void thankYouSuccessfulText() {
      mThankYouView.setText(getTextFromResource(R.string.thank_you_thrst_pilot));
   }

   @Override
   public void thankYouFailureText() {
      mThankYouView.setText(getTextFromResource(R.string.first_failure_text_thrst_pilot));
      mThankYouView2.setVisibility(View.VISIBLE);
   }

   @Override
   public void setSuccessPaymentText() {
      mPaymentStatusView.setText(getTextFromResource(R.string.payment_success_status_pilot));
   }

   @Override
   public void setFailurePaymentText() {
      mPaymentStatusView.setText(getTextFromResource(R.string.payment_error_status_pilot));
   }

   @Override
   public void setSuccessResume() {
      mPaymentCharged.setText(getTextFromResource(R.string.total_amount_charged_pilot));
   }

   @Override
   public void setFailureResume() {
      mPaymentCharged.setText(getTextFromResource(R.string.payment_amount_due_pilot));
   }

   @Override
   public void setChargeMoney(double money, double subTotal, double taxAmount, double tipAmount){
      mMoneyCharged.setText(CurrencyUtils.toCurrency(money));
      tvSubTotal.setText(CurrencyUtils.toCurrency(subTotal));
      tvTax.setText(CurrencyUtils.toCurrency(taxAmount));
      tvTip.setText(CurrencyUtils.toCurrency(tipAmount));
   }

   @Override
   public void setPaymentSuccessImage() {
      mPaymentImage.setImageDrawable(getDrawableFromResource(R.drawable.check_succes_pilot));
   }

   @Override
   public void setPaymentFailureImage() {
      mPaymentImage.setImageDrawable(getDrawableFromResource(R.drawable.check_error_pilot));
   }

   @Override
   public void setShowScreenSuccessMessage() {
      mSubThankYouMessage.setText(getTextFromResource(R.string.payment_resume_success_pilot));
   }

   @Override
   public void setShowScreenFailureMessage() {
      mSubThankYouMessage.setText(getTextFromResource(R.string.payment_resume_failure_pilot));
   }

   @Override
   public void showSubThankYouMessage() {
      mSubThankYouMessage.setVisibility(View.VISIBLE);
   }

   @Override
   public void showDoneButton() {
      mButtonDone.setVisibility(View.VISIBLE);
   }

   @Override
   public void hideViewsOnError() {
      tvReviewMerchant.setVisibility(View.GONE);
   }

   @Override
   public void setEarnedPoints(int earnedPoints) {
      tvEarnedPoints.setText(String.format(getContext().getString(R.string.dtl_earned_points), earnedPoints));
   }

   @Override
   public void setReceiptURL(String url) {
      setupSpannableAction(tvReceipt, new OpenURLSpannable(), url);
   }

   private String getTextFromResource(int id) { return getContext().getString(id);}

   private Drawable getDrawableFromResource(int id) { return ContextCompat.getDrawable(getContext(), id);}

   public void showReceipt(String url) {
      Intent intent = new Intent(Intent.ACTION_VIEW);
      intent.setDataAndType(Uri.parse(url), "text/html");
      getContext().startActivity(intent);
   }

   private void setupSpannableAction(TextView textView, ClickableSpan clickableSpan, String url) {
      SpannableString spannableString = new SpannableString(textView.getText());
      spannableString.setSpan(clickableSpan, 0, textView.getText().length(), 0);
      textView.setText(spannableString);
      textView.setMovementMethod(LinkMovementMethod.getInstance());
      textView.setTag(url);
   }

   private class OpenURLSpannable extends ClickableSpan {
      @Override
      public void onClick(View view) {
         showReceipt(view.getTag().toString());
      }

      @Override
      public void updateDrawState(TextPaint ds) {
         ds.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
         ds.setUnderlineText(false);
      }

   }

}
