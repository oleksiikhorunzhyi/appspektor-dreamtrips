package com.worldventures.wallet.ui.wizard.input.manual;

import com.worldventures.wallet.ui.common.base.WalletPresenter;

public interface WizardManualInputPresenter extends WalletPresenter<WizardManualInputScreen> {

   void goBack();

   void checkBarcode(String barcode);

   void retry(String barcode);

   void retryAssignedToCurrentDevice();
}
