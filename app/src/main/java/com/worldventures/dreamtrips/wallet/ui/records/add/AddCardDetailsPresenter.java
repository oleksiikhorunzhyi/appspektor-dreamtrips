package com.worldventures.dreamtrips.wallet.ui.records.add;

import android.content.Context;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.janet.composer.ActionPipeCacheWiper;
import com.worldventures.dreamtrips.core.utils.LocaleHelper;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsInteractor;
import com.worldventures.dreamtrips.wallet.analytics.AddCardDetailsAction;
import com.worldventures.dreamtrips.wallet.analytics.CardDetailsOptionsAction;
import com.worldventures.dreamtrips.wallet.analytics.SetDefaultCardAction;
import com.worldventures.dreamtrips.wallet.analytics.WalletAnalyticsCommand;
import com.worldventures.dreamtrips.wallet.domain.entity.AddressInfo;
import com.worldventures.dreamtrips.wallet.domain.entity.AddressInfoWithLocale;
import com.worldventures.dreamtrips.wallet.domain.entity.ImmutableAddressInfoWithLocale;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCard;
import com.worldventures.dreamtrips.wallet.domain.entity.card.BankCard;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.service.command.AddBankCardCommand;
import com.worldventures.dreamtrips.wallet.service.command.FetchDefaultCardCommand;
import com.worldventures.dreamtrips.wallet.service.command.GetDefaultAddressCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;
import com.worldventures.dreamtrips.wallet.ui.common.helper.ErrorHandler;
import com.worldventures.dreamtrips.wallet.ui.common.helper.ErrorSubscriberWrapper;
import com.worldventures.dreamtrips.wallet.ui.common.helper.OperationActionStateSubscriberWrapper;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;
import com.worldventures.dreamtrips.wallet.ui.dashboard.CardListPath;
import com.worldventures.dreamtrips.wallet.util.BankCardHelper;
import com.worldventures.dreamtrips.wallet.util.CardUtils;
import com.worldventures.dreamtrips.wallet.util.FormatException;
import com.worldventures.dreamtrips.wallet.util.SmartCardInteractorHelper;

import javax.inject.Inject;

import flow.Flow.Direction;
import io.techery.janet.helper.ActionStateToActionTransformer;
import rx.Observable;
import rx.functions.Action1;

public class AddCardDetailsPresenter extends WalletPresenter<AddCardDetailsPresenter.Screen, Parcelable> {

   @Inject Navigator navigator;
   @Inject LocaleHelper localeHelper;
   @Inject BankCardHelper bankCardHelper;
   @Inject SmartCardInteractor smartCardInteractor;
   @Inject AnalyticsInteractor analyticsInteractor;
   @Inject SmartCardInteractorHelper smartCardInteractorHelper;

   private final BankCard bankCard;

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
      trackScreen();
      connectToDefaultCardPipe();
      connectToDefaultAddressPipe();
      connectToSaveCardDetailsPipe();
      loadDataFromDevice();
   }

   private void trackScreen() {
      smartCardInteractor.smartCardModifierPipe()
            .observeSuccessWithReplay()
            .take(1)
            .subscribe(command -> {
               analyticsInteractor.walletAnalyticsCommandPipe()
                     .send(new WalletAnalyticsCommand(AddCardDetailsAction.forBankCard(bankCard,
                           command.getResult().connectionStatus() == SmartCard.ConnectionStatus.CONNECTED)));
            });
   }

   private void connectToDefaultCardPipe() {
      smartCardInteractor.fetchDefaultCardCommandPipe().clearReplays();
      smartCardInteractorHelper.sendSingleDefaultCardTask(bankCard -> {
         getView().defaultPaymentCard(!CardUtils.isRealCard(bankCard));
         getView().setAsDefaultPaymentCardCondition().compose(bindView()).subscribe(this::onSetAsDefaultCard);
      }, bindViewIoToMainComposer());
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
            .subscribe(ErrorSubscriberWrapper.<AddressInfoWithLocale>forView(getView().provideOperationDelegate())
                  .onNext(new Action1<AddressInfoWithLocale>() {
                     @Override
                     public void call(AddressInfoWithLocale defaultAddress) {
                        setDefaultAddress(defaultAddress);
                     }
                  })
                  .onFail(ErrorHandler.create(getContext()))
                  .wrap()
            );
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
            .subscribe(OperationActionStateSubscriberWrapper.<AddBankCardCommand>forView(getView().provideOperationDelegate())
                  .onSuccess(this::onCardAdd)
                  .onFail(ErrorHandler.<AddBankCardCommand>builder(getContext())
                        .handle(FormatException.class, R.string.wallet_add_card_details_error_message)
                        .build())
                  .wrap());
   }

   private void onCardAdd(AddBankCardCommand command) {
      if (command.setAsDefaultCard()) trackSetAsDefault(command.getResult());
      trackAddedCard(bankCard, command.setAsDefaultCard());
      navigator.single(new CardListPath(), Direction.REPLACE);
   }

   private void trackSetAsDefault(BankCard bankCard) {
      analyticsInteractor.walletAnalyticsCommandPipe()
            .send(new WalletAnalyticsCommand(SetDefaultCardAction.forBankCard(bankCard)));
   }

   private void trackAddedCard(BankCard bankCard, boolean setAsDefault) {
      analyticsInteractor.walletAnalyticsCommandPipe()
            .send(new WalletAnalyticsCommand(CardDetailsOptionsAction.forBankCard(bankCard, setAsDefault)));
   }

   private void loadDataFromDevice() {
      smartCardInteractor.fetchDefaultCardCommandPipe().send(new FetchDefaultCardCommand());
      smartCardInteractor.getDefaultAddressCommandPipe().send(new GetDefaultAddressCommand());
   }

   public void onCardInfoConfirmed(AddressInfo addressInfo, String cvv, String nickname, boolean useDefaultAddress, boolean setAsDefaultAddress, boolean setAsDefaultCard) {
      smartCardInteractor.saveCardDetailsDataPipe()
            .send(new AddBankCardCommand.Builder().setBankCard(bankCard)
                  .setManualAddressInfo(addressInfo)
                  .setNickName(nickname)
                  .setCvv(cvv)
                  .setIssuerInfo(bankCard.issuerInfo())
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
