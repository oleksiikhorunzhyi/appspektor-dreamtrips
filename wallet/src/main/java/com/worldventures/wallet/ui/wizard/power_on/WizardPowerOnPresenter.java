package com.worldventures.wallet.ui.wizard.power_on;

import com.worldventures.wallet.ui.common.base.WalletPresenter;

public interface WizardPowerOnPresenter extends WalletPresenter<WizardPowerOnScreen> {

   void onBack();

   void openUserAgreement();

   void openCheckScreen();

}
