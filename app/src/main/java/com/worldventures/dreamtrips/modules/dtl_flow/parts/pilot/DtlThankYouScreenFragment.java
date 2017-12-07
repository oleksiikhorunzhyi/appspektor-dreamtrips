package com.worldventures.dreamtrips.modules.dtl_flow.parts.pilot;

import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.worldventures.core.ui.annotations.Layout;
import com.worldventures.core.utils.DateTimeUtils;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.rx.RxBaseFragmentWithArgs;
import com.worldventures.dreamtrips.modules.dtl.bundle.ThrstPaymentBundle;
import com.worldventures.dreamtrips.modules.dtl.event.DtlThrstTransactionSucceedEvent;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.Merchant;
import com.worldventures.dreamtrips.modules.dtl.presenter.DtlThrstThankYouScreenPresenter;
import com.worldventures.dreamtrips.modules.dtl.util.DtlDateTimeUtils;
import com.worldventures.dreamtrips.modules.dtl.view.util.TransactionStatusInjector;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.utils.CurrencyUtils;
import com.worldventures.dreamtrips.social.ui.activity.SocialComponentActivity;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import butterknife.InjectView;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

@Layout(R.layout.include_detail_transaction)
public class DtlThankYouScreenFragment extends RxBaseFragmentWithArgs<DtlThrstThankYouScreenPresenter, ThrstPaymentBundle> implements DtlThrstThankYouScreenPresenter.View {
   @InjectView(R.id.payment_done_button) Button mButtonDone;
   @InjectView(R.id.tv_total) TextView mMoneyCharged;
   @InjectView(R.id.tv_earned_points) TextView tvEarnedPoints;
   @InjectView(R.id.tv_subtotal) TextView tvSubTotal;
   @InjectView(R.id.tv_tip) TextView tvTip;
   @InjectView(R.id.tv_tax) TextView tvTax;
   @InjectView(R.id.tv_receipt) TextView tvReceipt;
   @InjectView(R.id.tv_review_merchant) TextView tvReviewMerchant;
   @InjectView(R.id.currentTime) TextView tvDate;

   private TransactionStatusInjector transactionStatusInjector;
   private Merchant merchant;

   @Override
   public void afterCreateView(View rootView) {
      super.afterCreateView(rootView);
      ThrstPaymentBundle thrstPaymentBundle = getArgs();
      merchant = thrstPaymentBundle.getMerchant();
      ((SocialComponentActivity) getActivity()).getSupportActionBar().setTitle(merchant.displayName());
      transactionStatusInjector = new TransactionStatusInjector(getActivity(), rootView);
   }

   @Override
   public void onStart() {
      super.onStart();
      Observable.interval(1, TimeUnit.SECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .compose(bindUntilStopViewComposer())
            .subscribe(delay -> setCurrentTime());
   }

   @Override
   protected DtlThrstThankYouScreenPresenter createPresenter(Bundle savedInstanceState) {
      return new DtlThrstThankYouScreenPresenter(getArgs());
   }

   @OnClick(R.id.payment_done_button)
   public void onDoneClick() {
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
   public void hideBackIcon() {
      ActionBar actionBar = ((SocialComponentActivity) getActivity()).getSupportActionBar();
      if (actionBar == null) {
         return;
      }

      actionBar.setHomeButtonEnabled(false);
      actionBar.setDisplayHomeAsUpEnabled(false);
   }

   @Override
   public void showTransactionSuccessfulMessage() {
      transactionStatusInjector.showSuccessMessage();
   }

   @Override
   public void showTransactionFailedMessage() {
      transactionStatusInjector.showFailureMessage();
   }

   @Override
   public void setChargeMoney(double money, double subTotal, double taxAmount, double tipAmount,
         String currencyCode, String currencySymbol) {
      mMoneyCharged.setText(CurrencyUtils.toCurrency(money, currencyCode, currencySymbol));
      tvSubTotal.setText(CurrencyUtils.toCurrency(subTotal, currencyCode, currencySymbol));
      tvTax.setText(CurrencyUtils.toCurrency(taxAmount, currencyCode, currencySymbol));
      tvTip.setText(CurrencyUtils.toCurrency(tipAmount, currencyCode, currencySymbol));
   }

   @Override
   public void showDoneButton() {
      mButtonDone.setVisibility(View.VISIBLE);
   }

   @Override
   public void hideReviewMerchant() {
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

   private void setCurrentTime() {
      tvDate.setText(DateTimeUtils.convertDateToString(new Date(), DtlDateTimeUtils.THANK_YOU_SCREEN_FORMAT));
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
