package com.worldventures.wallet.util;

import android.content.Context;

import com.worldventures.wallet.domain.entity.SmartCardUser;
import com.worldventures.wallet.service.SmartCardLocationInteractor;
import com.worldventures.wallet.ui.common.navigation.Navigator;
import com.worldventures.wallet.ui.dashboard.CardListScreen;
import com.worldventures.wallet.ui.settings.WalletSettingsScreen;
import com.worldventures.wallet.ui.settings.general.WalletGeneralSettingsScreen;
import com.worldventures.wallet.ui.settings.security.WalletSecuritySettingsScreen;

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

   boolean isSampleCardMode();

   void finishRegularProvisioning(Navigator navigator);

   boolean pinFunctionalityAvailable();

   Observable<Void> clearSettings(SmartCardLocationInteractor interactor);
}
