package com.worldventures.dreamtrips.modules.dtl.view.fragment;

import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.google.zxing.Result;
import com.innahema.collections.query.queriables.Queryable;
import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.api.error.ErrorResponse;
import com.worldventures.dreamtrips.core.api.error.FieldError;
import com.worldventures.dreamtrips.core.module.RouteCreatorModule;
import com.worldventures.dreamtrips.core.navigation.creator.RouteCreator;
import com.worldventures.dreamtrips.core.permission.PermissionConstants;
import com.worldventures.dreamtrips.core.permission.PermissionDispatcher;
import com.worldventures.dreamtrips.core.permission.PermissionSubscriber;
import com.worldventures.dreamtrips.core.rx.RxBaseFragmentWithArgs;
import com.worldventures.dreamtrips.modules.common.view.custom.ImageryDraweeView;
import com.worldventures.dreamtrips.modules.dtl.bundle.MerchantBundle;
import com.worldventures.dreamtrips.modules.dtl.helper.DtlEnrollWizard;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchant;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.Merchant;
import com.worldventures.dreamtrips.modules.dtl.model.transaction.DtlTransaction;
import com.worldventures.dreamtrips.modules.dtl.presenter.DtlScanQrCodePresenter;

import javax.inject.Inject;
import javax.inject.Named;

import butterknife.ButterKnife;
import butterknife.InjectView;
import cn.pedant.SweetAlert.SweetAlertDialog;
import me.dm7.barcodescanner.zxing.ZXingScannerView;
import timber.log.Timber;

@Layout(R.layout.fragment_scan_qr)
public class DtlScanQrCodeFragment extends RxBaseFragmentWithArgs<DtlScanQrCodePresenter, MerchantBundle> implements DtlScanQrCodePresenter.View, ZXingScannerView.ResultHandler {

   @Inject @Named(RouteCreatorModule.DTL_TRANSACTION) RouteCreator<DtlTransaction> routeCreator;
   @Inject PermissionDispatcher permissionDispatcher;

   //
   @InjectView(R.id.name) TextView name;
   @InjectView(R.id.address) TextView address;
   @InjectView(R.id.merchant_image) ImageryDraweeView merchantImage;
   @InjectView(R.id.scanner_view) ZXingScannerView scanner;
   //
   private DtlEnrollWizard dtlEnrollWizard;
   //
   private SweetAlertDialog progressDialog;
   private SweetAlertDialog alertDialog;

   @Override
   protected DtlScanQrCodePresenter createPresenter(Bundle savedInstanceState) {
      return new DtlScanQrCodePresenter(getArgs().getMerchant());
   }

   @Override
   public void afterCreateView(View rootView) {
      super.afterCreateView(rootView);
      dtlEnrollWizard = new DtlEnrollWizard(router, routeCreator);
   }

   @Override
   public void onResume() {
      super.onResume();
      ButterKnife.<Toolbar>findById(getActivity(), R.id.toolbar_actionbar).setTitle(R.string.dtl_barcode_title);
      permissionDispatcher.requestPermission(PermissionConstants.CAMERA_PERMISSIONS)
            .compose(this::bind)
            .subscribe(new PermissionSubscriber().onPermissionGrantedAction(this::startCamera)
                  .onPermissionRationaleAction(this::showRationaleForCamera)
                  .onPermissionDeniedAction(this::showDeniedForCamera));
      scanner.setResultHandler(this);
   }

   void startCamera() {
      scanner.startCamera();
   }

   void showRationaleForCamera() {
      Snackbar.make(getView(), R.string.permission_camera_rationale, Snackbar.LENGTH_SHORT).show();
   }

   void showDeniedForCamera() {
      Snackbar.make(getView(), R.string.no_camera_permission, Snackbar.LENGTH_SHORT).show();
   }

   @Override
   public void setMerchant(Merchant merchant) {
      name.setText(merchant.displayName());
      if (!TextUtils.isEmpty(merchant.address())) {
         address.setText(String.format("%s, %s, %s, %s", merchant.address(), merchant.city(), merchant.state(), merchant
               .zip()));
      }
      if (merchant.images() != null) {
         merchantImage.setImageUrl(merchant.images().get(0).getImagePath());
      }
   }

   @Override
   public void onPause() {
      super.onPause();
      scanner.stopCamera();
   }

   @Override
   public void handleResult(final Result rawResult) {
      String contents = rawResult.getText();
      getPresenter().codeScanned(contents);
      Timber.d(contents);
   }

   @Override
   public void openScanReceipt(DtlTransaction dtlTransaction) {
      dtlEnrollWizard.clearAndProceed(getFragmentManager(), dtlTransaction, getArgs());
   }

   @Override
   public void finish() {
      getActivity().finish();
   }

   @Override
   public void hideProgress() {
      if (progressDialog != null) progressDialog.dismissWithAnimation();
   }

   @Override
   public void showProgress(@StringRes int titleText) {
      if (progressDialog == null || !progressDialog.isShowing()) {
         scanner.stopCamera();
         //
         progressDialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.PROGRESS_TYPE);
         progressDialog.getProgressHelper().setBarColor(getResources().getColor(R.color.theme_main));
         progressDialog.setTitleText(getString(titleText));
         progressDialog.setCancelable(false);
         progressDialog.show();
      } else progressDialog.setTitle(getString(titleText));
   }

   @Override
   public void photoUploadError() {
      showImageUploadError(sweetAlertDialog -> {
         sweetAlertDialog.dismiss();
         getPresenter().photoUploadFailed();
      });
   }

   @Override
   public void noConnection() {
      showImageUploadError(sweetAlertDialog -> {
         sweetAlertDialog.dismissWithAnimation();
         scanner.startCamera();
      });
   }

   private void showImageUploadError(SweetAlertDialog.OnSweetClickListener onSweetClickListener) {
      if (alertDialog != null && alertDialog.isShowing()) return;
      //
      alertDialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE).setTitleText(getString(R.string.alert))
            .setContentText(getString(R.string.dtl_photo_upload_error))
            .setConfirmText(getString(R.string.ok))
            .setConfirmClickListener(onSweetClickListener);
      alertDialog.setCancelable(false);
      alertDialog.show();
   }

   @Override
   public void onApiCallFailed() {
      hideProgress();
   }

   @Override
   public boolean onApiError(ErrorResponse errorResponse) {
      FieldError fieldError = Queryable.from(errorResponse.getErrors()).firstOrDefault();
      //
      if (fieldError != null) {
         SweetAlertDialog alertDialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE).setTitleText(getString(R.string.alert))
               .setContentText(fieldError.getFirstMessage())
               .setConfirmText(getString(R.string.ok))
               .setConfirmClickListener(sweetAlertDialog -> {
                  switch (fieldError.field) {
                     case DtlTransaction.BILL_TOTAL:
                     case DtlTransaction.RECEIPT_PHOTO_URL:
                        getPresenter().photoUploadFailed();
                        break;
                     case DtlTransaction.LOCATION:
                     case DtlTransaction.CHECKIN:
                        getActivity().finish();
                        break;
                     case DtlTransaction.MERCHANT_TOKEN:
                     default:
                        sweetAlertDialog.dismissWithAnimation();
                        scanner.startCamera();
                        break;
                  }
               });
         alertDialog.setCancelable(false);
         alertDialog.show();
         return true;
      }
      //
      return false;
   }
}
