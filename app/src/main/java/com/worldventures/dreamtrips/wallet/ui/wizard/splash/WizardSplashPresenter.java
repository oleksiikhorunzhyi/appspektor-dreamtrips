package com.worldventures.dreamtrips.wallet.ui.wizard.splash;

import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenterI;

public interface WizardSplashPresenter extends WalletPresenterI<WizardSplashScreen> {

   void onBack();

   void startScanCard();

}
