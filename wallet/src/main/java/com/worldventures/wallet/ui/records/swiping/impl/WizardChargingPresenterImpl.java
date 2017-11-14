package com.worldventures.wallet.ui.records.swiping.impl;


import com.worldventures.wallet.analytics.ConnectFlyeToChargerAction;
import com.worldventures.wallet.analytics.WalletAnalyticsCommand;
import com.worldventures.wallet.domain.entity.record.Record;
import com.worldventures.wallet.service.RecordInteractor;
import com.worldventures.wallet.service.SmartCardInteractor;
import com.worldventures.wallet.service.WalletAnalyticsInteractor;
import com.worldventures.wallet.service.command.SmartCardUserCommand;
import com.worldventures.wallet.service.command.http.CreateRecordCommand;
import com.worldventures.wallet.ui.common.base.WalletDeviceConnectionDelegate;
import com.worldventures.wallet.ui.common.base.WalletPresenterImpl;
import com.worldventures.wallet.ui.common.navigation.Navigator;
import com.worldventures.wallet.ui.records.swiping.WizardChargingPresenter;
import com.worldventures.wallet.ui.records.swiping.WizardChargingScreen;
import com.worldventures.wallet.util.WalletRecordUtil;

import io.techery.janet.helper.ActionStateSubscriber;
import io.techery.janet.operationsubscriber.OperationActionSubscriber;
import io.techery.janet.smartcard.action.charger.StartCardRecordingAction;
import io.techery.janet.smartcard.action.charger.StopCardRecordingAction;
import io.techery.janet.smartcard.event.CardSwipedEvent;
import rx.android.schedulers.AndroidSchedulers;

// TODO: 5/30/17 Create task and refactor both screen and presenter, it's ugly and error handling is a joke
public class WizardChargingPresenterImpl extends WalletPresenterImpl<WizardChargingScreen> implements WizardChargingPresenter {

   private final SmartCardInteractor smartCardInteractor;
   private final RecordInteractor recordInteractor;
   private final WalletAnalyticsInteractor analyticsInteractor;

   public WizardChargingPresenterImpl(Navigator navigator, WalletDeviceConnectionDelegate deviceConnectionDelegate,
         SmartCardInteractor smartCardInteractor, RecordInteractor recordInteractor,
         WalletAnalyticsInteractor analyticsInteractor) {
      super(navigator, deviceConnectionDelegate);
      this.smartCardInteractor = smartCardInteractor;
      this.recordInteractor = recordInteractor;
      this.analyticsInteractor = analyticsInteractor;
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
      smartCardInteractor.smartCardUserPipe()
            .createObservable(SmartCardUserCommand.fetch())
            .compose(getView().bindUntilDetach())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new ActionStateSubscriber<SmartCardUserCommand>()
                  .onSuccess(command -> getView().userPhoto(command.getResult().userPhoto()))
            );
   }

   private void observeCharger() {
      smartCardInteractor.startCardRecordingPipe()
            .createObservable(new StartCardRecordingAction())
            .compose(getView().bindUntilDetach())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(OperationActionSubscriber.forView(getView().provideOperationStartCardRecording()).create());

      smartCardInteractor.cardSwipedEventPipe()
            .observeSuccess()
            .compose(getView().bindUntilDetach())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(event -> {
               if (event.result == CardSwipedEvent.Result.ERROR) {
                  getView().showSwipeError();
               } else {
                  getView().showSwipeSuccess();
               }
            });

      smartCardInteractor.chargedEventPipe()
            .observeSuccess()
            .compose(getView().bindUntilDetach())
            .observeOn(AndroidSchedulers.mainThread())
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
            .compose(getView().bindUntilDetach())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(OperationActionSubscriber.forView(getView().provideOperationCreateRecord())
                  .onSuccess(command -> bankCardCreated(command.getResult()))
                  .create());
   }

   private void trackScreen() {
      analyticsInteractor.walletAnalyticsPipe()
            .send(new WalletAnalyticsCommand(new ConnectFlyeToChargerAction()));
   }

   @Override
   public void detachView(boolean retainInstance) {
      super.detachView(retainInstance);
      smartCardInteractor.stopCardRecordingPipe().send(new StopCardRecordingAction());
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
