package com.worldventures.dreamtrips.wallet.ui.wizard.magstripe;

import android.content.Context;
import android.os.Parcelable;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.wallet.domain.entity.card.BankCard;
import com.worldventures.dreamtrips.wallet.domain.entity.card.BankCard.CardType;
import com.worldventures.dreamtrips.wallet.domain.entity.card.Card;
import com.worldventures.dreamtrips.wallet.domain.entity.card.ImmutableBankCard;
import com.worldventures.dreamtrips.wallet.service.MagstripeReaderInteractor;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;
import com.worldventures.dreamtrips.wallet.ui.common.helper.OperationSubscriberWrapper;
import com.worldventures.dreamtrips.wallet.ui.wizard.card_details.AddCardDetailsPath;

import javax.inject.Inject;

import flow.Flow;
import io.techery.janet.helper.ActionStateSubscriber;
import io.techery.janet.magstripe.SwipeData;
import io.techery.janet.magstripe.action.StartRecordingAction;
import io.techery.janet.magstripe.action.StopRecordingAction;
import io.techery.janet.magstripe.action.SwipeCardEventAction;
import io.techery.janet.smartcard.model.Record;

public class WizardMagstripePresenter extends WalletPresenter<WizardMagstripePresenter.Screen, Parcelable> {

   private final CardType cardType;

   @Inject MagstripeReaderInteractor interactor;

   public WizardMagstripePresenter(Context context, Injector injector, CardType cardType) {
      super(context, injector);
      this.cardType = cardType;
   }

   @Override
   public void onAttachedToWindow() {
      super.onAttachedToWindow();
      getView().showLabelsForCardType(cardType);

      interactor.startRecordingActionPipe()
            .createObservable(new StartRecordingAction())
            .compose(bindViewIoToMainComposer())
            .subscribe(new ActionStateSubscriber<StartRecordingAction>().onFail((action, throwable) -> {
               getView().provideOperationDelegate()
                     .showError(getContext().getString(R.string.error_something_went_wrong), o -> {});
            }));

      interactor.swipeCardEventActionPipe()
            .observe()
            .compose(bindViewIoToMainComposer())
            .subscribe(OperationSubscriberWrapper.<SwipeCardEventAction>forView(getView().provideOperationDelegate())
                  .onSuccess(action -> cardSwiped(action.data()))
                  .onFail(getContext().getString(R.string.wallet_wizard_magstripe_swipe_error)).wrap());
   }

   @Override
   public void onDetachedFromWindow() {
      super.onDetachedFromWindow();
      interactor.stopRecordingActionPipe()
            .send(new StopRecordingAction());
   }

   public void goBack() {
      Flow.get(getContext()).goBack();
   }

   public void cardSwiped(SwipeData swipeData) {
      //TODO: validate swipe data
      BankCard bankCard = ImmutableBankCard.builder()
            .id(Card.NO_ID)
            .number(Long.parseLong(swipeData.pan()))
            .type(Record.FinancialService.MASTERCARD)
            .cardType(cardType)
            .expiryYear(Integer.parseInt(swipeData.exp().substring(0, 2)))
            .expiryMonth(Integer.parseInt(swipeData.exp().substring(2, 4)))
            .build();
      Flow.get(getContext()).set(new AddCardDetailsPath(bankCard));
   }

   public interface Screen extends WalletScreen {
      void showLabelsForCardType(CardType cardType);
   }
}
