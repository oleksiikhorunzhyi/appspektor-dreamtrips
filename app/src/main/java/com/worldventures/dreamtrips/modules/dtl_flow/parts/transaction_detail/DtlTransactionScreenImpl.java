package com.worldventures.dreamtrips.modules.dtl_flow.parts.transaction_detail;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.worldventures.core.ui.util.ViewUtils;
import com.worldventures.core.utils.DateTimeUtils;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.dtl.view.util.TransactionStatusInjector;
import com.worldventures.dreamtrips.modules.dtl_flow.DtlLayout;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.transactions.model.TransactionModel;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.utils.CurrencyUtils;

import butterknife.InjectView;
import butterknife.OnClick;
import cn.pedant.SweetAlert.SweetAlertDialog;
import flow.Flow;

public class DtlTransactionScreenImpl extends DtlLayout<DtlTransactionScreen, DtlTransactionPresenter, DtlTransactionPath>
      implements DtlTransactionScreen {

   private TransactionModel transaction;
   private MaterialDialog progressDialog;

   @InjectView(R.id.toolbar_actionbar) Toolbar toolbar;
   @InjectView(R.id.tv_title) TextView tvTitle;

   @InjectView(R.id.thrst_status_labels_container) ViewGroup thrstStatusLabelsContainer;
   @InjectView(R.id.thrst_billed_amount_container) ViewGroup thrstBilledAmountLabelsContainer;
   @InjectView(R.id.non_thrst_billed_amount_container) ViewGroup nonThrstBilledAmountLabelsContainer;

   // non thrst views
   @InjectView(R.id.tv_non_thrst_subtotal) TextView tvNonThrstSubTotal;
   @InjectView(R.id.tv_non_thrst_earned_points) TextView tvNonThrstEarnedPoints;
   @InjectView(R.id.tv_non_thrst_currentTime) TextView tvNonThrstTransactionDate;
   // thrst views
   @InjectView(R.id.tv_total) TextView tvTotal;
   @InjectView(R.id.tv_subtotal) TextView tvSubTotal;
   @InjectView(R.id.tv_tip) TextView tvTip;
   @InjectView(R.id.tv_tax) TextView tvTax;
   @InjectView(R.id.tv_earned_points) TextView tvEarnedPoints;
   @InjectView(R.id.currentTime) TextView tvDate;

   @InjectView(R.id.tv_review_merchant) TextView tvReview;
   @InjectView(R.id.tv_receipt) TextView tvReceipt;

   private TransactionStatusInjector transactionStatusInjector;

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
      if (transaction == null) {
         return;
      }

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

      transactionStatusInjector = new TransactionStatusInjector(getActivity(), this);
   }

   @Override
   public void showNonThrstTransaction(TransactionModel transactionModel) {
      thrstStatusLabelsContainer.setVisibility(GONE);
      thrstBilledAmountLabelsContainer.setVisibility(GONE);
      nonThrstBilledAmountLabelsContainer.setVisibility(VISIBLE);

      tvNonThrstSubTotal.setText(CurrencyUtils.toCurrency(transaction.getSubTotalAmount(), transactionModel.getCurrenyCode(),
            transactionModel.getCurrencySymbol()));
      tvNonThrstEarnedPoints.setText(getContext().getString(R.string.dtl_earned_points, transaction.getEarnedPoints()));
      tvNonThrstTransactionDate.setText(DateTimeUtils.convertDateToString(transaction.getTransactionDate(), DateTimeUtils.TRANSACTION_DATE_FORMAT_FULL));

      hideReviewsOnTablets();
   }

   @Override
   public void showThrstTransaction(TransactionModel transactionModel) {
      thrstStatusLabelsContainer.setVisibility(VISIBLE);
      thrstBilledAmountLabelsContainer.setVisibility(VISIBLE);
      nonThrstBilledAmountLabelsContainer.setVisibility(GONE);

      switch (transactionModel.getThrstPaymentStatus()) {
         case INITIATED:
         case SUCCESSFUL:
            transactionStatusInjector.showSuccessMessage();
            break;
         case REFUNDED:
            transactionStatusInjector.showRefundedMessage();
            break;
         default:
            transactionStatusInjector.showFailureMessage();
            break;
      }

      tvTotal.setText(CurrencyUtils.toCurrency(transaction.getTotalAmount(), transactionModel.getCurrenyCode(),
            transactionModel.getCurrencySymbol()));
      tvSubTotal.setText(CurrencyUtils.toCurrency(transaction.getSubTotalAmount(), transactionModel.getCurrenyCode(),
            transactionModel.getCurrencySymbol()));
      tvTax.setText(CurrencyUtils.toCurrency(transaction.getTax(), transactionModel.getCurrenyCode(),
            transactionModel.getCurrencySymbol()));
      tvTip.setText(CurrencyUtils.toCurrency(transaction.getTip(), transactionModel.getCurrenyCode(),
            transactionModel.getCurrencySymbol()));
      tvDate.setText(DateTimeUtils.convertDateToString(transaction.getTransactionDate(),
            DateTimeUtils.TRANSACTION_DATE_FORMAT));

      final int pointsCaptionFormatResId;

      if (transactionModel.getThrstPaymentStatus() == TransactionModel.ThrstPaymentStatus.REFUNDED) {
         pointsCaptionFormatResId = R.string.dtl_transaction_adjusted_points;

         final int colorResId = transactionModel.getTotalAmount() < 0D ?
               R.color.transaction_amount_red_color : R.color.transaction_amount_green_color;
         final int highlightColor = ContextCompat.getColor(getContext(), colorResId);
         tvTotal.setTextColor(highlightColor);
         tvSubTotal.setTextColor(highlightColor);
         tvTax.setTextColor(highlightColor);
         tvTip.setTextColor(highlightColor);
      } else {
         pointsCaptionFormatResId = R.string.dtl_earned_points;
      }

      tvEarnedPoints.setText(getContext().getString(pointsCaptionFormatResId, transaction.getEarnedPoints()));

      hideReviewsOnTablets();
   }

   private void hideReviewsOnTablets() {
      if (ViewUtils.isTablet(getActivity())) {
         tvReview.setVisibility(GONE);
      }
   }

   @OnClick(R.id.tv_receipt)
   void onReceiptClick() {
      getPresenter().showReceipt();
   }

   @OnClick(R.id.tv_review_merchant)
   void onLeaveReviewClick() {
      getPresenter().reviewMerchant();
   }

   @Override
   public void showReceipt(String url) {
      Intent intent = new Intent(Intent.ACTION_VIEW);
      intent.setDataAndType(Uri.parse(url), "text/html");
      getContext().startActivity(intent);
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
      if (progressDialog != null && progressDialog.isShowing()) {
         progressDialog.dismiss();
      }
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
