package com.worldventures.dreamtrips.wallet.ui.settings.general.newcard.success;

import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenterI;

public interface UnassignSuccessPresenter extends WalletPresenterI<UnassignSuccessScreen> {
   void goBack();

   void navigateToWizard();
}