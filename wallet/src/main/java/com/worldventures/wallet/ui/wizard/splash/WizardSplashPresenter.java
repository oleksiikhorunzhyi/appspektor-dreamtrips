package com.worldventures.wallet.ui.wizard.splash;

import com.worldventures.wallet.ui.common.base.WalletPresenter;

public interface WizardSplashPresenter extends WalletPresenter<WizardSplashScreen> {

   void onBack();

   void startScanCard();

}
