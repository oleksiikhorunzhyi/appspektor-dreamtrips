package com.worldventures.dreamtrips.wallet.ui.wizard.input.scanner.impl;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.zxing.Result;
import com.worldventures.core.utils.HttpErrorHandlingUtil;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.wallet.service.command.http.GetSmartCardStatusCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletBaseController;
import com.worldventures.dreamtrips.wallet.ui.common.helper2.error.ErrorViewFactory;
import com.worldventures.dreamtrips.wallet.ui.common.helper2.error.SimpleDialogErrorViewProvider;
import com.worldventures.dreamtrips.wallet.ui.common.helper2.error.http.HttpErrorViewProvider;
import com.worldventures.dreamtrips.wallet.ui.common.helper2.progress.SimpleDialogProgressView;
import com.worldventures.dreamtrips.wallet.ui.widget.WalletBarCodeFinder;
import com.worldventures.dreamtrips.wallet.ui.widget.WalletBarCodeScanner;
import com.worldventures.dreamtrips.wallet.ui.wizard.input.scanner.WizardScanBarcodePresenter;
import com.worldventures.dreamtrips.wallet.ui.wizard.input.scanner.WizardScanBarcodeScreen;

import javax.inject.Inject;

import io.techery.janet.operationsubscriber.view.ComposableOperationView;
import io.techery.janet.operationsubscriber.view.OperationView;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class WizardScanBarcodeScreenImpl extends WalletBaseController<WizardScanBarcodeScreen, WizardScanBarcodePresenter> implements WizardScanBarcodeScreen, ZXingScannerView.ResultHandler {

   private WalletBarCodeScanner scanner;
   private View contentView;

   @Inject WizardScanBarcodePresenter presenter;
   @Inject HttpErrorHandlingUtil httpErrorHandlingUtil;

   @Override
   protected void onFinishInflate(View view) {
      super.onFinishInflate(view);
      final Toolbar toolbar = view.findViewById(R.id.toolbar);
      toolbar.setNavigationOnClickListener(v -> getPresenter().goBack());
      contentView = view.findViewById(R.id.content);
      final WalletBarCodeFinder finder = view.findViewById(R.id.scanner_view_finder);
      scanner = view.findViewById(R.id.scanner_view);
      scanner.setBarCodeFinder(finder);
      scanner.setResultHandler(this);
      final View manualInputView = view.findViewById(R.id.wallet_wizard_scan_barcode_manual_input);
      manualInputView.setOnClickListener(manualInput -> getPresenter().startManualInput());
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
   protected void onActivityStarted(Activity activity) {
      super.onActivityStarted(activity);
      if (isAttached()) getPresenter().requestCamera();
   }

   @Override
   protected void onActivityStopped(Activity activity) {
      if (scanner != null) scanner.stopCamera();
      super.onActivityStopped(activity);
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
                        R.string.wallet_wizard_scan_barcode_invalid_format,
                        command -> presenter.retryScan(), null, () -> presenter.retryScan()))
                  .addProvider(new HttpErrorViewProvider<>(getContext(), httpErrorHandlingUtil,
                        command -> getPresenter().retry(command.barcode),
                        command -> presenter.retryScan()))
                  .build()
      );
   }

   @Override
   public void showErrorCardIsAssignedDialog() {
      new MaterialDialog.Builder(getContext())
            .content(R.string.wallet_wizard_scan_barcode_card_is_assigned)
            .positiveText(R.string.ok)
            .onPositive((dialog, which) -> presenter.retryScan())
            .cancelListener(dialog -> presenter.retryScan())
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
}
