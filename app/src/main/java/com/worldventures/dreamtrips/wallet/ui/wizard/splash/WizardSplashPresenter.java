package com.worldventures.dreamtrips.wallet.ui.wizard.splash;

import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;

public interface WizardSplashPresenter extends WalletPresenter<WizardSplashScreen> {

   void onBack();

   void startScanCard();

}
