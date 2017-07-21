package com.worldventures.dreamtrips.wallet.ui.settings;

import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenterI;

public interface WalletSettingsPresenter extends WalletPresenterI<WalletSettingsScreen> {
   void goBack();

   void openGeneralScreen();

   void openSecurityScreen();

   void openHelpScreen();
}
