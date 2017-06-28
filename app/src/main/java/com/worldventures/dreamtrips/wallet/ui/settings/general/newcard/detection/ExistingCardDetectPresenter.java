package com.worldventures.dreamtrips.wallet.ui.settings.general.newcard.detection;

import android.content.Context;
import android.os.Parcelable;

import com.techery.spares.module.Injector;
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
import com.worldventures.dreamtrips.wallet.service.command.reset.ResetOptions;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.service.WalletBluetoothService;
import com.worldventures.dreamtrips.wallet.service.command.ActiveSmartCardCommand;
import com.worldventures.dreamtrips.wallet.service.command.device.DeviceStateCommand;
import com.worldventures.dreamtrips.wallet.service.command.reset.ResetOptions;
import com.worldventures.dreamtrips.wallet.service.command.reset.WipeSmartCardDataCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;
import com.worldventures.dreamtrips.wallet.ui.settings.general.newcard.helper.CardIdUtil;
import com.worldventures.dreamtrips.wallet.ui.settings.general.newcard.poweron.NewCardPowerOnPath;
import com.worldventures.dreamtrips.wallet.ui.settings.general.newcard.success.UnassignSuccessPath;
import com.worldventures.dreamtrips.wallet.ui.settings.general.reset.CheckPinDelegate;
import com.worldventures.dreamtrips.wallet.ui.settings.general.reset.FactoryResetAction;
import com.worldventures.dreamtrips.wallet.ui.settings.general.reset.FactoryResetView;

import javax.inject.Inject;

import io.techery.janet.helper.ActionStateSubscriber;
import io.techery.janet.operationsubscriber.OperationActionSubscriber;
import io.techery.janet.operationsubscriber.view.OperationView;

public class ExistingCardDetectPresenter extends WalletPresenter<ExistingCardDetectPresenter.Screen, Parcelable> {

   @Inject Navigator navigator;
   @Inject SmartCardInteractor smartCardInteractor;
   @Inject AnalyticsInteractor analyticsInteractor;
   @Inject FactoryResetInteractor factoryResetInteractor;
   @Inject WalletBluetoothService bluetoothService;
   private final CheckPinDelegate checkPinDelegate;

   public ExistingCardDetectPresenter(Context context, Injector injector) {
      super(context, injector);
      checkPinDelegate = new CheckPinDelegate(smartCardInteractor, factoryResetInteractor, analyticsInteractor,
            navigator, FactoryResetAction.NEW_CARD);
   }

   @Override
   public void onAttachedToWindow() {
      super.onAttachedToWindow();
      checkPinDelegate.observePinStatus(getView());
      observerSmartCardConnectedStatus();
      fetchSmartCardId();
   }

   private void fetchSmartCardId() {
      smartCardInteractor.activeSmartCardPipe()
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
      smartCardInteractor.deviceStatePipe()
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

   void unassignCard() {
      sendAnalyticAction(new UnAssignCardContinueAction());
      checkPinDelegate.getFactoryResetDelegate().setupDelegate(getView());
   }

   void prepareUnassignCard() {
      smartCardInteractor.activeSmartCardPipe()
            .createObservable(new ActiveSmartCardCommand())
            .compose(bindViewIoToMainComposer())
            .subscribe(OperationActionSubscriber.forView(getView().provideActiveSmartCardOperationView())
                  .onSuccess(command -> getView().showConfirmationUnassignDialog(command.getResult().smartCardId()))
                  .create());
   }

   void prepareUnassignCardOnBackend() {
      smartCardInteractor.activeSmartCardPipe()
            .createObservable(new ActiveSmartCardCommand())
            .compose(bindViewIoToMainComposer())
            .subscribe(OperationActionSubscriber.forView(getView().provideActiveSmartCardOperationView())
                  .onSuccess(command -> {
                     sendAnalyticAction(new ExistSmartCardDontHaveCardAction());
                     getView().showConfirmationUnassignOnBackend(command.getResult().smartCardId());
                  })
                  .create());
   }

   void unassignCardOnBackend() {
      smartCardInteractor.wipeSmartCardDataCommandActionPipe()
            .createObservable(new WipeSmartCardDataCommand(ResetOptions.builder()
                  .wipePaymentCards(false)
                  .wipeUserSmartCardData(false)
                  .build()))
            .compose(bindViewIoToMainComposer())
            .subscribe(OperationActionSubscriber.forView(getView().provideWipeOperationView())
                  .onSuccess(activeSmartCardCommand -> {
                     sendAnalyticAction(new ExistSmartCardDontHaveCardContinueAction());
                     navigator.single(new UnassignSuccessPath());
                  })
                  .create());
   }

   public void goBack() {
      navigator.goBack();
   }

   void navigateToPowerOn() {
      sendAnalyticAction(new ExistSmartCardHaveCardAction());
      navigator.go(new NewCardPowerOnPath());
   }

   private void sendAnalyticAction(WalletAnalyticsAction action) {
      analyticsInteractor
            .walletAnalyticsCommandPipe()
            .send(new WalletAnalyticsCommand(action));
   }

   void retryFactoryReset() {
      checkPinDelegate.getFactoryResetDelegate().factoryReset();
   }

   public interface Screen extends WalletScreen, FactoryResetView {

      OperationView<ActiveSmartCardCommand> provideActiveSmartCardOperationView();

      OperationView<DeviceStateCommand> provideDeviceStateOperationView();

      OperationView<WipeSmartCardDataCommand> provideWipeOperationView();

      void setSmartCardId(String scId);

      void modeConnectedSmartCard();

      void modeDisconnectedSmartCard();

      void showConfirmationUnassignDialog(String scId);

      void showConfirmationUnassignOnBackend(String scId);
   }
}
