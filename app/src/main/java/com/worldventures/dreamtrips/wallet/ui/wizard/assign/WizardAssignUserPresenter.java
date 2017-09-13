package com.worldventures.dreamtrips.wallet.ui.wizard.assign;

import com.worldventures.dreamtrips.core.utils.HttpErrorHandlingUtil;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;


public interface WizardAssignUserPresenter extends WalletPresenter<WizardAssignUserScreen> {

   void onWizardComplete();

   void onWizardCancel();

   HttpErrorHandlingUtil httpErrorHandlingUtil();

}
