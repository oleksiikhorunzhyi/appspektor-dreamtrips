package com.worldventures.dreamtrips.wallet.ui.wizard.input.manual;

import com.worldventures.dreamtrips.util.HttpErrorHandlingUtil;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenterI;

public interface WizardManualInputPresenter extends WalletPresenterI<WizardManualInputScreen> {

   void goBack();

   void checkBarcode(String barcode);

   HttpErrorHandlingUtil httpErrorHandlingUtil();

   void retry(String barcode);
}
