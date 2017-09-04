package com.worldventures.dreamtrips.wallet.ui.settings.general.newcard.detection.impl;


import com.worldventures.dreamtrips.core.utils.HttpErrorHandlingUtil;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsInteractor;
import com.worldventures.dreamtrips.wallet.analytics.WalletAnalyticsAction;
import com.worldventures.dreamtrips.wallet.analytics.WalletAnalyticsCommand;
import com.worldventures.dreamtrips.wallet.analytics.new_smartcard.ExistSmartCardAction;
import com.worldventures.dreamtrips.wallet.analytics.new_smartcard.ExistSmartCardDontHaveCardAction;
import com.worldventures.dreamtrips.wallet.analytics.new_smartcard.ExistSmartCardDontHaveCardContinueAction;
import com.worldventures.dreamtrips.wallet.analytics.new_smartcard.ExistSmartCardHaveCardAction;
import com.worldventures.dreamtrips.wallet.analytics.new_smartcard.ExistSmartCardNotConnectedAction;
import com.worldventures.dreamtrips.wallet.analytics.new_smartcard.UnAssignCardContinueAction;
import com.worldventures.dreamtrips.wallet.domain.entity.ConnectionStatus;
import com.worldventures.dreamtrips.wallet.service.FactoryResetInteractor;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.service.WalletNetworkService;
import com.worldventures.dreamtrips.wallet.service.command.ActiveSmartCardCommand;
import com.worldventures.dreamtrips.wallet.service.command.device.DeviceStateCommand;
import com.worldventures.dreamtrips.wallet.service.command.reset.ResetOptions;
import com.worldventures.dreamtrips.wallet.service.command.reset.WipeSmartCardDataCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenterImpl;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;
import com.worldventures.dreamtrips.wallet.ui.settings.general.newcard.detection.ExistingCardDetectPresenter;
import com.worldventures.dreamtrips.wallet.ui.settings.general.newcard.detection.ExistingCardDetectScreen;
import com.worldventures.dreamtrips.wallet.ui.settings.general.newcard.helper.CardIdUtil;
import com.worldventures.dreamtrips.wallet.ui.settings.general.reset.CheckPinDelegate;
import com.worldventures.dreamtrips.wallet.ui.settings.general.reset.FactoryResetAction;

import io.techery.janet.helper.ActionStateSubscriber;
import io.techery.janet.operationsubscriber.OperationActionSubscriber;

public class ExistingCardDetectPresenterImpl extends WalletPresenterImpl<ExistingCardDetectScreen> implements ExistingCardDetectPresenter {

   private final AnalyticsInteractor analyticsInteractor;
   private final CheckPinDelegate checkPinDelegate;
   private final HttpErrorHandlingUtil httpErrorHandlingUtil;

   public ExistingCardDetectPresenterImpl(Navigator navigator, SmartCardInteractor smartCardInteractor,
         WalletNetworkService networkService, AnalyticsInteractor analyticsInteractor,
         FactoryResetInteractor factoryResetInteractor, HttpErrorHandlingUtil httpErrorHandlingUtil) {
      super(navigator, smartCardInteractor, networkService);
      this.analyticsInteractor = analyticsInteractor;
      this.httpErrorHandlingUtil = httpErrorHandlingUtil;
      checkPinDelegate = new CheckPinDelegate(smartCardInteractor, factoryResetInteractor, analyticsInteractor,
            navigator, FactoryResetAction.NEW_CARD);
   }

   @Override
   public void attachView(ExistingCardDetectScreen view) {
      super.attachView(view);
      checkPinDelegate.observePinStatus(getView());
      observerSmartCardConnectedStatus();
      fetchSmartCardId();
   }

   private void fetchSmartCardId() {
      getSmartCardInteractor().activeSmartCardPipe()
            .createObservable(new ActiveSmartCardCommand())
            .compose(bindViewIoToMainComposer())
            .subscribe(new ActionStateSubscriber<ActiveSmartCardCommand>()
                  .onSuccess(command -> bindSmartCardId(command.getResult().smartCardId()))
            );
   }

   private void bindSmartCardId(String smartCardId) {
      getView().setSmartCardId(CardIdUtil.pushZeroToSmartCardId(smartCardId));
   }

   private void observerSmartCardConnectedStatus() {
      getSmartCardInteractor().deviceStatePipe()
            .createObservable(DeviceStateCommand.fetch())
            .compose(bindViewIoToMainComposer())
            .subscribe(OperationActionSubscriber.forView(getView().provideDeviceStateOperationView())
                  .onSuccess(command -> handleConnectedResult(command.getResult().connectionStatus()))
                  .create());
   }

   private void handleConnectedResult(ConnectionStatus connectionStatus) {
      if (connectionStatus.isConnected()) {
         sendAnalyticAction(new ExistSmartCardAction());
         getView().modeConnectedSmartCard();
      } else {
         sendAnalyticAction(new ExistSmartCardNotConnectedAction());
         getView().modeDisconnectedSmartCard();
      }
   }

   @Override
   public void unassignCard() {
      sendAnalyticAction(new UnAssignCardContinueAction());
      checkPinDelegate.getFactoryResetDelegate().setupDelegate(getView());
   }

   @Override
   public void prepareUnassignCard() {
      getSmartCardInteractor().activeSmartCardPipe()
            .createObservable(new ActiveSmartCardCommand())
            .compose(bindViewIoToMainComposer())
            .subscribe(OperationActionSubscriber.forView(getView().provideActiveSmartCardOperationView())
                  .onSuccess(command -> getView().showConfirmationUnassignDialog(command.getResult().smartCardId()))
                  .create());
   }

   @Override
   public void prepareUnassignCardOnBackend() {
      getSmartCardInteractor().activeSmartCardPipe()
            .createObservable(new ActiveSmartCardCommand())
            .compose(bindViewIoToMainComposer())
            .subscribe(OperationActionSubscriber.forView(getView().provideActiveSmartCardOperationView())
                  .onSuccess(command -> {
                     sendAnalyticAction(new ExistSmartCardDontHaveCardAction());
                     getView().showConfirmationUnassignOnBackend(command.getResult().smartCardId());
                  })
                  .create());
   }

   @Override
   public void unassignCardOnBackend() {
      getSmartCardInteractor().wipeSmartCardDataPipe()
            .createObservable(new WipeSmartCardDataCommand(ResetOptions.builder()
                  .wipePaymentCards(false)
                  .wipeUserSmartCardData(false)
                  .build()))
            .compose(bindViewIoToMainComposer())
            .subscribe(OperationActionSubscriber.forView(getView().provideWipeOperationView())
                  .onSuccess(activeSmartCardCommand -> {
                     sendAnalyticAction(new ExistSmartCardDontHaveCardContinueAction());
                     getNavigator().goUnassignSuccess();
                  })
                  .create());
   }

   @Override
   public HttpErrorHandlingUtil httpErrorHandlingUtil() {
      return httpErrorHandlingUtil;
   }

   public void goBack() {
      getNavigator().goBack();
   }

   @Override
   public void navigateToPowerOn() {
      sendAnalyticAction(new ExistSmartCardHaveCardAction());
      getNavigator().goNewCardPowerOn();
   }

   private void sendAnalyticAction(WalletAnalyticsAction action) {
      analyticsInteractor
            .walletAnalyticsCommandPipe()
            .send(new WalletAnalyticsCommand(action));
   }

   void retryFactoryReset() {
      checkPinDelegate.getFactoryResetDelegate().factoryReset();
   }
}
