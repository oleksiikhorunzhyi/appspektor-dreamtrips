package com.worldventures.dreamtrips.wallet.util;

import android.content.Context;

import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardUser;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.NavigatorConductor;
import com.worldventures.dreamtrips.wallet.ui.dashboard.CardListScreen;
import com.worldventures.dreamtrips.wallet.ui.settings.WalletSettingsScreen;
import com.worldventures.dreamtrips.wallet.ui.settings.general.WalletGeneralSettingsScreen;
import com.worldventures.dreamtrips.wallet.ui.settings.security.WalletSecuritySettingsScreen;
import com.worldventures.dreamtrips.wallet.ui.wizard.pin.proposal.PinProposalAction;

import rx.Observable;
import rx.functions.Action0;

public class WalletFeatureHelperFull implements WalletFeatureHelper {

   @Override
   public void prepareSettingsScreen(WalletSettingsScreen view) {
      // do nothing
   }

   @Override
   public void prepareSettingsGeneralScreen(WalletGeneralSettingsScreen view) {
      // do nothing
   }

   @Override
   public void openEditProfile(Context context, Action0 action) {
      action.call();
   }

   @Override
   public void prepareSettingsSecurityScreen(WalletSecuritySettingsScreen view) {
      // do nothing
   }

   @Override
   public void openFindCard(Context context, Action0 action) {
      action.call();
   }

   @Override
   public void prepareDashboardScreen(CardListScreen view) {
      // do nothing
   }

   @Override
   public boolean addingCardIsNotSupported() {
      return false;
   }

   @Override
   public boolean offlineModeState(boolean isOfflineMode) {
      return isOfflineMode;
   }

   @Override
   public Observable<Void> onUserAssigned(SmartCardUser user) {
      return Observable.just(null);
   }

   @Override
   public void onUserFetchedFromServer(SmartCardUser user) {
      // do nothing
   }

   @Override
   public void navigateFromSetupUserScreen(NavigatorConductor navigator) {
      navigator.goPinProposalUserSetup(PinProposalAction.WIZARD);
   }

   @Override
   public boolean isCardDetailSupported() {
      return true;
   }

   @Override
   public boolean isCardSyncSupported() {
      return true;
   }
}
