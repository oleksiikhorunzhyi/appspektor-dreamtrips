package com.worldventures.dreamtrips.wallet.ui.wizard.card_details;

import android.content.Context;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.janet.composer.ActionPipeCacheWiper;
import com.worldventures.dreamtrips.core.utils.LocaleHelper;
import com.worldventures.dreamtrips.wallet.domain.entity.AddressInfo;
import com.worldventures.dreamtrips.wallet.domain.entity.AddressInfoWithLocale;
import com.worldventures.dreamtrips.wallet.domain.entity.ImmutableAddressInfoWithLocale;
import com.worldventures.dreamtrips.wallet.domain.entity.ImmutableRecordIssuerInfo;
import com.worldventures.dreamtrips.wallet.domain.entity.RecordIssuerInfo;
import com.worldventures.dreamtrips.wallet.domain.entity.card.BankCard;
import com.worldventures.dreamtrips.wallet.domain.entity.card.ImmutableBankCard;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.service.command.FetchDefaultCardCommand;
import com.worldventures.dreamtrips.wallet.service.command.GetDefaultAddressCommand;
import com.worldventures.dreamtrips.wallet.service.command.SaveCardDetailsDataCommand;
import com.worldventures.dreamtrips.wallet.service.command.http.FetchRecordIssuerInfoCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;
import com.worldventures.dreamtrips.wallet.ui.common.helper.OperationSubscriberWrapper;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;
import com.worldventures.dreamtrips.wallet.ui.dashboard.list.CardListPath;
import com.worldventures.dreamtrips.wallet.util.BankCardHelper;
import com.worldventures.dreamtrips.wallet.util.CardUtils;
import com.worldventures.dreamtrips.wallet.util.FormatException;
import com.worldventures.dreamtrips.wallet.util.SmartCardInteractorHelper;

import javax.inject.Inject;

import flow.Flow.Direction;
import io.techery.janet.Command;
import io.techery.janet.helper.ActionStateToActionTransformer;
import rx.Observable;
import timber.log.Timber;

public class AddCardDetailsPresenter extends WalletPresenter<AddCardDetailsPresenter.Screen, Parcelable> {

   @Inject Navigator navigator;
   @Inject LocaleHelper localeHelper;
   @Inject BankCardHelper bankCardHelper;
   @Inject SmartCardInteractor smartCardInteractor;
   @Inject SmartCardInteractorHelper smartCardInteractorHelper;

   private final BankCard bankCard;
   private RecordIssuerInfo issuerInfo;

   public AddCardDetailsPresenter(Context context, Injector injector, BankCard bankCard) {
      super(context, injector);

      this.bankCard = bankCard;
   }

   @Override
   public void attachView(Screen view) {
      super.attachView(view);

      getView().cardBankInfo(bankCardHelper, bankCard);
   }

   @Override
   public void onAttachedToWindow() {
      super.onAttachedToWindow();
      connectToDefaultCardPipe();
      connectToRecordIssuerInfoPipe();
      connectToDefaultAddressPipe();
      connectToSaveCardDetailsPipe();
      loadDataFromDevice();
   }

   private void connectToDefaultCardPipe() {
      smartCardInteractor.fetchDefaultCardCommandPipe().clearReplays();
      smartCardInteractorHelper.sendSingleDefaultCardTask(bankCard -> {
         getView().defaultPaymentCard(!CardUtils.isRealCard(bankCard));
         getView().setAsDefaultPaymentCardCondition().compose(bindView()).subscribe(this::onSetAsDefaultCard);
      }, bindViewIoToMainComposer());
   }

   private void connectToRecordIssuerInfoPipe() {
      smartCardInteractor.recordIssuerInfoPipe()
            .createObservableResult(new FetchRecordIssuerInfoCommand(bankCardHelper.obtainIin(bankCard.number())))
            .compose(bindViewIoToMainComposer())
            .map(Command::getResult)
            .subscribe(it -> {
               issuerInfo = it;
               getView().cardBankInfo(bankCardHelper, ImmutableBankCard.copyOf(bankCard)
                     .withIssuerInfo(ImmutableRecordIssuerInfo.builder()
                           .bankName(it.bankName())
                           .cardType(it.cardType())
                           .financialService(it.financialService())
                           .build())
               );
            }, throwable -> {
               Timber.e("", throwable);
            });
   }

