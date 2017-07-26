package com.worldventures.dreamtrips.wallet.ui.records.swiping.impl;


import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsInteractor;
import com.worldventures.dreamtrips.wallet.analytics.ConnectFlyeToChargerAction;
import com.worldventures.dreamtrips.wallet.analytics.FailedToAddCardAction;
import com.worldventures.dreamtrips.wallet.analytics.WalletAnalyticsCommand;
import com.worldventures.dreamtrips.wallet.domain.entity.record.Record;
import com.worldventures.dreamtrips.wallet.service.RecordInteractor;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.service.WalletNetworkService;
import com.worldventures.dreamtrips.wallet.service.command.SmartCardUserCommand;
import com.worldventures.dreamtrips.wallet.service.command.http.CreateRecordCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenterImpl;
import com.worldventures.dreamtrips.wallet.ui.common.helper.ErrorActionStateSubscriberWrapper;
import com.worldventures.dreamtrips.wallet.ui.common.helper.ErrorHandler;
import com.worldventures.dreamtrips.wallet.ui.common.helper.ErrorHandlerFactory;
import com.worldventures.dreamtrips.wallet.ui.common.helper.OperationActionStateSubscriberWrapper;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;
import com.worldventures.dreamtrips.wallet.ui.records.model.RecordViewModel;
import com.worldventures.dreamtrips.wallet.ui.records.swiping.WizardChargingPresenter;
import com.worldventures.dreamtrips.wallet.ui.records.swiping.WizardChargingScreen;
import com.worldventures.dreamtrips.wallet.util.WalletRecordUtil;

import java.net.UnknownHostException;

import io.techery.janet.helper.ActionStateSubscriber;
import io.techery.janet.smartcard.action.charger.StartCardRecordingAction;
import io.techery.janet.smartcard.action.charger.StopCardRecordingAction;
import io.techery.janet.smartcard.event.CardSwipedEvent;
import io.techery.janet.smartcard.exception.NotConnectedException;

// TODO: 5/30/17 Create task and refactor both screen and presenter, it's ugly and error handling is a joke
public class WizardChargingPresenterImpl extends WalletPresenterImpl<WizardChargingScreen> implements WizardChargingPresenter {

   private final RecordInteractor recordInteractor;
   private final AnalyticsInteractor analyticsInteractor;
   private final ErrorHandlerFactory errorHandlerFactory;

   public WizardChargingPresenterImpl(Navigator navigator, SmartCardInteractor smartCardInteractor,
         WalletNetworkService networkService, RecordInteractor recordInteractor, AnalyticsInteractor analyticsInteractor,
         ErrorHandlerFactory errorHandlerFactory) {
      super(navigator, smartCardInteractor, networkService);
      this.recordInteractor = recordInteractor;
      this.analyticsInteractor = analyticsInteractor;
      this.errorHandlerFactory = errorHandlerFactory;
   }

   @Override
   public void attachView(WizardChargingScreen view) {
      super.attachView(view);
      trackScreen();
      fetchUserPhoto();
      observeCharger();
      observeBankCardCreation();
      //observeConnectionStatus();
      //was developed in scope of SMARTCARD-1516
      //commented due to bug SMARTCARD-1792,
      //TODO: uncomment by request in future
   }

   private void fetchUserPhoto() {
      getSmartCardInteractor().smartCardUserPipe()
            .createObservable(SmartCardUserCommand.fetch())
            .compose(bindViewIoToMainComposer())
            .subscribe(new ActionStateSubscriber<SmartCardUserCommand>()
                  .onSuccess(command -> getView().userPhoto(command.getResult().userPhoto()))
            );
   }

   private void observeCharger() {
      getSmartCardInteractor().startCardRecordingPipe()
            .createObservable(new StartCardRecordingAction())
            .compose(bindViewIoToMainComposer())
            .subscribe(ErrorActionStateSubscriberWrapper.<StartCardRecordingAction>forView(getView().provideOperationDelegate())
                  .onFail(createErrorHandlerBuilder(StartCardRecordingAction.class)
                        .handle(Throwable.class, action -> getView().trySwipeAgain())
                        .build())
                  .wrap());

      getSmartCardInteractor().cardSwipedEventPipe()
            .observeSuccess()
            .compose(bindViewIoToMainComposer())
            .subscribe(event -> {
               if (event.result == CardSwipedEvent.Result.ERROR) {
                  getView().showSwipeError();
               } else {
                  getView().showSwipeSuccess();
               }
            });

      getSmartCardInteractor().chargedEventPipe()
            .observeSuccess()
            .compose(bindViewIoToMainComposer())
            .take(1)
            .map(cardChargedEvent -> cardChargedEvent.record)
            .subscribe(this::cardSwiped, this::errorReceiveRecord);
   }

   private void errorReceiveRecord(Throwable throwable) {
      getView().trySwipeAgain();
   }

   private void observeBankCardCreation() {
      recordInteractor.bankCardPipe()
            .observe()
            .compose(bindViewIoToMainComposer())
            .subscribe(OperationActionStateSubscriberWrapper.<CreateRecordCommand>forView(getView().provideOperationDelegate())
                  .onFail(createErrorHandlerBuilder(CreateRecordCommand.class).build())
                  .onSuccess(command -> bankCardCreated(command.getResult()))
                  .wrap());
   }

   private <T> ErrorHandler.Builder<T> createErrorHandlerBuilder(Class<T> clazz) {
      return errorHandlerFactory.<T>builder()
            .handle(NotConnectedException.class, t -> {
               analyticsInteractor.walletAnalyticsCommandPipe()
                     .send(new WalletAnalyticsCommand(FailedToAddCardAction.noCardConnection()));
            }).handle(UnknownHostException.class, t -> {
               analyticsInteractor.walletAnalyticsCommandPipe()
                     .send(new WalletAnalyticsCommand(FailedToAddCardAction.noNetworkConnection()));
            });
   }

   private void trackScreen() {
      analyticsInteractor.walletAnalyticsCommandPipe()
            .send(new WalletAnalyticsCommand(new ConnectFlyeToChargerAction()));
   }

   @Override
   public void detachView(boolean retainInstance) {
      super.detachView(retainInstance);
      getSmartCardInteractor().stopCardRecordingPipe().send(new StopCardRecordingAction());
   }

   @Override
   public void goBack() {
      getNavigator().goBack();
   }

   private void cardSwiped(io.techery.janet.smartcard.model.Record card) {
      recordInteractor.bankCardPipe().send(new CreateRecordCommand(card));
   }

   private void bankCardCreated(Record record) {
      getNavigator().goAddCard(WalletRecordUtil.prepareRecordViewModel(record));
   }
}
