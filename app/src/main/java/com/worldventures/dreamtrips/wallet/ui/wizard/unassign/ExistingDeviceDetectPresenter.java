package com.worldventures.dreamtrips.wallet.ui.wizard.unassign;

import com.worldventures.dreamtrips.util.HttpErrorHandlingUtil;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenterI;

public interface ExistingDeviceDetectPresenter extends WalletPresenterI<ExistingDeviceDetectScreen> {

   void goBack();

   void repair();

   HttpErrorHandlingUtil httpErrorHandlingUtil();

   void retryReAssigning();

   void repairConfirmed();
}
