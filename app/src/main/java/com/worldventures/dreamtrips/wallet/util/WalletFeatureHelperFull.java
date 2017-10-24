package com.worldventures.dreamtrips.wallet.util;

import android.content.Context;

import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardUser;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;
import com.worldventures.dreamtrips.wallet.ui.dashboard.CardListPresenter;
import com.worldventures.dreamtrips.wallet.ui.settings.WalletSettingsPresenter;
import com.worldventures.dreamtrips.wallet.ui.settings.general.WalletGeneralSettingsPresenter;
import com.worldventures.dreamtrips.wallet.ui.settings.security.WalletSecuritySettingsPresenter;
import com.worldventures.dreamtrips.wallet.ui.wizard.pin.proposal.PinProposalAction;
import com.worldventures.dreamtrips.wallet.ui.wizard.pin.proposal.PinProposalPath;

import rx.Observable;
import rx.functions.Action0;

public class WalletFeatureHelperFull implements WalletFeatureHelper {

   @Override
   public void prepareSettingsScreen(WalletSettingsPresenter.Screen view) {
      // do nothing
   }

   @Override
   public void prepareSettingsGeneralScreen(WalletGeneralSettingsPresenter.Screen view) {
      // do nothing
   }

   @Override
   public void openEditProfile(Context context, Action0 action) {
      action.call();
   }

   @Override
   public void prepareSettingsSecurityScreen(WalletSecuritySettingsPresenter.Screen view) {
      // do nothing
   }

   @Override
   public void openFindCard(Context context, Action0 action) {
      action.call();
   }

   @Override
   public void prepareDashboardScreen(CardListPresenter.Screen view) {
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
   public void navigateFromSetupUserScreen(Navigator navigator, SmartCardUser user, boolean withoutLast) {
      if (withoutLast) {
         navigator.withoutLast(new PinProposalPath(PinProposalAction.WIZARD));
      } else {
         navigator.go(new PinProposalPath(PinProposalAction.WIZARD));
      }
   }

   @Override
   public boolean isSampleCardMode() {
      return false;
   }
}
