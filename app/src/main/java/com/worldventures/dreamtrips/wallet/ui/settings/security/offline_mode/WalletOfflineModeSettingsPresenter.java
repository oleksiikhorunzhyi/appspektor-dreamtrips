package com.worldventures.dreamtrips.wallet.ui.settings.security.offline_mode;

import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;

public interface WalletOfflineModeSettingsPresenter extends WalletPresenter<WalletOfflineModeSettingsScreen> {

   void goBack();

   void switchOfflineMode();

   void switchOfflineModeCanceled();

   void navigateToSystemSettings();

   void fetchOfflineModeState();

}
