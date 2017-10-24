package com.worldventures.dreamtrips.wallet.util;

import android.content.Context;

import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardUser;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;
import com.worldventures.dreamtrips.wallet.ui.dashboard.CardListPresenter;
import com.worldventures.dreamtrips.wallet.ui.settings.WalletSettingsPresenter;
import com.worldventures.dreamtrips.wallet.ui.settings.general.WalletGeneralSettingsPresenter;
import com.worldventures.dreamtrips.wallet.ui.settings.security.WalletSecuritySettingsPresenter;

import rx.Observable;
import rx.functions.Action0;

public interface WalletFeatureHelper {

   void prepareSettingsScreen(WalletSettingsPresenter.Screen view);
   //SettingsGeneral
   void prepareSettingsGeneralScreen(WalletGeneralSettingsPresenter.Screen view);
   void openEditProfile(Context context, Action0 action);
   //SettingsSecurity
   void prepareSettingsSecurityScreen(WalletSecuritySettingsPresenter.Screen view);
   void openFindCard(Context context, Action0 action);
   //Dashboard
   void prepareDashboardScreen(CardListPresenter.Screen view);
   boolean addingCardIsNotSupported();
   boolean offlineModeState(boolean isOfflineMode);
   Observable<Void> onUserAssigned(SmartCardUser user);
   void onUserFetchedFromServer(SmartCardUser user);
   void navigateFromSetupUserScreen(Navigator navigator, SmartCardUser user, boolean withoutLast);
   boolean isSampleCardMode();
}
