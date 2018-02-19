package com.worldventures.wallet.ui.settings.general.newcard.detection.impl;

import com.worldventures.wallet.analytics.WalletAnalyticsAction;
import com.worldventures.wallet.analytics.WalletAnalyticsCommand;
import com.worldventures.wallet.analytics.new_smartcard.ExistSmartCardAction;
import com.worldventures.wallet.analytics.new_smartcard.ExistSmartCardDontHaveCardAction;
import com.worldventures.wallet.analytics.new_smartcard.ExistSmartCardDontHaveCardContinueAction;
import com.worldventures.wallet.analytics.new_smartcard.ExistSmartCardHaveCardAction;
import com.worldventures.wallet.analytics.new_smartcard.ExistSmartCardNotConnectedAction;
import com.worldventures.wallet.analytics.new_smartcard.UnAssignCardContinueAction;
import com.worldventures.wallet.domain.entity.ConnectionStatus;
import com.worldventures.wallet.service.SmartCardInteractor;
import com.worldventures.wallet.service.WalletAnalyticsInteractor;
import com.worldventures.wallet.service.command.ActiveSmartCardCommand;
import com.worldventures.wallet.service.command.device.DeviceStateCommand;
import com.worldventures.wallet.service.command.reset.ResetOptions;
import com.worldventures.wallet.ui.common.base.WalletDeviceConnectionDelegate;
import com.worldventures.wallet.ui.common.base.WalletPresenterImpl;
import com.worldventures.wallet.ui.common.navigation.Navigator;
import com.worldventures.wallet.ui.settings.general.newcard.detection.ExistingCardDetectPresenter;
import com.worldventures.wallet.ui.settings.general.newcard.detection.ExistingCardDetectScreen;
import com.worldventures.wallet.ui.settings.general.reset.delegate.FactoryResetDelegate;

import rx.android.schedulers.AndroidSchedulers;
import timber.log.Timber;

public class ExistingCardDetectPresenterImpl extends WalletPresenterImpl<ExistingCardDetectScreen> implements ExistingCardDetectPresenter {

   private final SmartCardInteractor smartCardInteractor;
   private final WalletAnalyticsInteractor analyticsInteractor;
   private final FactoryResetDelegate factoryResetDelegate;

   public ExistingCardDetectPresenterImpl(
         Navigator navigator,
         WalletDeviceConnectionDelegate deviceConnectionDelegate,
         SmartCardInteractor smartCardInteractor,
         WalletAnalyticsInteractor analyticsInteractor,
         FactoryResetDelegate factoryResetDelegate) {
      super(navigator, deviceConnectionDelegate);
      this.smartCardInteractor = smartCardInteractor;
      this.analyticsInteractor = analyticsInteractor;
      this.factoryResetDelegate = factoryResetDelegate;
   }

   @Override
   public void attachView(ExistingCardDetectScreen view) {
      super.attachView(view);
      factoryResetDelegate.bindView(view);
   }

   @Override
   public void fetchSmartCardId() {
      smartCardInteractor.activeSmartCardPipe()
            .createObservableResult(new ActiveSmartCardCommand())
            .compose(getView().bindUntilDetach())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(command -> bindSmartCardId(command.getResult().getSmartCardId()), Timber::e);
   }

   @Override
   public void fetchSmartCardConnection() {
      smartCardInteractor.deviceStatePipe()
            .createObservableResult(DeviceStateCommand.Companion.fetch())
            .compose(getView().bindUntilDetach())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(command -> handleConnectedResult(command.getResult().getConnectionStatus()), Timber::e);
   }

   private void bindSmartCardId(String smartCardId) {
      getView().setSmartCardId(smartCardId);
   }

   private void handleConnectedResult(ConnectionStatus connectionStatus) {
      sendAnalyticAction(connectionStatus.isConnected() ? new ExistSmartCardAction() : new ExistSmartCardNotConnectedAction());
      getView().setSmartCardConnection(connectionStatus);
   }

   @Override
   public void unassignCardConfirmed(String smartCardId) {
      sendAnalyticAction(new UnAssignCardContinueAction());
      factoryResetDelegate.startRegularFactoryReset();
   }

   @Override
   public void unassignCard() {
      getView().showConfirmationUnassignDialog();
   }

   @Override
   public void unassignWithoutCard() {
      sendAnalyticAction(new ExistSmartCardDontHaveCardAction());
      getView().showConfirmationUnassignWhioutCard();
   }

   @Override
   public void unassignWithoutCardConfirmed(String smartCardId) {
      sendAnalyticAction(new ExistSmartCardDontHaveCardContinueAction());
      factoryResetDelegate.factoryReset(
            ResetOptions.builder()
                  .wipePaymentCards(false)
                  .withEnterPin(false)
                  .wipeUserSmartCardData(false)
                  .smartCardIsAvailable(false)
                  .build());
   }

   public void goBack() {
      getNavigator().goBack();
   }

   @Override
   public void cardAvailable() {
      sendAnalyticAction(new ExistSmartCardHaveCardAction());
      getNavigator().goNewCardPowerOn();
   }

   private void sendAnalyticAction(WalletAnalyticsAction action) {
      analyticsInteractor.walletAnalyticsPipe().send(new WalletAnalyticsCommand(action));
   }
}
