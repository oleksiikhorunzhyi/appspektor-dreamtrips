package com.worldventures.dreamtrips.wallet.ui.wizard.input.scanner;

import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;

public interface WizardScanBarcodePresenter extends WalletPresenter<WizardScanBarcodeScreen> {

   void goBack();

   void requestCamera();

   void barcodeScanned(String barcode);

   void startManualInput();

   void retry(String barcode);

   void retryScan();
}