   private void connectToDefaultAddressPipe() {
      smartCardInteractor.getDefaultAddressCommandPipe()
            .observeWithReplay()
            .compose(new ActionStateToActionTransformer<>())
            .map(command -> {
               AddressInfo addressInfo = command.getResult();
               if (addressInfo == null) return null;

               return ImmutableAddressInfoWithLocale.builder()
                     .addressInfo(addressInfo)
                     .locale(localeHelper.getDefaultLocale())
                     .build();
            })
            .compose(bindViewIoToMainComposer())
            .subscribe(this::setDefaultAddress, throwable -> {
               Timber.e(throwable, "Fail to use GetDefaultAddressCommand");
               // TODO: 8/24/16 add error handling
            });
   }

   private void setDefaultAddress(@Nullable AddressInfoWithLocale defaultAddress) {
      if (defaultAddress == null) return;
      getView().defaultAddress(defaultAddress);
   }

   private void connectToSaveCardDetailsPipe() {
      smartCardInteractor.saveCardDetailsDataPipe()
            .observeWithReplay()
            .compose(bindViewIoToMainComposer())
            .compose(new ActionPipeCacheWiper<>(smartCardInteractor.saveCardDetailsDataPipe()))
            .subscribe(OperationSubscriberWrapper.<SaveCardDetailsDataCommand>forView(getView().provideOperationDelegate())
                  .onSuccess(saveCardDetailsDataCommand ->
                        navigator.single(new CardListPath(), Direction.REPLACE))
                  .onFail(throwable -> {
                     Context context = getContext();
                     String msg = throwable.getCause() instanceof FormatException ? context.getString(R.string.wallet_add_card_details_error_message) : context
                           .getString(R.string.error_something_went_wrong);

                     return new OperationSubscriberWrapper.MessageActionHolder<>(msg, null);
                  })
                  .wrap());
   }

   private void loadDataFromDevice() {
      smartCardInteractor.fetchDefaultCardCommandPipe().send(new FetchDefaultCardCommand());
      smartCardInteractor.getDefaultAddressCommandPipe().send(new GetDefaultAddressCommand());
   }

   public void onCardInfoConfirmed(AddressInfo addressInfo, String cvv, String nickname, boolean useDefaultAddress, boolean setAsDefaultAddress, boolean setAsDefaultCard) {
      smartCardInteractor.saveCardDetailsDataPipe()
            .send(new SaveCardDetailsDataCommand.Builder().setBankCard(bankCard)
                  .setManualAddressInfo(addressInfo)
                  .setNickName(nickname)
                  .setCvv(cvv)
                  .setIssuerInfo(issuerInfo)
                  .setUseDefaultAddress(useDefaultAddress)
                  .setSetAsDefaultAddress(setAsDefaultAddress)
                  .setSetAsDefaultCard(setAsDefaultCard)
                  .create());
   }

   private void onSetAsDefaultCard(boolean setDefaultCard) {
      if (!setDefaultCard) return;

      smartCardInteractorHelper.sendSingleDefaultCardTask(defaultCard -> {
         if (!CardUtils.isRealCard(defaultCard)) return;
         getView().showChangeCardDialog(bankCardHelper.bankNameWithCardNumber(defaultCard));
      }, bindViewIoToMainComposer());
   }

   public void defaultCardDialogConfirmed(boolean confirmed) {
      if (!confirmed) getView().defaultPaymentCard(false);
   }

   public void goBack() {
      navigator.goBack();
   }

   public interface Screen extends WalletScreen {

      void cardBankInfo(BankCardHelper cardHelper, BankCard bankCard);

      void defaultAddress(AddressInfoWithLocale defaultAddress);

      void defaultPaymentCard(boolean defaultPaymentCard);

      void showChangeCardDialog(@NonNull String bankCardName);

      Observable<Boolean> setAsDefaultPaymentCardCondition();
   }

}
