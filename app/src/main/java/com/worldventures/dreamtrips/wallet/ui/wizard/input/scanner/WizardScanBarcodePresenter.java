package com.worldventures.dreamtrips.wallet.ui.wizard.input.scanner;

import com.worldventures.dreamtrips.util.HttpErrorHandlingUtil;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenterI;

public interface WizardScanBarcodePresenter extends WalletPresenterI<WizardScanBarcodeScreen> {

   void goBack();

   void requestCamera();

   void barcodeScanned(String barcode);

   void startManualInput();

   HttpErrorHandlingUtil httpErrorHandlingUtil();

   void retry(String barcode);
}
