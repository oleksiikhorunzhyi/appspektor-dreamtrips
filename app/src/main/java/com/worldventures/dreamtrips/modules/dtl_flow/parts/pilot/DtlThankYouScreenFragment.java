package com.worldventures.dreamtrips.modules.dtl_flow.parts.pilot;

import android.content.Context;
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

import com.afollestad.materialdialogs.MaterialDialog;
import com.worldventures.core.ui.annotations.Layout;
import com.worldventures.core.utils.DateTimeUtils;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.rx.RxBaseFragmentWithArgs;
import com.worldventures.dreamtrips.modules.dtl.bundle.MerchantBundle;
import com.worldventures.dreamtrips.modules.dtl.presenter.DtlThrstThankYouScreenPresenter;
import com.worldventures.dreamtrips.modules.dtl.util.DtlDateTimeUtils;
import com.worldventures.dreamtrips.modules.dtl.view.util.TransactionStatusInjector;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.utils.CurrencyUtils;
import com.worldventures.dreamtrips.social.ui.activity.SocialComponentActivity;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import butterknife.InjectView;
import butterknife.OnClick;
import cn.pedant.SweetAlert.SweetAlertDialog;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

@Layout(R.layout.include_detail_transaction)
public class DtlThankYouScreenFragment
      extends RxBaseFragmentWithArgs<DtlThrstThankYouScreenPresenter, MerchantBundle>
      implements DtlThrstThankYouScreenPresenter.View {
   @InjectView(R.id.payment_done_button) Button mButtonDone;
   @InjectView(R.id.tv_total) TextView mMoneyCharged;
   @InjectView(R.id.tv_earned_points) TextView tvEarnedPoints;
   @InjectView(R.id.tv_subtotal) TextView tvSubTotal;
   @InjectView(R.id.tv_tip) TextView tvTip;
   @InjectView(R.id.tv_tax) TextView tvTax;
   @InjectView(R.id.transaction_buttons_container) View transactionButtonsContainer;
   @InjectView(R.id.tv_receipt) TextView tvReceipt;
   @InjectView(R.id.tv_review_merchant) TextView tvReviewMerchant;
   @InjectView(R.id.currentTime) TextView tvDate;

   private MaterialDialog progressDialog;

   private TransactionStatusInjector transactionStatusInjector;

   @Override
   public void afterCreateView(View rootView) {
      super.afterCreateView(rootView);
      ((SocialComponentActivity) getActivity()).getSupportActionBar().setTitle(getArgs().getMerchant().displayName());
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
      return new DtlThrstThankYouScreenPresenter(getArgs().getMerchant());
   }

   @OnClick(R.id.tv_send)
   public void onEmailClick() {
      getPresenter().onSendEmailClick(getView().findViewById(R.id.receipt_main_views_container));
   }

   @OnClick(R.id.payment_done_button)
   public void onDoneClick() {
      getPresenter().onDoneClick();
   }

   @Override
   public void showTransactionButtons() {
      transactionButtonsContainer.setVisibility(View.VISIBLE);
   }

   @Override
   public void hideTransactionButtons() {
      transactionButtonsContainer.setVisibility(View.INVISIBLE);
   }

   @Override
   public void goBack() {
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
   public void setChargeMoney(double money, double subTotal, double taxAmount, double tipAmount) {
      mMoneyCharged.setText(CurrencyUtils.toCurrency(money));
      tvSubTotal.setText(CurrencyUtils.toCurrency(subTotal));
      tvTax.setText(CurrencyUtils.toCurrency(taxAmount));
      tvTip.setText(CurrencyUtils.toCurrency(tipAmount));
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

   @Override
   public void showSuccessEmailMessage() {
      Context context = getContext();
      SweetAlertDialog alertDialog = new SweetAlertDialog(context, SweetAlertDialog.SUCCESS_TYPE)
            .setTitleText(context.getString(R.string.app_name))
            .setContentText(context.getString(R.string.dtl_send_email_success))
            .setConfirmText(context.getString(R.string.ok))
            .setConfirmClickListener(sweetAlertDialog -> {
               sweetAlertDialog.dismissWithAnimation();
            });
      alertDialog.setCancelable(false);
      alertDialog.show();
   }

   @Override
   public void showErrorEmailMessage() {
      Context context = getContext();
      SweetAlertDialog alertDialog = new SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE)
            .setTitleText(context.getString(R.string.dtl_alert_title_error))
            .setContentText(context.getString(R.string.dtl_send_email_error))
            .setConfirmText(context.getString(R.string.ok))
            .setConfirmClickListener(sweetAlertDialog -> {
               sweetAlertDialog.dismissWithAnimation();
            });
      alertDialog.setCancelable(false);
      alertDialog.show();
   }

   @Override
   public void showLoadingDialog() {
      progressDialog = new MaterialDialog.Builder(getContext()).progress(true, 0)
            .content(R.string.loading)
            .cancelable(false)
            .canceledOnTouchOutside(false)
            .show();
   }

   @Override
   public void hideLoadingDialog() {
      if (progressDialog != null && progressDialog.isShowing()) {
         progressDialog.dismiss();
      }
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
