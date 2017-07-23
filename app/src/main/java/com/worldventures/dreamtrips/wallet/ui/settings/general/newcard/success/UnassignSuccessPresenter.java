package com.worldventures.dreamtrips.wallet.ui.settings.general.newcard.success;

import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;

public interface UnassignSuccessPresenter extends WalletPresenter<UnassignSuccessScreen> {
   void goBack();

   void navigateToWizard();
}