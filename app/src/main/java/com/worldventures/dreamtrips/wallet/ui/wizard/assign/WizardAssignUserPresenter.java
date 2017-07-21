package com.worldventures.dreamtrips.wallet.ui.wizard.assign;

import com.worldventures.dreamtrips.util.HttpErrorHandlingUtil;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenterI;


public interface WizardAssignUserPresenter extends WalletPresenterI<WizardAssignUserScreen> {

   void onWizardComplete();

   void onWizardCancel();

   HttpErrorHandlingUtil httpErrorHandlingUtil();

}
