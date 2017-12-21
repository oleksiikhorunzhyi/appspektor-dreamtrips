package com.worldventures.wallet.ui.wizard.assign;

import com.worldventures.wallet.ui.common.base.WalletPresenter;

public interface WizardAssignUserPresenter extends WalletPresenter<WizardAssignUserScreen> {

   void onWizardComplete();

   void onWizardCancel();

}
