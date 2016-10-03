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
import com.worldventures.dreamtrips.wallet.domain.entity.card.Card;
import com.worldventures.dreamtrips.wallet.domain.entity.card.ImmutableBankCard;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;
import com.worldventures.dreamtrips.wallet.ui.common.helper.ErrorActionStateSubscriberWrapper;
import com.worldventures.dreamtrips.wallet.ui.common.helper.ErrorHandler;
import com.worldventures.dreamtrips.wallet.ui.common.helper.OperationActionStateSubscriberWrapper;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;
import com.worldventures.dreamtrips.wallet.ui.wizard.card_details.AddCardDetailsPath;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import io.techery.janet.smartcard.action.charger.StartCardRecordingAction;
import io.techery.janet.smartcard.action.charger.StopCardRecordingAction;
import io.techery.janet.smartcard.event.CardChargedEvent;

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
      smartCardInteractor.startCardRecordingPipe()
            .createObservable(new StartCardRecordingAction())
            .compose(bindViewIoToMainComposer())
            .subscribe(ErrorActionStateSubscriberWrapper.<StartCardRecordingAction>forView(getView().provideOperationDelegate())
                  .onFail(ErrorHandler.create(getContext()))
                  .wrap());

      //TODO implement chain with FetchRecordIssuerInfoCommand, and differ `no card connection` and `no internet connection` errors in analytics
      smartCardInteractor.chargedEventPipe()
            .observe()
            .delay(2, TimeUnit.SECONDS) // // TODO: 9/16/16 for demo mock device
            .compose(bindViewIoToMainComposer())
            .compose(new ActionPipeCacheWiper<>(smartCardInteractor.chargedEventPipe()))
            .subscribe(OperationActionStateSubscriberWrapper.<CardChargedEvent>forView(getView().provideOperationDelegate())
                  .onSuccess(event -> cardSwiped(event.card))
                  .onFail(ErrorHandler.<CardChargedEvent>builder(getContext())
                        .defaultMessage(R.string.wallet_wizard_charging_swipe_error)
                        .defaultAction(cardChargedEvent -> analyticsInteractor.walletAnalyticsCommandPipe()
                              .send(new WalletAnalyticsCommand(FailedToAddCardAction.noCardConnection()))
                  ).build())
                  .wrap());
   }

   private void trackScreen() {
      analyticsInteractor.walletAnalyticsCommandPipe()
            .send(new WalletAnalyticsCommand(new ConnectFlyeToChargerAction()));
   }

   @Override
   public void onDetachedFromWindow() {
      super.onDetachedFromWindow();
      smartCardInteractor.stopCardRecordingPipe()
            .send(new StopCardRecordingAction());
   }

   public void goBack() {
      navigator.goBack();
   }

   public void cardSwiped(io.techery.janet.smartcard.model.Card card) {
      //TODO: validate swipe data
      BankCard bankCard = ImmutableBankCard.builder()
            .id(Card.NO_ID)
            .number(Long.parseLong(card.pan()))
            .expiryYear(Integer.parseInt(card.exp().substring(0, 2)))
            .expiryMonth(Integer.parseInt(card.exp().substring(2, 4)))
            .build();
      navigator.withoutLast(new AddCardDetailsPath(bankCard));
   }

   public interface Screen extends WalletScreen {
   }
}
