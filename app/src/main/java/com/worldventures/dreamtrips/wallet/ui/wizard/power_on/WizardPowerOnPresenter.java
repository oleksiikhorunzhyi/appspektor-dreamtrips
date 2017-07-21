package com.worldventures.dreamtrips.wallet.ui.wizard.power_on;

import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenterI;

public interface WizardPowerOnPresenter extends WalletPresenterI<WizardPowerOnScreen> {

   void onBack();

   void openUserAgreement();

   void openCheckScreen();

}
