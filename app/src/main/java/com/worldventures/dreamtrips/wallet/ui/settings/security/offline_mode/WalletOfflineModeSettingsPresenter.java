package com.worldventures.dreamtrips.wallet.ui.settings.security.offline_mode;

import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenterI;

public interface WalletOfflineModeSettingsPresenter extends WalletPresenterI<WalletOfflineModeSettingsScreen> {

   void goBack();

   void switchOfflineMode();

   void switchOfflineModeCanceled();

   void navigateToSystemSettings();

   void fetchOfflineModeState();

}
