package com.worldventures.dreamtrips.wallet.ui.records.swiping;

import android.content.Context;
import android.os.Parcelable;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsInteractor;
import com.worldventures.dreamtrips.wallet.analytics.ConnectFlyeToChargerAction;
import com.worldventures.dreamtrips.wallet.analytics.FailedToAddCardAction;
import com.worldventures.dreamtrips.wallet.analytics.WalletAnalyticsCommand;
import com.worldventures.dreamtrips.wallet.domain.entity.ConnectionStatus;
import com.worldventures.dreamtrips.wallet.domain.entity.card.BankCard;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.service.command.SmartCardUserCommand;
import com.worldventures.dreamtrips.wallet.service.command.device.DeviceStateCommand;
import com.worldventures.dreamtrips.wallet.service.command.http.CreateBankCardCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;
import com.worldventures.dreamtrips.wallet.ui.common.helper.ErrorActionStateSubscriberWrapper;
import com.worldventures.dreamtrips.wallet.ui.common.helper.ErrorHandler;
import com.worldventures.dreamtrips.wallet.ui.common.helper.OperationActionStateSubscriberWrapper;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;
import com.worldventures.dreamtrips.wallet.ui.records.add.AddCardDetailsPath;
import com.worldventures.dreamtrips.wallet.ui.records.connectionerror.ConnectionErrorPath;

import java.io.File;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import io.techery.janet.helper.ActionStateSubscriber;
import io.techery.janet.smartcard.action.charger.StartCardRecordingAction;
import io.techery.janet.smartcard.action.charger.StopCardRecordingAction;
import io.techery.janet.smartcard.event.CardSwipedEvent;
import io.techery.janet.smartcard.exception.NotConnectedException;
import io.techery.janet.smartcard.model.Record;

public class WizardChargingPresenter extends WalletPresenter<WizardChargingPresenter.Screen, Parcelable> {

   @Inject Navigator navigator;
   @Inject SmartCardInteractor smartCardInteractor;
   @Inject AnalyticsInteractor analyticsInteractor;

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
                  .onSuccess(command -> getView().userPhoto(command.getResult().userPhoto().monochrome()))
            );
   }

   private void observeConnectionStatus() {
      smartCardInteractor.deviceStatePipe()
            .observeSuccessWithReplay()
            .throttleLast(1, TimeUnit.SECONDS)
            .map(command -> command.getResult().connectionStatus())
            .distinctUntilChanged()
            .compose(bindViewIoToMainComposer())
            .subscribe(getView()::checkConnection);

      smartCardInteractor.deviceStatePipe().send(DeviceStateCommand.fetch());
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
      smartCardInteractor.bankCardPipe()
            .observe()
            .compose(bindViewIoToMainComposer())
            .subscribe(OperationActionStateSubscriberWrapper.<CreateBankCardCommand>forView(getView().provideOperationDelegate())
                  .onFail(createErrorHandlerBuilder(CreateBankCardCommand.class).build())
                  .onSuccess(command -> bankCardCreated(command.getResult()))
                  .wrap());
   }

   private <T> ErrorHandler.Builder<T> createErrorHandlerBuilder(Class<T> clazz) {
      return ErrorHandler.<T>builder(getContext())
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

   private void cardSwiped(Record card) {
      smartCardInteractor.bankCardPipe().send(new CreateBankCardCommand(card));
   }

   private void bankCardCreated(BankCard bankCard) {
      navigator.withoutLast(new AddCardDetailsPath(bankCard));
   }

   public void showConnectionErrorScreen() {
      navigator.withoutLast(new ConnectionErrorPath());
   }

   public interface Screen extends WalletScreen {

      void checkConnection(ConnectionStatus connectionStatus);

      void showSwipeError();

      void trySwipeAgain();

      void showSwipeSuccess();

      void userPhoto(File file);
   }
}
