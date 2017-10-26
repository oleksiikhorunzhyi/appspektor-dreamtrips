package com.worldventures.wallet.ui.settings.security;

import com.worldventures.wallet.ui.common.base.WalletPresenter;

public interface WalletSecuritySettingsPresenter extends WalletPresenter<WalletSecuritySettingsScreen> {

   void goBack();

   void openOfflineModeScreen();

   void openLostCardScreen();

   void disableDefaultCardTimer();

   void resetPin();

   void autoClearSmartCardClick();

   void addPin();

   void removePin();

   void lockStatusFailed();
}
