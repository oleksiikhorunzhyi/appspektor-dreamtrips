package com.worldventures.dreamtrips.wallet.ui.records.add;

import android.content.Context;
import android.os.Parcelable;
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
import com.worldventures.dreamtrips.wallet.util.AddressFormatException;
import com.worldventures.dreamtrips.wallet.util.BankCardHelper;
import com.worldventures.dreamtrips.wallet.util.CardNameFormatException;
import com.worldventures.dreamtrips.wallet.util.CardUtils;
import com.worldventures.dreamtrips.wallet.util.CvvFormatException;
import com.worldventures.dreamtrips.wallet.util.SmartCardInteractorHelper;

import javax.inject.Inject;

import flow.Flow.Direction;
import io.techery.janet.helper.ActionStateToActionTransformer;
import rx.Observable;
import rx.functions.Action1;

import static android.text.TextUtils.getTrimmedLength;

public class AddCardDetailsPresenter extends WalletPresenter<AddCardDetailsPresenter.Screen, Parcelable> {

   @Inject Navigator navigator;
   @Inject LocaleHelper localeHelper;
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
      getView().setCardBank(bankCard);
   }

   @Override
   public void onAttachedToWindow() {
      super.onAttachedToWindow();
      trackScreen();
      observeCardNameChanging();
      connectToDefaultCardPipe();
      connectToDefaultAddressPipe();
      connectToSaveCardDetailsPipe();
      loadDataFromDevice();
      observeMandatoryFields();
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
                        // this changes need for improve error handling in feature
                        .handle(CardNameFormatException.class, R.string.wallet_add_card_details_error_message)
                        .handle(CvvFormatException.class, R.string.wallet_add_card_details_error_message)
                        .handle(AddressFormatException.class, R.string.wallet_add_card_details_error_message)
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

   public void onCardInfoConfirmed(AddressInfo addressInfo, String cvv, String cardName, boolean setAsDefaultCard) {
      smartCardInteractor.saveCardDetailsDataPipe()
            .send(new AddBankCardCommand.Builder().setBankCard(bankCard)
                  .setManualAddressInfo(addressInfo)
                  .setCardName(cardName)
                  .setCvv(cvv)
                  .setIssuerInfo(bankCard.issuerInfo())
                  .setSetAsDefaultCard(setAsDefaultCard)
                  .create());
   }

   private void onSetAsDefaultCard(boolean setDefaultCard) {
      if (!setDefaultCard) return;

      smartCardInteractorHelper.sendSingleDefaultCardTask(defaultCard -> {
         if (!CardUtils.isRealCard(defaultCard)) return;
         getView().showChangeCardDialog(defaultCard);
      }, bindViewIoToMainComposer());
   }

   public void defaultCardDialogConfirmed(boolean confirmed) {
      if (!confirmed) getView().defaultPaymentCard(false);
   }

   public void goBack() {
      navigator.goBack();
   }

   private void observeCardNameChanging() {
      final Screen view = getView();
      view.getCardNameObservable()
            .compose(bindView())
            .subscribe(view::setCardName);
   }

   private void observeMandatoryFields() {
      final Screen screen = getView();

      Observable.combineLatest(
            screen.getCardNameObservable(),
            screen.getAddress1Observable(),
            screen.getCityObservable(),
            screen.getZipObservable(),
            screen.getStateObservable(),
            screen.getCvvObservable(),
            this::checkMandatoryFields)
            .compose(bindView())
            .subscribe(screen::setEnableButton);
   }

   private boolean checkMandatoryFields(String cardName, String address1, String city, String zipCode, String state, String cvv) {
      return getTrimmedLength(cardName) > 0
            && getTrimmedLength(address1) > 0
            && getTrimmedLength(city) > 0
            && getTrimmedLength(zipCode) > 0
            && getTrimmedLength(state) > 0
            && cvv.length() == BankCardHelper.obtainRequiredCvvLength(bankCard.number());
   }

   public interface Screen extends WalletScreen {

      void setCardBank(BankCard bankCard);

      void setCardName(String cardName);

      Observable<String> getCardNameObservable();

      Observable<String> getAddress1Observable();

      Observable<String> getStateObservable();

      Observable<String> getZipObservable();

      Observable<String> getCityObservable();

      Observable<String> getCvvObservable();

      void defaultAddress(AddressInfoWithLocale defaultAddress);

      void defaultPaymentCard(boolean defaultPaymentCard);

      void showChangeCardDialog(BankCard bankCard);

      Observable<Boolean> setAsDefaultPaymentCardCondition();

      void setEnableButton(boolean enable);
   }

}
