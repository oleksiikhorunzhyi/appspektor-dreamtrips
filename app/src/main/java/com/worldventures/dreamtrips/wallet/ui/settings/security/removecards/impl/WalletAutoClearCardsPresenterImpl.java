package com.worldventures.dreamtrips.wallet.ui.settings.security.removecards.impl;


import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsInteractor;
import com.worldventures.dreamtrips.wallet.analytics.WalletAnalyticsAction;
import com.worldventures.dreamtrips.wallet.analytics.WalletAnalyticsCommand;
import com.worldventures.dreamtrips.wallet.analytics.settings.AutoClearAction;
import com.worldventures.dreamtrips.wallet.analytics.settings.AutoClearChangedAction;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.service.WalletNetworkService;
import com.worldventures.dreamtrips.wallet.service.command.SetAutoClearSmartCardDelayCommand;
import com.worldventures.dreamtrips.wallet.service.command.device.DeviceStateCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenterImpl;
import com.worldventures.dreamtrips.wallet.ui.common.helper.ErrorHandlerFactory;
import com.worldventures.dreamtrips.wallet.ui.common.helper.OperationActionStateSubscriberWrapper;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.NavigatorConductor;
import com.worldventures.dreamtrips.wallet.ui.settings.common.model.SettingsRadioModel;
import com.worldventures.dreamtrips.wallet.ui.settings.common.provider.AutoClearSmartCardItemProvider;
import com.worldventures.dreamtrips.wallet.ui.settings.security.removecards.WalletAutoClearCardsPresenter;
import com.worldventures.dreamtrips.wallet.ui.settings.security.removecards.WalletAutoClearCardsScreen;

public class WalletAutoClearCardsPresenterImpl extends WalletPresenterImpl<WalletAutoClearCardsScreen> implements WalletAutoClearCardsPresenter {

   private final AnalyticsInteractor analyticsInteractor;
   private final ErrorHandlerFactory errorHandlerFactory;

   private boolean autoClearWasChanged = false;
   private final AutoClearSmartCardItemProvider itemProvider;

   public WalletAutoClearCardsPresenterImpl(NavigatorConductor navigator, SmartCardInteractor smartCardInteractor,
         WalletNetworkService networkService, AnalyticsInteractor analyticsInteractor, ErrorHandlerFactory errorHandlerFactory) {
      super(navigator, smartCardInteractor, networkService);
      this.analyticsInteractor = analyticsInteractor;
      this.errorHandlerFactory = errorHandlerFactory;
      this.itemProvider = new AutoClearSmartCardItemProvider();
   }


//   TODO : uncomment on implement
   // View State
//   @Override
//   public void onNewViewState() {
//      state = new WalletAutoClearCardsState();
//   }
//
//   @Override
//   public void applyViewState() {
//      super.applyViewState();
//      autoClearWasChanged = state.autoClearWasChanged();
//   }
//
//   @Override
//   public void onSaveInstanceState(Bundle bundle) {
//      state.setAutoClearWasChanged(autoClearWasChanged);
//      super.onSaveInstanceState(bundle);
//   }


   @Override
   public void attachView(WalletAutoClearCardsScreen view) {
      super.attachView(view);
      getView().setItems(itemProvider.items());
      observeSmartCard();
      observeDelayChange();
   }

   @Override
   public void detachView(boolean retainInstance) {
      if (autoClearWasChanged) {
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
      getSmartCardInteractor().autoClearDelayPipe().send(new SetAutoClearSmartCardDelayCommand(delayMinutes));
   }

   private void observeSmartCard() {
      getSmartCardInteractor().deviceStatePipe()
            .createObservableResult(DeviceStateCommand.fetch())
            .compose(bindViewIoToMainComposer())
            .subscribe(deviceStateCommand -> {
               bindToView(deviceStateCommand.getResult().clearFlyeDelay());
               trackScreen();
            });
   }

   private void observeDelayChange() {
      getSmartCardInteractor().autoClearDelayPipe()
            .observe()
            .compose(bindViewIoToMainComposer())
            .subscribe(OperationActionStateSubscriberWrapper.<SetAutoClearSmartCardDelayCommand>forView(getView().provideOperationDelegate())
                  .onSuccess(command -> {
                     bindToView(command.getResult());
                     autoClearWasChanged = true;
                  })
                  .onFail(errorHandlerFactory.errorHandler())
                  .wrap());
   }

   private void bindToView(long autoClearDelay) {
      getView().setSelectedPosition(itemProvider.getPositionForValue(autoClearDelay));
   }

   private void trackChangedDelay() {
      final SettingsRadioModel selectedDelay = itemProvider.item(getView().getSelectedPosition());
      trackAutoClear(new AutoClearChangedAction(getView().getTextBySelectedModel(selectedDelay)));
   }

   private void trackScreen() {
      final SettingsRadioModel selectedDelay = itemProvider.item(getView().getSelectedPosition());
      trackAutoClear(new AutoClearAction(getView().getTextBySelectedModel(selectedDelay)));
   }

   private void trackAutoClear(WalletAnalyticsAction autoClearAction) {
      analyticsInteractor.walletAnalyticsCommandPipe()
            .send(new WalletAnalyticsCommand(autoClearAction));
   }
}
