package com.worldventures.dreamtrips.wallet.ui.wizard.charging;

import android.content.Context;
import android.os.Parcelable;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.janet.composer.ActionPipeCacheWiper;
import com.worldventures.dreamtrips.wallet.domain.entity.card.BankCard;
import com.worldventures.dreamtrips.wallet.domain.entity.card.Card;
import com.worldventures.dreamtrips.wallet.domain.entity.card.ImmutableBankCard;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;
import com.worldventures.dreamtrips.wallet.ui.common.helper.OperationSubscriberWrapper;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;
import com.worldventures.dreamtrips.wallet.ui.wizard.card_details.AddCardDetailsPath;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import io.techery.janet.helper.ActionStateSubscriber;
import io.techery.janet.smartcard.action.charger.StartCardRecordingAction;
import io.techery.janet.smartcard.action.charger.StopCardRecordingAction;
import io.techery.janet.smartcard.event.CardChargedEvent;

public class WizardChargingPresenter extends WalletPresenter<WizardChargingPresenter.Screen, Parcelable> {

   @Inject SmartCardInteractor smartCardInteractor;
   @Inject Navigator navigator;

   public WizardChargingPresenter(Context context, Injector injector) {
      super(context, injector);
   }

   @Override
   public void onAttachedToWindow() {
      super.onAttachedToWindow();

      smartCardInteractor.startCardRecordingPipe()
            .createObservable(new StartCardRecordingAction())
            .compose(bindViewIoToMainComposer())
            .subscribe(new ActionStateSubscriber<StartCardRecordingAction>().onFail((action, throwable) -> {
               getView().provideOperationDelegate()
                     .showError(getContext().getString(R.string.error_something_went_wrong), o -> {});
            }));

      smartCardInteractor.chargedEventPipe()
            .observe()
            .delay(2, TimeUnit.SECONDS) // // TODO: 9/16/16 for demo mock device
            .compose(bindViewIoToMainComposer())
            .compose(new ActionPipeCacheWiper<>(smartCardInteractor.chargedEventPipe()))
            .subscribe(OperationSubscriberWrapper.<CardChargedEvent>forView(getView().provideOperationDelegate())
                  .onSuccess(event -> cardSwiped(event.card))
                  .onFail(getContext().getString(R.string.wallet_wizard_charging_swipe_error)).wrap());
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
