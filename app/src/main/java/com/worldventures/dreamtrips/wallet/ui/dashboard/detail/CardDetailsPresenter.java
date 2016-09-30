package com.worldventures.dreamtrips.wallet.ui.dashboard.detail;

import android.content.Context;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.janet.composer.ActionPipeCacheWiper;
import com.worldventures.dreamtrips.core.utils.LocaleHelper;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsInteractor;
import com.worldventures.dreamtrips.wallet.analytics.CardDetailsAction;
import com.worldventures.dreamtrips.wallet.analytics.ChangeDefaultCardAction;
import com.worldventures.dreamtrips.wallet.analytics.PaycardAnalyticsCommand;
import com.worldventures.dreamtrips.wallet.analytics.WalletAnalyticsCommand;
import com.worldventures.dreamtrips.wallet.domain.entity.AddressInfoWithLocale;
import com.worldventures.dreamtrips.wallet.domain.entity.ImmutableAddressInfoWithLocale;
import com.worldventures.dreamtrips.wallet.domain.entity.card.BankCard;
import com.worldventures.dreamtrips.wallet.domain.entity.card.Card;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.service.command.FetchDefaultCardCommand;
import com.worldventures.dreamtrips.wallet.service.command.SetDefaultCardOnDeviceCommand;
import com.worldventures.dreamtrips.wallet.service.command.UpdateBankCardCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;
import com.worldventures.dreamtrips.wallet.ui.common.helper.OperationSubscriberWrapper;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;
import com.worldventures.dreamtrips.wallet.ui.wizard.edit_card.EditCardDetailsPath;
import com.worldventures.dreamtrips.wallet.util.BankCardHelper;
import com.worldventures.dreamtrips.wallet.util.CardUtils;

import javax.inject.Inject;

import io.techery.janet.Command;
import io.techery.janet.smartcard.action.records.DeleteRecordAction;
import rx.Observable;

import static java.lang.Integer.valueOf;

public class CardDetailsPresenter extends WalletPresenter<CardDetailsPresenter.Screen, Parcelable> {

   @Inject Navigator navigator;
   @Inject LocaleHelper localeHelper;
   @Inject SmartCardInteractor smartCardInteractor;
   @Inject AnalyticsInteractor analyticsInteractor;
   @Inject BankCardHelper bankCardHelper;

   private final BankCard bankCard;

   public CardDetailsPresenter(Context context, Injector injector, BankCard bankCard) {
      super(context, injector);
      this.bankCard = bankCard;
   }

   @Override
   public void onAttachedToWindow() {
      super.onAttachedToWindow();
      trackScreen();
      Screen view = getView();

      view.setTitle(bankCardHelper.financialServiceWithCardNumber(bankCard));
      view.showCardBankInfo(bankCardHelper, bankCard);
      view.showDefaultAddress(obtainAddressWithCountry());

      connectToDefaultCardPipe();
      connectToDeleteCardPipe();
      connectToSetDefaultCardIdPipe();
   }

   private void trackScreen() {
      analyticsInteractor.paycardAnalyticsCommandPipe()
            .send(new PaycardAnalyticsCommand(new CardDetailsAction(), bankCard));
   }

   private void connectToDefaultCardPipe() {
      smartCardInteractor.fetchDefaultCardCommandPipe()
            .createObservableResult(new FetchDefaultCardCommand())
            .map(Command::getResult)
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

   public void editAddress() {
      navigator.go(new EditCardDetailsPath(bankCard));
   }

   public void onDeleteCardConfirmed() {
      smartCardInteractor.deleteCardPipe().send(new DeleteRecordAction(valueOf(bankCard.id())));
   }

   public void onSetAsDefaultCard(boolean setDefaultCard) {
      smartCardInteractor.fetchDefaultCardCommandPipe()
            .createObservableResult(new FetchDefaultCardCommand())
            .compose(bindViewIoToMainComposer())
            .map(Command::getResult)
            .subscribe(defaultCard -> {
               if (setDefaultCard) {
                  if (CardUtils.isRealCard(defaultCard)) {
                     getView().showDefaultCardDialog(bankCardHelper.bankNameWithCardNumber(defaultCard));
                  } else {
                     trackSetAsDefault();
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
         trackSetAsDefault();
         smartCardInteractor.setDefaultCardOnDeviceCommandPipe().send(new SetDefaultCardOnDeviceCommand(bankCard.id()));
      }
   }

   private void trackSetAsDefault() {
      analyticsInteractor.walletAnalyticsCommandPipe()
            .send(new WalletAnalyticsCommand(ChangeDefaultCardAction.forBankCard(bankCard)));
   }

   public void goBack() {
      navigator.goBack();
   }

   void nicknameUpdated(String nickName) {
      smartCardInteractor.updateBankCardPipe().send(UpdateBankCardCommand.updateNickName(bankCard, nickName));
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
