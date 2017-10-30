package com.worldventures.dreamtrips.modules.dtl_flow.parts.transaction_detail;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.worldventures.core.ui.util.ViewUtils;
import com.worldventures.core.utils.DateTimeUtils;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.dtl_flow.DtlLayout;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.transactions.model.TransactionModel;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.utils.CurrencyUtils;

import butterknife.InjectView;
import flow.Flow;

public class DtlTransactionScreenImpl extends DtlLayout<DtlTransactionScreen, DtlTransactionPresenter, DtlTransactionPath>
      implements DtlTransactionScreen {

   private TransactionModel transaction;

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
   }

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

}