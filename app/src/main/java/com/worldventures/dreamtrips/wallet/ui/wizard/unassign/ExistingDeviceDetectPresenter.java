package com.worldventures.dreamtrips.wallet.ui.wizard.unassign;

import com.worldventures.core.utils.HttpErrorHandlingUtil;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;

public interface ExistingDeviceDetectPresenter extends WalletPresenter<ExistingDeviceDetectScreen> {

   void goBack();

   void repair();

   HttpErrorHandlingUtil httpErrorHandlingUtil();

   void retryReAssigning();

   void repairConfirmed();
}
