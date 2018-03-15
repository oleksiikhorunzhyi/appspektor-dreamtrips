package com.worldventures.dreamtrips.modules.dtl.view.fragment;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import com.afollestad.materialdialogs.MaterialDialog;
import com.facebook.drawee.view.SimpleDraweeView;
import com.worldventures.core.ui.annotations.Layout;
import com.worldventures.core.ui.annotations.MenuResource;
import com.worldventures.core.ui.util.GraphicUtils;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.router.NavigationConfigBuilder;
import com.worldventures.dreamtrips.core.rx.RxBaseFragmentWithArgs;
import com.worldventures.dreamtrips.modules.dtl.bundle.MerchantBundle;
import com.worldventures.dreamtrips.modules.dtl.bundle.ThrstFlowBundle;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.Merchant;
import com.worldventures.dreamtrips.modules.dtl.presenter.DtlThrstScanReceiptPresenter;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import mbanje.kurt.fabbutton.CircleImageView;
import mbanje.kurt.fabbutton.FabButton;

@Layout(R.layout.fragment_thrst_scan_receipt)
@MenuResource(R.menu.menu_mock)
public class DtlThrstScanReceiptFragment extends RxBaseFragmentWithArgs<DtlThrstScanReceiptPresenter, MerchantBundle> implements DtlThrstScanReceiptPresenter.View {

   @InjectView(R.id.verify) Button verify;
   @InjectView(R.id.scan_receipt) Button scanReceipt;
   @InjectView(R.id.receipt) SimpleDraweeView receipt;
   @InjectView(R.id.fab_progress) FabButton fabProgress;
   @InjectView(R.id.fabbutton_circle) CircleImageView circleView;

   private MaterialDialog progressDialog;

   @Override
   public void onActivityCreated(Bundle savedInstanceState) {
      super.onActivityCreated(savedInstanceState);
      ButterKnife.<Toolbar>findById(getActivity(), R.id.toolbar_actionbar).setNavigationIcon(R.drawable.ic_close_light);
   }

   @Override
   public void afterCreateView(View rootView) {
      super.afterCreateView(rootView);
   }

   @Override
   public void onResume() {
      super.onResume();
      ButterKnife.<Toolbar>findById(getActivity(), R.id.toolbar_actionbar)
            .setTitle(R.string.dtl_thrst_scan_receipt_screen_title);
   }

   @Override
   protected DtlThrstScanReceiptPresenter createPresenter(Bundle savedInstanceState) {
      return new DtlThrstScanReceiptPresenter(getArgs().getMerchant());
   }

   @OnClick(R.id.verify)
   void onVerify() {
      getPresenter().openThrstFlow();
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
   public void enableVerification() {
      verify.setEnabled(true);
   }

   @Override
   public void disableVerification() {
      verify.setEnabled(false);
   }

   @Override
   public void showProgress() {
      hideProgress();

      progressDialog = new MaterialDialog.Builder(getContext()).progress(true, 0)
            .content(R.string.loading)
            .cancelable(false)
            .canceledOnTouchOutside(false)
            .show();
   }

   @Override
   public void hideProgress() {
      if (progressDialog != null && progressDialog.isShowing()) {
         progressDialog.dismiss();
      }
   }

   @Override
   public void showReceiptLoadingError(String filePath) {
      hideProgress();

      progressDialog = new MaterialDialog.Builder(getContext())
            .content(R.string.receipt_upload_error_caption)
            .negativeText(R.string.cancel)
            .positiveText(R.string.retry)
            .cancelable(false)
            .canceledOnTouchOutside(false)
            .onNegative((dialog, which) -> getActivity().onBackPressed())
            .onPositive((dialog, which) -> getPresenter().retryPhotoUpload(filePath))
            .show();
   }

   @Override
   public void showThrstOpeningError() {
      informUser(R.string.thrst_opening_error_caption);
   }

   @Override
   public void onApiError() {
   }

   @Override
   public void openThrstFlow(Merchant merchant, String receiptUrl, String token, String transactionId) {
      router.moveTo(DtlThrstFlowFragment.class, NavigationConfigBuilder.forFragment()
            .containerId(R.id.container_main)
            .backStackEnabled(true)
            .clearBackStack(false)
            .data(new ThrstFlowBundle(merchant, receiptUrl, token, transactionId))
            .fragmentManager(getFragmentManager())
            .build());
   }
}
