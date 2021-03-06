package com.worldventures.dreamtrips.modules.dtl.view.fragment;

import android.app.Dialog;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.worldventures.core.ui.annotations.Layout;
import com.worldventures.core.ui.annotations.MenuResource;
import com.worldventures.core.ui.util.GraphicUtils;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.module.FragmentClassProviderModule;
import com.worldventures.dreamtrips.core.navigation.creator.FragmentClassProvider;
import com.worldventures.dreamtrips.core.rx.RxBaseFragmentWithArgs;
import com.worldventures.dreamtrips.modules.common.view.dialog.ProgressDialogFragment;
import com.worldventures.dreamtrips.modules.common.view.util.TextWatcherAdapter;
import com.worldventures.dreamtrips.modules.dtl.bundle.MerchantBundle;
import com.worldventures.dreamtrips.modules.dtl.helper.DtlEnrollWizard;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.offer.Currency;
import com.worldventures.dreamtrips.modules.dtl.model.transaction.DtlTransaction;
import com.worldventures.dreamtrips.modules.dtl.presenter.DtlScanReceiptPresenter;
import com.worldventures.dreamtrips.modules.dtl.validator.AmountValidator;
import com.worldventures.dreamtrips.modules.dtl.view.custom.CurrencyDTEditText;
import com.worldventures.dreamtrips.social.ui.activity.presenter.ComponentPresenter;

import javax.inject.Inject;
import javax.inject.Named;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import cn.pedant.SweetAlert.SweetAlertDialog;
import mbanje.kurt.fabbutton.CircleImageView;
import mbanje.kurt.fabbutton.FabButton;

@Layout(R.layout.fragment_scan_receipt)
@MenuResource(R.menu.menu_mock)
@ComponentPresenter.ComponentTitle(R.string.dtl_scan_receipt_screen_title)
public class DtlScanReceiptFragment extends RxBaseFragmentWithArgs<DtlScanReceiptPresenter, MerchantBundle> implements DtlScanReceiptPresenter.View {

   @InjectView(R.id.verify) Button verify;
   @InjectView(R.id.scan_receipt) Button scanReceipt;
   @InjectView(R.id.receipt) SimpleDraweeView receipt;
   @InjectView(R.id.shadow) View shadow;
   @InjectView(R.id.fab_progress) FabButton fabProgress;
   @InjectView(R.id.fabbutton_circle) CircleImageView circleView;
   @InjectView(R.id.inputPoints) CurrencyDTEditText amountInput;
   @InjectView(R.id.currency) TextView currencyHint;

   @Inject @Named(FragmentClassProviderModule.DTL_TRANSACTION) FragmentClassProvider<DtlTransaction> fragmentClassProvider;

   protected ProgressDialogFragment progressDialog;
   private DtlEnrollWizard dtlEnrollWizard;

   private TextWatcherAdapter textWatcherAdapter = new TextWatcherAdapter() {
      @Override
      public void onTextChanged(CharSequence s, int start, int before, int count) {
         if (s.length() != 0) {
            getPresenter().onAmountChanged(Double.valueOf(s.toString()));
         }
      }
   };

   @Override
   public void onActivityCreated(Bundle savedInstanceState) {
      super.onActivityCreated(savedInstanceState);
      ButterKnife.<Toolbar>findById(getActivity(), R.id.toolbar_actionbar).setNavigationIcon(R.drawable.ic_close_light);
   }

   @Override
   public void afterCreateView(View rootView) {
      super.afterCreateView(rootView);
      dtlEnrollWizard = new DtlEnrollWizard(router, fragmentClassProvider);
      //
      amountInput.addValidator(new AmountValidator(getString(R.string.dtl_amount_invalid)));
      progressDialog = ProgressDialogFragment.create();
   }

   @Override
   public void onResume() {
      super.onResume();
      ButterKnife.<Toolbar>findById(getActivity(), R.id.toolbar_actionbar)
            .setTitle(R.string.dtl_scan_receipt_screen_title);
      amountInput.addTextChangedListener(textWatcherAdapter);
   }

   @Override
   public void onPause() {
      super.onPause();
      amountInput.removeTextChangedListener(textWatcherAdapter);
   }

   @Override
   protected DtlScanReceiptPresenter createPresenter(Bundle savedInstanceState) {
      return new DtlScanReceiptPresenter(getArgs().getMerchant());
   }

   @Override
   public void preSetBillAmount(double amount) {
      amountInput.setText(String.valueOf(amount));
   }

   @OnClick(R.id.verify)
   void onVerify() {
      if (amountInput.validate()) {
         getPresenter().verify();
      }
   }

   @OnClick(R.id.scan_receipt)
   void onImage() {
      getPresenter().scanReceipt();
   }

   @Override
   public void hideScanButton() {
      scanReceipt.setVisibility(View.GONE);
   }

   @Override
   public void attachReceipt(Uri uri) {
      fabProgress.setVisibility(View.VISIBLE);
      receipt.setController(GraphicUtils.provideFrescoResizingController(uri, receipt.getController()));
      fabProgress.showProgress(false);
      fabProgress.setIcon(R.drawable.ic_upload_done, R.drawable.ic_upload_done);
      int color = fabProgress.getContext().getResources().getColor(R.color.bucket_green);
      circleView.setColor(color);
   }

   @Override
   public void showCurrency(Currency currency) {
      final int padding = amountInput.getPaddingForCurrency(currency.prefix());
      currencyHint.setPadding(8, padding, 0, 0);
      currencyHint.setText(currency.getCurrencyHint());
      amountInput.setCurrencySymbol(currency.prefix());
   }

   @Override
   public void showErrorDialog(String error) {
      SweetAlertDialog alertDialog = new SweetAlertDialog(getContext(), SweetAlertDialog.ERROR_TYPE);
      alertDialog.setTitleText(getString(R.string.dtl_alert_title_error));
      alertDialog.setContentText(error);
      alertDialog.setConfirmText(getActivity().getString(R.string.ok));
      alertDialog.setConfirmClickListener(Dialog::dismiss);
      alertDialog.show();
   }

   @Override
   public void enableVerification() {
      verify.setEnabled(true);
   }

   @Override
   public void disableVerification() {
      verify.setEnabled(false);
   }

   @Override
   public void showProgress() {
      progressDialog.show(getFragmentManager());
   }

   @Override
   public void hideProgress() {
      progressDialog.dismiss();
   }

   @Override
   public void openVerify(DtlTransaction dtlTransaction) {
      progressDialog.dismiss();
      dtlEnrollWizard.proceed(getFragmentManager(), dtlTransaction, getArgs());
   }
}
