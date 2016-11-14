package com.worldventures.dreamtrips.wallet.ui.wizard.charging;

import android.content.Context;
import android.os.Parcelable;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.janet.composer.ActionPipeCacheWiper;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsInteractor;
import com.worldventures.dreamtrips.wallet.analytics.ConnectFlyeToChargerAction;
import com.worldventures.dreamtrips.wallet.analytics.FailedToAddCardAction;
import com.worldventures.dreamtrips.wallet.analytics.WalletAnalyticsCommand;
import com.worldventures.dreamtrips.wallet.domain.entity.card.BankCard;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.service.command.http.CreateBankCardCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;
import com.worldventures.dreamtrips.wallet.ui.common.helper.ErrorActionStateSubscriberWrapper;
import com.worldventures.dreamtrips.wallet.ui.common.helper.ErrorHandler;
import com.worldventures.dreamtrips.wallet.ui.common.helper.OperationActionStateSubscriberWrapper;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;
import com.worldventures.dreamtrips.wallet.ui.wizard.card_details.AddCardDetailsPath;

import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import io.techery.janet.smartcard.action.charger.StartCardRecordingAction;
import io.techery.janet.smartcard.action.charger.StopCardRecordingAction;
import io.techery.janet.smartcard.event.CardChargedEvent;
import io.techery.janet.smartcard.exception.NotConnectedException;
import io.techery.janet.smartcard.model.Card;

public class WizardChargingPresenter extends WalletPresenter<WizardChargingPresenter.Screen, Parcelable> {

   @Inject SmartCardInteractor smartCardInteractor;
   @Inject AnalyticsInteractor analyticsInteractor;
   @Inject Navigator navigator;

   public WizardChargingPresenter(Context context, Injector injector) {
      super(context, injector);
   }

   @Override
   public void onAttachedToWindow() {
      super.onAttachedToWindow();
      trackScreen();
      observeCharger();
      observeBankCardCreation();
   }

   private void observeCharger() {
      ErrorHandler<StartCardRecordingAction> cardRecordingErrorHandler = createErrorHandlerBuilder(StartCardRecordingAction.class).build();
      smartCardInteractor.startCardRecordingPipe()
            .createObservable(new StartCardRecordingAction())
            .compose(bindViewIoToMainComposer())
            .subscribe(ErrorActionStateSubscriberWrapper.<StartCardRecordingAction>forView(getView().provideOperationDelegate())
                  .onFail(cardRecordingErrorHandler)
                  .wrap());

      ErrorHandler<CardChargedEvent> chargedEventErrorHandler = createErrorHandlerBuilder(CardChargedEvent.class)
            .defaultMessage(R.string.wallet_wizard_charging_swipe_error)
            .build();
      smartCardInteractor.chargedEventPipe()
            .observe()
            .delay(2, TimeUnit.SECONDS) // // TODO: 9/16/16 for demo mock device
            .compose(bindViewIoToMainComposer())
            .compose(new ActionPipeCacheWiper<>(smartCardInteractor.chargedEventPipe()))
            .subscribe(OperationActionStateSubscriberWrapper.<CardChargedEvent>forView(getView().provideOperationDelegate())
                  .onSuccess(event -> cardSwiped(event.card))
                  .onFail(chargedEventErrorHandler)
                  .wrap());
   }

   private void observeBankCardCreation() {
      ErrorHandler<CreateBankCardCommand> errorHandler = createErrorHandlerBuilder(CreateBankCardCommand.class).build();
      smartCardInteractor.bankCardPipe()
            .observe()
            .compose(bindViewIoToMainComposer())
            .subscribe(OperationActionStateSubscriberWrapper.<CreateBankCardCommand>forView(getView().provideOperationDelegate())
                  .onFail(errorHandler)
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

   private void cardSwiped(Card card) {
      smartCardInteractor.bankCardPipe().send(new CreateBankCardCommand(card));
   }

   private void bankCardCreated(BankCard bankCard) {
      navigator.withoutLast(new AddCardDetailsPath(bankCard));
   }

   public interface Screen extends WalletScreen {
   }
}
