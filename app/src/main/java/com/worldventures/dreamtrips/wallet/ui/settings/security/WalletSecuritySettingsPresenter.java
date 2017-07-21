package com.worldventures.dreamtrips.wallet.ui.settings.security;

import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenterI;

public interface WalletSecuritySettingsPresenter extends WalletPresenterI<WalletSecuritySettingsScreen> {

   void goBack();

   void openOfflineModeScreen();

   void openLostCardScreen();

   void disableDefaultCardTimer();

   void resetPin();

   void autoClearSmartCardClick();

   void addPin();

   void removePin();
}
