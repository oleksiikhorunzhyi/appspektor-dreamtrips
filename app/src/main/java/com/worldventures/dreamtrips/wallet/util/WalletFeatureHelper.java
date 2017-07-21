package com.worldventures.dreamtrips.wallet.util;

import android.content.Context;

import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardUser;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;
import com.worldventures.dreamtrips.wallet.ui.dashboard.CardListScreen;
import com.worldventures.dreamtrips.wallet.ui.settings.WalletSettingsScreen;
import com.worldventures.dreamtrips.wallet.ui.settings.general.WalletGeneralSettingsScreen;
import com.worldventures.dreamtrips.wallet.ui.settings.security.WalletSecuritySettingsScreen;

import rx.Observable;
import rx.functions.Action0;

public interface WalletFeatureHelper {

   void prepareSettingsScreen(WalletSettingsScreen view);
   //SettingsGeneral
   void prepareSettingsGeneralScreen(WalletGeneralSettingsScreen view);
   void openEditProfile(Context context, Action0 action);
   //SettingsSecurity
   void prepareSettingsSecurityScreen(WalletSecuritySettingsScreen view);
   void openFindCard(Context context, Action0 action);
   //Dashboard
   void prepareDashboardScreen(CardListScreen view);
   boolean addingCardIsNotSupported();
   boolean offlineModeState(boolean isOfflineMode);
   Observable<Void> onUserAssigned(SmartCardUser user);
   void onUserFetchedFromServer(SmartCardUser user);
   void navigateFromSetupUserScreen(Navigator navigator, SmartCardUser user, boolean withoutLast);
   boolean isCardDetailSupported();
   boolean isCardSyncSupported();
}
