package com.worldventures.dreamtrips.wallet.ui.settings.security.disabledefaultcard.impl;


import android.content.Context;
import android.os.Bundle;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsInteractor;
import com.worldventures.dreamtrips.wallet.analytics.WalletAnalyticsAction;
import com.worldventures.dreamtrips.wallet.analytics.WalletAnalyticsCommand;
import com.worldventures.dreamtrips.wallet.analytics.settings.DisableDefaultAction;
import com.worldventures.dreamtrips.wallet.analytics.settings.DisableDefaultChangedAction;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.service.WalletNetworkService;
import com.worldventures.dreamtrips.wallet.service.command.SetDisableDefaultCardDelayCommand;
import com.worldventures.dreamtrips.wallet.service.command.device.DeviceStateCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenterImpl;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;
import com.worldventures.dreamtrips.wallet.ui.common.helper.ErrorHandlerFactory;
import com.worldventures.dreamtrips.wallet.ui.common.helper.OperationActionStateSubscriberWrapper;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.NavigatorConductor;
import com.worldventures.dreamtrips.wallet.ui.settings.common.model.SettingsRadioModel;
import com.worldventures.dreamtrips.wallet.ui.settings.common.provider.DisableDefaultCardItemProvider;
import com.worldventures.dreamtrips.wallet.ui.settings.security.disabledefaultcard.WalletDisableDefaultCardPresenter;
import com.worldventures.dreamtrips.wallet.ui.settings.security.disabledefaultcard.WalletDisableDefaultCardScreen;
import com.worldventures.dreamtrips.wallet.ui.settings.security.disabledefaultcard.WalletDisableDefaultCardState;

import java.util.List;

import javax.inject.Inject;

public class WalletDisableDefaultCardPresenterImpl extends WalletPresenterImpl<WalletDisableDefaultCardScreen> implements WalletDisableDefaultCardPresenter {

   private final AnalyticsInteractor analyticsInteractor;
   private final ErrorHandlerFactory errorHandlerFactory;
   private final DisableDefaultCardItemProvider itemProvider;

   private boolean delayWasChanged = false;

   public WalletDisableDefaultCardPresenterImpl(NavigatorConductor navigator, SmartCardInteractor smartCardInteractor,
         WalletNetworkService networkService, AnalyticsInteractor analyticsInteractor, ErrorHandlerFactory errorHandlerFactory) {
      super(navigator, smartCardInteractor, networkService);
      this.analyticsInteractor = analyticsInteractor;
      this.errorHandlerFactory = errorHandlerFactory;
      this.itemProvider = new DisableDefaultCardItemProvider();
   }

//   TODO : uncomment on implement
   // View State
//   @Override
//   public void onNewViewState() {
//      state = new WalletDisableDefaultCardState();
//   }
//
//   @Override
//   public void applyViewState() {
//      super.applyViewState();
//      delayWasChanged = state.delayWasChanged();
//   }
//
//   @Override
//   public void onSaveInstanceState(Bundle bundle) {
//      state.setDelayWasChanged(delayWasChanged);
//      super.onSaveInstanceState(bundle);
//   }


   @Override
   public void attachView(WalletDisableDefaultCardScreen view) {
      super.attachView(view);
      getView().setItems(itemProvider.items());
      fetchSmartCard();
      observeDelayChange();
   }

   @Override
   public void detachView(boolean retainInstance) {
      if (delayWasChanged) {
         //known problem: this action will be sent after action from onAttachView of next screen
         trackChangedDelay();
      }
      super.detachView(retainInstance);
   }

   @Override
   public void goBack() {
      getNavigator().goBack();
   }

   /**
    * @param delayMinutes 0 - never
    */
   @Override
   public void onTimeSelected(long delayMinutes) {
      getSmartCardInteractor().disableDefaultCardDelayPipe().send(new SetDisableDefaultCardDelayCommand(delayMinutes));
   }

   private void fetchSmartCard() {
      getSmartCardInteractor().deviceStatePipe()
            .createObservableResult(DeviceStateCommand.fetch())
            .compose(bindViewIoToMainComposer())
            .subscribe(command -> {
               bindToView(command.getResult().disableCardDelay());
               trackScreen();
            });
   }

   private void observeDelayChange() {
      getSmartCardInteractor().disableDefaultCardDelayPipe()
            .observe()
            .compose(bindViewIoToMainComposer())
            .subscribe(OperationActionStateSubscriberWrapper.<SetDisableDefaultCardDelayCommand>forView(getView().provideOperationDelegate())
                  .onSuccess(command -> {
                     bindToView(command.getResult());
                     delayWasChanged = true;
                  })
                  .onFail(errorHandlerFactory.errorHandler())
                  .wrap());
   }

   private void bindToView(long disableCardDelay) {
      final int position = itemProvider.getPositionForValue(disableCardDelay);
      getView().setSelectedPosition(position);
   }

   public void trackScreen() {
      final SettingsRadioModel selectedDelay = itemProvider.item(getView().getSelectedPosition());
      trackDisableDelay(new DisableDefaultAction(getView().getTextBySelectedModel(selectedDelay)));
   }

   public void trackChangedDelay() {
      final SettingsRadioModel selectedDelay = itemProvider.item(getView().getSelectedPosition());
      trackDisableDelay(new DisableDefaultChangedAction(getView().getTextBySelectedModel(selectedDelay)));
   }

   private void trackDisableDelay(WalletAnalyticsAction disableCardDelayAction) {
      analyticsInteractor.walletAnalyticsCommandPipe()
            .send(new WalletAnalyticsCommand(disableCardDelayAction));
   }
}
