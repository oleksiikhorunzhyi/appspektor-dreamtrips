package com.worldventures.dreamtrips.wallet.ui.settings.security.lostcard;

import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;

import rx.Observable;

public interface LostCardScreen extends WalletScreen{

   Observable<Boolean> observeTrackingEnable();

   void setMapEnabled(boolean enabled);

   void setTrackingSwitchStatus(boolean checked);

   void showRationaleForLocation();

   void showDeniedForLocation();

   void showDisableConfirmationDialog();
}
