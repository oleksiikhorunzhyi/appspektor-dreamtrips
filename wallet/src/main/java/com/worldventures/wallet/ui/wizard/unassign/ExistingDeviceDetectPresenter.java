package com.worldventures.wallet.ui.wizard.unassign;

import com.worldventures.core.utils.HttpErrorHandlingUtil;
import com.worldventures.wallet.ui.common.base.WalletPresenter;

public interface ExistingDeviceDetectPresenter extends WalletPresenter<ExistingDeviceDetectScreen> {

   void goBack();

   void repair();

   HttpErrorHandlingUtil httpErrorHandlingUtil();

   void retryReAssigning();

   void repairConfirmed();
}
