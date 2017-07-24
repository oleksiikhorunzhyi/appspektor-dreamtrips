package com.worldventures.dreamtrips.wallet.ui.wizard.input.scanner;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.zxing.Result;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.wallet.service.command.http.GetSmartCardStatusCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletLinearLayout;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.OperationScreen;
import com.worldventures.dreamtrips.wallet.ui.common.helper2.error.ErrorViewFactory;
import com.worldventures.dreamtrips.wallet.ui.common.helper2.error.http.HttpErrorViewProvider;
import com.worldventures.dreamtrips.wallet.ui.common.helper2.progress.SimpleDialogProgressView;
import com.worldventures.dreamtrips.wallet.ui.widget.WalletBarCodeFinder;
import com.worldventures.dreamtrips.wallet.ui.widget.WalletBarCodeScanner;

import butterknife.InjectView;
import butterknife.OnClick;
import io.techery.janet.operationsubscriber.view.ComposableOperationView;
import io.techery.janet.operationsubscriber.view.OperationView;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class WizardScanBarcodeScreen extends WalletLinearLayout<WizardScanBarcodePresenter.Screen, WizardScanBarcodePresenter, WizardScanBarcodePath>
      implements WizardScanBarcodePresenter.Screen, ZXingScannerView.ResultHandler {

   @InjectView(R.id.toolbar) Toolbar toolbar;
   @InjectView(R.id.scanner_view) WalletBarCodeScanner scanner;
   @InjectView(R.id.scanner_view_finder) WalletBarCodeFinder finder;
   @InjectView(R.id.content) View contentView;
   private int visibility;

   public WizardScanBarcodeScreen(Context context) {
      super(context);
   }

   public WizardScanBarcodeScreen(Context context, AttributeSet attrs) {
      super(context, attrs);
   }

   @Override
   protected void onFinishInflate() {
      super.onFinishInflate();
      supportConnectionStatusLabel(false);
      if (isInEditMode()) return;
      toolbar.setNavigationOnClickListener(v -> presenter.goBack());
      scanner.setBarCodeFinder(finder);
      scanner.setResultHandler(this);
   }

   public void onPostEnterAnimation() {
      presenter.requestCamera();
   }

   public void onPreExitAnimation() {
      scanner.stopCamera();
   }

   @Override
   protected void onWindowVisibilityChanged(int visibility) {
      super.onWindowVisibilityChanged(visibility);
      if (isInEditMode()) return;
      if (visibility == VISIBLE) {
         if (this.visibility == GONE || this.visibility == INVISIBLE) presenter.requestCamera();
      } else {
         scanner.stopCamera();
      }
      this.visibility = visibility;
   }

   @NonNull
   @Override
   public WizardScanBarcodePresenter createPresenter() {
      return new WizardScanBarcodePresenter(getContext(), getInjector());
   }

   @Override
   public OperationScreen provideOperationDelegate() { return null; }

   @Override
   public void startCamera() {
      scanner.startCamera();
   }

   @Override
   public void showRationaleForCamera() {
      Snackbar.make(this, R.string.permission_camera_rationale, Snackbar.LENGTH_SHORT).show();
   }

   @Override
   public void showDeniedForCamera() {
      Snackbar.make(this, R.string.no_camera_permission, Snackbar.LENGTH_SHORT).show();
   }

   @Override
   public OperationView<GetSmartCardStatusCommand> provideOperationFetchCardStatus() {
      return new ComposableOperationView<>(
            new SimpleDialogProgressView<>(getContext(), R.string.wallet_wizard_assigning_msg, false),
            ErrorViewFactory.<GetSmartCardStatusCommand>builder()
                  .addProvider(new HttpErrorViewProvider<>(getContext(), presenter.httpErrorHandlingUtil(),
                        c -> presenter.retry(c.barcode), c -> { /*nothing*/ }))
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
   public View getView() {
      return this;
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