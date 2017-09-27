package com.worldventures.dreamtrips.wallet.ui.settings;

import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;

public interface WalletSettingsPresenter extends WalletPresenter<WalletSettingsScreen> {
   void goBack();

   void openGeneralScreen();

   void openSecurityScreen();

   void openHelpScreen();
}
