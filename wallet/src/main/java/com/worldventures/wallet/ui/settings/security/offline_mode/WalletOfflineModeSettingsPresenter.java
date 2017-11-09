package com.worldventures.wallet.ui.settings.security.offline_mode;

import com.worldventures.wallet.ui.common.base.WalletPresenter;

public interface WalletOfflineModeSettingsPresenter extends WalletPresenter<WalletOfflineModeSettingsScreen> {

   void goBack();

   void switchOfflineMode();

   void switchOfflineModeCanceled();

   void navigateToSystemSettings();

   void fetchOfflineModeState();

}
