package com.worldventures.dreamtrips.wallet.ui.dashboard.detail;

import android.content.Context;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.janet.composer.ActionPipeCacheWiper;
import com.worldventures.dreamtrips.core.utils.LocaleHelper;
import com.worldventures.dreamtrips.wallet.domain.entity.AddressInfoWithLocale;
import com.worldventures.dreamtrips.wallet.domain.entity.ImmutableAddressInfoWithLocale;
import com.worldventures.dreamtrips.wallet.domain.entity.card.BankCard;
import com.worldventures.dreamtrips.wallet.domain.entity.card.Card;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.service.command.FetchDefaultCardCommand;
import com.worldventures.dreamtrips.wallet.service.command.SetDefaultCardOnDeviceCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;
import com.worldventures.dreamtrips.wallet.ui.common.helper.OperationSubscriberWrapper;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;
import com.worldventures.dreamtrips.wallet.util.BankCardHelper;
import com.worldventures.dreamtrips.wallet.util.CardUtils;

import javax.inject.Inject;

import io.techery.janet.smartcard.action.records.DeleteRecordAction;
import rx.Observable;

import static java.lang.Integer.valueOf;

public class CardDetailsPresenter extends WalletPresenter<CardDetailsPresenter.Screen, Parcelable> {

   @Inject Navigator navigator;
   @Inject LocaleHelper localeHelper;
   @Inject SmartCardInteractor smartCardInteractor;
   @Inject BankCardHelper bankCardHelper;

   private final BankCard bankCard;

   public CardDetailsPresenter(Context context, Injector injector, BankCard bankCard) {
      super(context, injector);
      this.bankCard = bankCard;
   }

   @Override
   public void onAttachedToWindow() {
      super.onAttachedToWindow();

      Screen view = getView();

      view.setTitle(bankCardHelper.financialServiceWithCardNumber(bankCard));
      view.showCardBankInfo(bankCardHelper, bankCard);
      view.showDefaultAddress(obtainAddressWithCountry());

      connectToDefaultCardPipe();
      connectToDeleteCardPipe();
      connectToSetDefaultCardIdPipe();
   }

   private void connectToDefaultCardPipe() {
      smartCardInteractor.fetchDefaultCardCommandPipe()
            .createObservableResult(new FetchDefaultCardCommand())
            .map(command -> command.getResult())
            .compose(bindViewIoToMainComposer())
            .subscribe(defaultBankCard -> {
               getView().setDefaultCardCondition(CardUtils.equals(defaultBankCard, bankCard));
               getView().setAsDefaultPaymentCardCondition().compose(bindView()).subscribe(this::onSetAsDefaultCard);
            });
   }

   private void connectToDeleteCardPipe() {
      smartCardInteractor.deleteCardPipe()
            .observeWithReplay()
            .filter(state -> valueOf(bankCard.id()).equals(state.action.recordId))
            .compose(bindViewIoToMainComposer())
            .compose(new ActionPipeCacheWiper<>(smartCardInteractor.deleteCardPipe()))
            .subscribe(OperationSubscriberWrapper.<DeleteRecordAction>forView(getView().provideOperationDelegate())
                  .onStart(getContext().getString(R.string.wallet_card_details_progress_delete, bankCard.title()))
                  .onSuccess(deleteRecordAction -> navigator.goBack())
                  .onFail(getContext().getString(R.string.error_something_went_wrong))
                  .wrap());
   }

   private void connectToSetDefaultCardIdPipe() {
      smartCardInteractor.setDefaultCardOnDeviceCommandPipe().clearReplays();
      smartCardInteractor.setDefaultCardOnDeviceCommandPipe()
            .observeWithReplay()
            .compose(bindViewIoToMainComposer())
            .compose(new ActionPipeCacheWiper<>(smartCardInteractor.setDefaultCardOnDeviceCommandPipe()))
            .subscribe(OperationSubscriberWrapper.<SetDefaultCardOnDeviceCommand>forView(getView().provideOperationDelegate())
                  .onStart("")
                  .onSuccess(action -> {
                  })
                  .onFail(getContext().getString(R.string.error_something_went_wrong))
                  .wrap());
   }

   private AddressInfoWithLocale obtainAddressWithCountry() {
      return ImmutableAddressInfoWithLocale.builder()
            .addressInfo(bankCard.addressInfo())
            .locale(localeHelper.getDefaultLocale())
            .build();
   }

   public void onDeleteCardClick() {
      getView().showDeleteCardDialog();
   }

   public void onDeleteCardConfirmed() {
      smartCardInteractor.deleteCardPipe().send(new DeleteRecordAction(valueOf(bankCard.id())));
   }

   public void onSetAsDefaultCard(boolean setDefaultCard) {
      smartCardInteractor.fetchDefaultCardCommandPipe()
            .createObservableResult(new FetchDefaultCardCommand())
            .compose(bindViewIoToMainComposer())
            .map(command -> command.getResult())
            .subscribe(defaultCard -> {
               if (setDefaultCard) {
                  if (CardUtils.isRealCard(defaultCard)) {
                     getView().showDefaultCardDialog(bankCardHelper.bankNameWithCardNumber(defaultCard));
                  } else {
                     smartCardInteractor.setDefaultCardOnDeviceCommandPipe()
                           .send(new SetDefaultCardOnDeviceCommand(bankCard.id()));
                  }
               } else {
                  if (CardUtils.equals(defaultCard, bankCard)) {
                     smartCardInteractor.setDefaultCardOnDeviceCommandPipe()
                           .send(new SetDefaultCardOnDeviceCommand(Card.NO_ID));
                  }
               }
            });
   }

   public void defaultCardDialogConfirmed(boolean confirmed) {
      if (!confirmed) {
         getView().setDefaultCardCondition(false);
      } else {
         smartCardInteractor.setDefaultCardOnDeviceCommandPipe().send(new SetDefaultCardOnDeviceCommand(bankCard.id()));
      }
   }

   public void goBack() {
      navigator.goBack();
   }

   public interface Screen extends WalletScreen {
      void setTitle(String title);

      void showCardBankInfo(BankCardHelper bankCardHelper, BankCard bankCard);

      void showDefaultAddress(AddressInfoWithLocale addressInfoWithLocale);

      void showDefaultCardDialog(@NonNull String bankCardName);

      void showDeleteCardDialog();

      void setDefaultCardCondition(boolean defaultCard);

      Observable<Boolean> setAsDefaultPaymentCardCondition();
   }

}