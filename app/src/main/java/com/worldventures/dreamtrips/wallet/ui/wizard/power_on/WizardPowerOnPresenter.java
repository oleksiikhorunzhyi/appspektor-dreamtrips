package com.worldventures.dreamtrips.wallet.ui.wizard.power_on;

import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;

public interface WizardPowerOnPresenter extends WalletPresenter<WizardPowerOnScreen> {

   void onBack();

   void openUserAgreement();

   void openCheckScreen();

}
