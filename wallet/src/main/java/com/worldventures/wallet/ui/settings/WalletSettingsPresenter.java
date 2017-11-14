package com.worldventures.wallet.ui.settings;

import com.worldventures.wallet.ui.common.base.WalletPresenter;

public interface WalletSettingsPresenter extends WalletPresenter<WalletSettingsScreen> {
   void goBack();

   void openGeneralScreen();

   void openSecurityScreen();

   void openHelpScreen();
}
