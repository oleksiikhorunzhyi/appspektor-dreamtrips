package com.worldventures.dreamtrips.wallet.ui.settings.security.disabledefaultcard.impl;


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
import com.worldventures.dreamtrips.wallet.ui.common.helper.ErrorHandlerFactory;
import com.worldventures.dreamtrips.wallet.ui.common.helper.OperationActionStateSubscriberWrapper;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;
import com.worldventures.dreamtrips.wallet.ui.settings.common.model.SettingsRadioModel;
import com.worldventures.dreamtrips.wallet.ui.settings.common.provider.DisableDefaultCardItemProvider;
import com.worldventures.dreamtrips.wallet.ui.settings.security.disabledefaultcard.WalletDisableDefaultCardPresenter;
import com.worldventures.dreamtrips.wallet.ui.settings.security.disabledefaultcard.WalletDisableDefaultCardScreen;

public class WalletDisableDefaultCardPresenterImpl extends WalletPresenterImpl<WalletDisableDefaultCardScreen> implements WalletDisableDefaultCardPresenter {

   private final AnalyticsInteractor analyticsInteractor;
   private final ErrorHandlerFactory errorHandlerFactory;
   private final DisableDefaultCardItemProvider itemProvider;

   public WalletDisableDefaultCardPresenterImpl(Navigator navigator, SmartCardInteractor smartCardInteractor,
         WalletNetworkService networkService, AnalyticsInteractor analyticsInteractor, ErrorHandlerFactory errorHandlerFactory) {
      super(navigator, smartCardInteractor, networkService);
      this.analyticsInteractor = analyticsInteractor;
      this.errorHandlerFactory = errorHandlerFactory;
      this.itemProvider = new DisableDefaultCardItemProvider();
   }

   @Override
   public void attachView(WalletDisableDefaultCardScreen view) {
      super.attachView(view);
      getView().setItems(itemProvider.items());
      fetchSmartCard();
      observeDelayChange();
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
                     getView().setDelayChanged(true);
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

   @Override
   public void trackChangedDelay() {
      final SettingsRadioModel selectedDelay = itemProvider.item(getView().getSelectedPosition());
      trackDisableDelay(new DisableDefaultChangedAction(getView().getTextBySelectedModel(selectedDelay)));
   }

   private void trackDisableDelay(WalletAnalyticsAction disableCardDelayAction) {
      analyticsInteractor.walletAnalyticsCommandPipe()
            .send(new WalletAnalyticsCommand(disableCardDelayAction));
   }
}
