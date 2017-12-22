package com.worldventures.wallet.util;

import android.content.Context;

import com.worldventures.wallet.domain.entity.SmartCardUser;
import com.worldventures.wallet.service.SmartCardLocationInteractor;
import com.worldventures.wallet.service.lostcard.command.UpdateTrackingStatusCommand;
import com.worldventures.wallet.ui.common.navigation.Navigator;
import com.worldventures.wallet.ui.dashboard.CardListScreen;
import com.worldventures.wallet.ui.settings.WalletSettingsScreen;
import com.worldventures.wallet.ui.settings.general.WalletGeneralSettingsScreen;
import com.worldventures.wallet.ui.settings.security.WalletSecuritySettingsScreen;
import com.worldventures.wallet.ui.wizard.pin.proposal.PinProposalAction;

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
   public boolean isSampleCardMode() {
      return false;
   }

   @Override
   public void finishRegularProvisioning(Navigator navigator) {
      navigator.goPinProposalUserSetup(PinProposalAction.WIZARD);
   }

   @Override
   public boolean pinFunctionalityAvailable() {
      return true;
   }

   @Override
   public Observable<Void> clearSettings(SmartCardLocationInteractor interactor) {
      // todo skip it if setting is disabled
      return interactor.updateTrackingStatusPipe()
            .createObservableResult(new UpdateTrackingStatusCommand(false))
            .map(disassociateCardUserHttpAction -> (Void) null);
   }
}
