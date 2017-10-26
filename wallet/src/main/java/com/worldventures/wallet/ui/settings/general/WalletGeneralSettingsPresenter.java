package com.worldventures.wallet.ui.settings.general;

import com.worldventures.wallet.ui.common.base.WalletPresenter;

public interface WalletGeneralSettingsPresenter extends WalletPresenter<WalletGeneralSettingsScreen> {

   void goBack();

   void openProfileScreen();

   void openAboutScreen();

   void openSoftwareUpdateScreen();

   void openDisplayOptionsScreen();

   void onClickFactoryResetSmartCard();

   void openSetupNewSmartCardScreen();

   void onClickRestartSmartCard();

   void onConfirmedRestartSmartCard();

   void openFactoryResetScreen();
}
