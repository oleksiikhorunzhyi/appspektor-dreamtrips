package com.worldventures.dreamtrips.wallet.ui.settings.general;

import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenterI;

public interface WalletGeneralSettingsPresenter extends WalletPresenterI<WalletGeneralSettingsScreen> {

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
