package com.worldventures.wallet.ui.settings.general.newcard.success;

import com.worldventures.wallet.ui.common.base.WalletPresenter;

public interface UnassignSuccessPresenter extends WalletPresenter<UnassignSuccessScreen> {
   void goBack();

   void navigateToWizard();
}