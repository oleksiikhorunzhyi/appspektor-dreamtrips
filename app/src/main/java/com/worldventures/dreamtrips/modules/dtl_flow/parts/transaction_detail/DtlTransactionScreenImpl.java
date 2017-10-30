package com.worldventures.dreamtrips.modules.dtl_flow.parts.transaction_detail;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.worldventures.core.ui.util.ViewUtils;
import com.worldventures.core.utils.DateTimeUtils;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.dtl.presenter.DtlThrstFlowPresenter;
import com.worldventures.dreamtrips.modules.dtl_flow.DtlLayout;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.transactions.model.TransactionModel;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.utils.CurrencyUtils;

import java.util.Random;

import butterknife.InjectView;
import cn.pedant.SweetAlert.SweetAlertDialog;
import flow.Flow;

public class DtlTransactionScreenImpl extends DtlLayout<DtlTransactionScreen, DtlTransactionPresenter, DtlTransactionPath>
      implements DtlTransactionScreen {

   private final String SUCCESSFUL_STATUS = "SUCCESSFUL";

   private TransactionModel transaction;
   private MaterialDialog progressDialog;

   @InjectView(R.id.tv_thank_you_pilot) TextView mThankYouView;
   @InjectView(R.id.tv_thank_you_pilot2) TextView mThankYouView2;
   @InjectView(R.id.tv_payment_status_pilot) TextView mPaymentStatusView;
   @InjectView(R.id.iv_status_payment_pilot) ImageView mPaymentImage;
   @InjectView(R.id.tv_total_charged_text_pilot) TextView mPaymentCharged;
   @InjectView(R.id.tv_payment_sub_thank_you_message_pilot) TextView mSubThankYouMessage;

   @InjectView(R.id.toolbar_actionbar) Toolbar toolbar;
   @InjectView(R.id.tv_title) TextView tvTitle;

   @InjectView(R.id.tv_total) TextView tvTotal;
   @InjectView(R.id.tv_subtotal) TextView tvSubTotal;
   @InjectView(R.id.tv_tip) TextView tvTip;
   @InjectView(R.id.tv_tax) TextView tvTax;
   @InjectView(R.id.tv_earned_points) TextView tvEarnedPoints;
   @InjectView(R.id.currentTime) TextView tvDate;
   @InjectView(R.id.tv_review_merchant) TextView tvReview;
   @InjectView(R.id.tv_receipt) TextView tvReceipt;

   public DtlTransactionScreenImpl(Context context) {
      super(context);
   }

   public DtlTransactionScreenImpl(Context context, AttributeSet attrs) {
      super(context, attrs);
   }

   @Override
   public DtlTransactionPresenter createPresenter() {
      return new DtlTransactionPresenterImpl(getContext(), injector, getPath().getTransaction());
   }

   @Override
   protected void onPostAttachToWindowView() {
      inflateToolbarMenu(toolbar);
      transaction = getPath().getTransaction();
      if (transaction == null) return;

      if (ViewUtils.isTabletLandscape(getContext())) {
         toolbar.setBackgroundColor(Color.WHITE);
         tvTitle.setVisibility(View.VISIBLE);
         tvTitle.setText(transaction.getMerchantName());
      } else {
         toolbar.setTitle(transaction.getMerchantName());
         toolbar.setNavigationIcon(ViewUtils.isTabletLandscape(getContext()) ? R.drawable.back_icon_black : R.drawable.back_icon);
         toolbar.setNavigationOnClickListener(view -> {
            Flow.get(getContext()).goBack();
         });
      }
      initData();
   }

   private void initData() {
      tvTotal.setText(CurrencyUtils.toCurrency(transaction.getTotalAmount()));
      tvSubTotal.setText(CurrencyUtils.toCurrency(transaction.getSubTotalAmount()));
      tvTax.setText(CurrencyUtils.toCurrency(transaction.getTax()));
      tvTip.setText(CurrencyUtils.toCurrency(transaction.getTip()));
      tvDate.setText(DateTimeUtils.convertDateToString(transaction.getTransactionDate(), DateTimeUtils.TRANSACTION_DATE_FORMAT));
      tvEarnedPoints.setText(String.format(getContext().getString(R.string.dtl_earned_points), transaction.getEarnedPoints()));
      setupSpannableAction(tvReceipt, new OpenURLSpannable());
      setupSpannableAction(tvReview, new ReviewMerchantSpannable());

      if(!SUCCESSFUL_STATUS.equals(transaction.getPaymentStatus())){
         mThankYouView.setText(getContext().getString(R.string.first_failure_text_thrst_pilot));
         mThankYouView2.setVisibility(View.VISIBLE);
         mPaymentStatusView.setText(getContext().getString(R.string.payment_error_status_pilot));
         mPaymentImage.setImageDrawable(getDrawableFromResource(R.drawable.check_error_pilot));
         mPaymentCharged.setText(getContext().getString(R.string.payment_amount_due_pilot));
         mSubThankYouMessage.setText(getContext().getString(R.string.payment_resume_failure_pilot));
         mSubThankYouMessage.setVisibility(View.VISIBLE);
      }

   }

   private Drawable getDrawableFromResource(int id) { return ContextCompat.getDrawable(getContext(), id);}

   @Override
   public void showReceipt(String url) {
      Intent intent = new Intent(Intent.ACTION_VIEW);
      intent.setDataAndType(Uri.parse(url), "text/html");
      getContext().startActivity(intent);
   }

   private void setupSpannableAction(TextView textView, ClickableSpan clickableSpan) {
      SpannableString spannableString = new SpannableString(textView.getText());
      spannableString.setSpan(clickableSpan, 0, textView.getText().length(), 0);
      textView.setText(spannableString);
      textView.setMovementMethod(LinkMovementMethod.getInstance());
   }

   private class OpenURLSpannable extends ClickableSpan {
      @Override
      public void onClick(View view) {
         getPresenter().showReceipt();
      }

      @Override
      public void updateDrawState(TextPaint ds) {
         ds.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
         ds.setUnderlineText(false);
      }

   }

   private class ReviewMerchantSpannable extends ClickableSpan {
      @Override
      public void onClick(View view) {
         getPresenter().reviewMerchant();
      }

      @Override
      public void updateDrawState(TextPaint ds) {
         ds.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
         ds.setUnderlineText(false);
      }

   }

   @Override
   public void showLoadingMerchantDialog() {
      progressDialog = new MaterialDialog.Builder(getActivity()).progress(true, 0)
            .content(R.string.loading)
            .cancelable(false)
            .canceledOnTouchOutside(false)
            .show();
   }

   @Override
   public void hideLoadingMerchantDialog() {
      if (progressDialog != null && progressDialog.isShowing()) progressDialog.dismiss();
   }

   @Override
   public void showCouldNotShowMerchantDialog() {
      new SweetAlertDialog(getActivity(), SweetAlertDialog.WARNING_TYPE).setTitleText(getContext().getString(R.string.app_name))
            .setContentText(getContext().getString(R.string.could_not_load_merchant_for_review))
            .setConfirmText(getContext().getString(R.string.OK))
            .setConfirmClickListener(sweetAlertDialog -> {
               sweetAlertDialog.dismissWithAnimation();
            })
            .setCancelClickListener(SweetAlertDialog::dismissWithAnimation);
   }
}