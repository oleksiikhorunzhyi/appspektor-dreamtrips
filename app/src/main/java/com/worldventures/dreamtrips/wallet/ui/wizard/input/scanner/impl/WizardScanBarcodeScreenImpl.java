package com.worldventures.dreamtrips.wallet.ui.wizard.input.scanner.impl;


import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.zxing.Result;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.wallet.service.command.http.GetSmartCardStatusCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletBaseController;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.OperationScreen;
import com.worldventures.dreamtrips.wallet.ui.common.helper2.error.ErrorViewFactory;
import com.worldventures.dreamtrips.wallet.ui.common.helper2.error.SimpleDialogErrorViewProvider;
import com.worldventures.dreamtrips.wallet.ui.common.helper2.error.http.HttpErrorViewProvider;
import com.worldventures.dreamtrips.wallet.ui.common.helper2.progress.SimpleDialogProgressView;
import com.worldventures.dreamtrips.wallet.ui.widget.WalletBarCodeFinder;
import com.worldventures.dreamtrips.wallet.ui.widget.WalletBarCodeScanner;
import com.worldventures.dreamtrips.wallet.ui.wizard.input.scanner.WizardScanBarcodePresenter;
import com.worldventures.dreamtrips.wallet.ui.wizard.input.scanner.WizardScanBarcodeScreen;

import javax.inject.Inject;

import butterknife.InjectView;
import butterknife.OnClick;
import io.techery.janet.operationsubscriber.view.ComposableOperationView;
import io.techery.janet.operationsubscriber.view.OperationView;

public class WizardScanBarcodeScreenImpl extends WalletBaseController<WizardScanBarcodeScreen, WizardScanBarcodePresenter> implements WizardScanBarcodeScreen {

   @InjectView(R.id.toolbar) Toolbar toolbar;
   @InjectView(R.id.scanner_view) WalletBarCodeScanner scanner;
   @InjectView(R.id.scanner_view_finder) WalletBarCodeFinder finder;
   @InjectView(R.id.content) View contentView;

   @Inject WizardScanBarcodePresenter presenter;

   @Override
   protected void onFinishInflate(View view) {
      super.onFinishInflate(view);
      toolbar.setNavigationOnClickListener(v -> getPresenter().goBack());
      scanner.setBarCodeFinder(finder);
      scanner.setResultHandler(this);
   }

   @Override
   public View inflateView(LayoutInflater layoutInflater, ViewGroup viewGroup) {
      return layoutInflater.inflate(R.layout.screen_wallet_wizard_barcode_scan, viewGroup, false);
   }

   @Override
   public boolean supportConnectionStatusLabel() {
      return false;
   }

   @Override
   public boolean supportHttpConnectionStatusLabel() {
      return false;
   }

   @Override
   public void onPostEnterAnimation() {
      getPresenter().requestCamera();
   }

   @Override
   public void onPreExitAnimation() {
      scanner.stopCamera();
   }

   @Override
   protected void onAttach(@NonNull View view) {
      super.onAttach(view);
      getPresenter().requestCamera();
   }

   @Override
   protected void onDetach(@NonNull View view) {
      scanner.stopCamera();
      super.onDetach(view);
      getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
   }

   @Override
   public WizardScanBarcodePresenter getPresenter() {
      return presenter;
   }

   @Override
   public OperationScreen provideOperationDelegate() { return null; }

   @Override
   public void startCamera() {
      scanner.startCamera();
   }

   @Override
   public void showRationaleForCamera() {
      Snackbar.make(getView(), R.string.permission_camera_rationale, Snackbar.LENGTH_SHORT).show();
   }

   @Override
   public void showDeniedForCamera() {
      Snackbar.make(getView(), R.string.no_camera_permission, Snackbar.LENGTH_SHORT).show();
   }

   @Override
   public View getContentView() {
      return contentView;
   }

   @Override
   public OperationView<GetSmartCardStatusCommand> provideOperationFetchCardStatus() {
      return new ComposableOperationView<>(
            new SimpleDialogProgressView<>(getContext(), R.string.wallet_wizard_assigning_msg, false),
            ErrorViewFactory.<GetSmartCardStatusCommand>builder()
                  .addProvider(new SimpleDialogErrorViewProvider<>(getContext(), NumberFormatException.class,
                        R.string.wallet_wizard_scan_barcode_invalid_format))
                  .addProvider(new HttpErrorViewProvider<>(getContext(), getPresenter().httpErrorHandlingUtil(),
                        command -> getPresenter().retry(command.barcode),
                        command -> { /*nothing*/ }))
                  .build()
      );
   }

   @Override
   public void showErrorCardIsAssignedDialog() {
      new MaterialDialog.Builder(getContext())
            .content(R.string.wallet_wizard_scan_barcode_card_is_assigned)
            .positiveText(R.string.ok)
            .onPositive((dialog, which) -> dialog.dismiss())
            .show();
   }

   @Override
   public void reset() {
      scanner.resumeCameraPreview(this);
   }

   @Override
   public void handleResult(Result result) {
      getPresenter().barcodeScanned(result.getText());
   }

   @OnClick(R.id.wallet_wizard_scan_barcode_manual_input)
   void onInputManuallyClicked() {
      getPresenter().startManualInput();
   }
}
