package com.worldventures.dreamtrips.wallet.ui.records.swiping;

import android.content.Context;
import android.os.Parcelable;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsInteractor;
import com.worldventures.dreamtrips.wallet.analytics.ConnectFlyeToChargerAction;
import com.worldventures.dreamtrips.wallet.analytics.FailedToAddCardAction;
import com.worldventures.dreamtrips.wallet.analytics.WalletAnalyticsCommand;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardUser;
import com.worldventures.dreamtrips.wallet.domain.entity.record.Record;
import com.worldventures.dreamtrips.wallet.service.RecordInteractor;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.service.command.SmartCardUserCommand;
import com.worldventures.dreamtrips.wallet.service.command.http.CreateRecordCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;
import com.worldventures.dreamtrips.wallet.ui.common.helper.ErrorActionStateSubscriberWrapper;
import com.worldventures.dreamtrips.wallet.ui.common.helper.ErrorHandler;
import com.worldventures.dreamtrips.wallet.ui.common.helper.ErrorHandlerFactory;
import com.worldventures.dreamtrips.wallet.ui.common.helper.OperationActionStateSubscriberWrapper;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;
import com.worldventures.dreamtrips.wallet.ui.records.add.AddCardDetailsPath;

import java.net.UnknownHostException;

import javax.inject.Inject;

import io.techery.janet.helper.ActionStateSubscriber;
import io.techery.janet.smartcard.action.charger.StartCardRecordingAction;
import io.techery.janet.smartcard.action.charger.StopCardRecordingAction;
import io.techery.janet.smartcard.event.CardSwipedEvent;
import io.techery.janet.smartcard.exception.NotConnectedException;

// TODO: 5/30/17 Create task and refactor both screen and presenter, it's ugly and error handling is a joke
public class WizardChargingPresenter extends WalletPresenter<WizardChargingPresenter.Screen, Parcelable> {

   @Inject Navigator navigator;
   @Inject SmartCardInteractor smartCardInteractor;
   @Inject RecordInteractor recordInteractor;
   @Inject AnalyticsInteractor analyticsInteractor;
   @Inject ErrorHandlerFactory errorHandlerFactory;

   public WizardChargingPresenter(Context context, Injector injector) {
      super(context, injector);
   }

   @Override
   public void onAttachedToWindow() {
      super.onAttachedToWindow();
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
            .compose(bindViewIoToMainComposer())
            .subscribe(new ActionStateSubscriber<SmartCardUserCommand>()
                  .onSuccess(this::bindSmartCardUser)
            );
   }

   private void bindSmartCardUser(SmartCardUserCommand command) {
      final SmartCardUser smartCardUser = command.getResult();
      if (smartCardUser.userPhoto() != null) {
         getView().userPhoto(command.getResult().userPhoto().photoUrl());
      }
   }

   private void observeCharger() {
      smartCardInteractor.startCardRecordingPipe()
            .createObservable(new StartCardRecordingAction())
            .compose(bindViewIoToMainComposer())
            .subscribe(ErrorActionStateSubscriberWrapper.<StartCardRecordingAction>forView(getView().provideOperationDelegate())
                  .onFail(createErrorHandlerBuilder(StartCardRecordingAction.class)
                        .handle(Throwable.class, action -> getView().trySwipeAgain())
                        .build())
                  .wrap());

      smartCardInteractor.cardSwipedEventPipe()
            .observeSuccess()
            .compose(bindViewIoToMainComposer())
            .subscribe(event -> {
               if (event.result == CardSwipedEvent.Result.ERROR) {
                  getView().showSwipeError();
               } else {
                  getView().showSwipeSuccess();
               }
            });

      smartCardInteractor.chargedEventPipe()
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
   public void onDetachedFromWindow() {
      super.onDetachedFromWindow();
      smartCardInteractor.stopCardRecordingPipe().send(new StopCardRecordingAction());
   }

   public void goBack() {
      navigator.goBack();
   }

   private void cardSwiped(io.techery.janet.smartcard.model.Record card) {
      recordInteractor.bankCardPipe().send(new CreateRecordCommand(card));
   }

   private void bankCardCreated(Record record) {
      navigator.withoutLast(new AddCardDetailsPath(record));
   }

   public interface Screen extends WalletScreen {

      void showSwipeError();

      void trySwipeAgain();

      void showSwipeSuccess();

      void userPhoto(String photoUrl);
   }
}
