package com.worldventures.wallet.ui.wizard.input.scanner;

import com.worldventures.wallet.ui.common.base.WalletPresenter;

public interface WizardScanBarcodePresenter extends WalletPresenter<WizardScanBarcodeScreen> {

   void goBack();

   void requestCamera();

   void barcodeScanned(String barcode);

   void startManualInput();

   void retry(String barcode);

   void retryAssignedToCurrentDevice();

   void retryScan();
}
