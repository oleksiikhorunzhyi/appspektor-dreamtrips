package com.worldventures.dreamtrips.wallet.ui.records.add;

import android.content.Context;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.text.TextUtils;

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
import com.worldventures.dreamtrips.wallet.domain.entity.record.Record;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.service.command.AddRecordCommand;
import com.worldventures.dreamtrips.wallet.service.command.GetDefaultAddressCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;
import com.worldventures.dreamtrips.wallet.ui.common.helper.ErrorHandler;
import com.worldventures.dreamtrips.wallet.ui.common.helper.ErrorSubscriberWrapper;
import com.worldventures.dreamtrips.wallet.ui.common.helper.OperationActionStateSubscriberWrapper;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;
import com.worldventures.dreamtrips.wallet.ui.dashboard.CardListPath;
import com.worldventures.dreamtrips.wallet.util.AddressFormatException;
import com.worldventures.dreamtrips.wallet.util.CardNameFormatException;
import com.worldventures.dreamtrips.wallet.util.CvvFormatException;
import com.worldventures.dreamtrips.wallet.util.SmartCardInteractorHelper;
import com.worldventures.dreamtrips.wallet.util.WalletRecordUtil;
import com.worldventures.dreamtrips.wallet.util.WalletValidateHelper;

import javax.inject.Inject;

import flow.Flow.Direction;
import io.techery.janet.helper.ActionStateToActionTransformer;
import rx.Observable;
import rx.functions.Action1;

import static android.text.TextUtils.getTrimmedLength;

public class AddCardDetailsPresenter extends WalletPresenter<AddCardDetailsPresenter.Screen, Parcelable> {

   @Inject Navigator navigator;
   @Inject SmartCardInteractor smartCardInteractor;
   @Inject AnalyticsInteractor analyticsInteractor;
   @Inject SmartCardInteractorHelper smartCardInteractorHelper;

   private final Record record;

   public AddCardDetailsPresenter(Context context, Injector injector, Record record) {
      super(context, injector);
      this.record = record;
   }

   @Override
   public void attachView(Screen view) {
      super.attachView(view);
      getView().setCardBank(record);
   }

   @Override
   public void onAttachedToWindow() {
      super.onAttachedToWindow();
      trackScreen();
      connectToDefaultCardPipe();
      connectToDefaultAddressPipe();
      connectToSaveCardDetailsPipe();
      loadDataFromDevice();
      observeMandatoryFields();
   }

   private void trackScreen() {
      smartCardInteractor.deviceStatePipe()
            .observeSuccessWithReplay()
            .take(1)
            .subscribe(command -> analyticsInteractor.walletAnalyticsCommandPipe()
                  .send(new WalletAnalyticsCommand(AddCardDetailsAction.forBankCard(record,
                        command.getResult().connectionStatus().isConnected()))));
   }

   private void connectToDefaultCardPipe() {
      smartCardInteractorHelper.sendSingleDefaultCardTask(bankCard -> {
         getView().defaultPaymentCard(!WalletRecordUtil.isRealRecord(bankCard));
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
                     .locale(LocaleHelper.getDefaultLocale())
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
            .subscribe(OperationActionStateSubscriberWrapper.<AddRecordCommand>forView(getView().provideOperationDelegate())
                  .onSuccess(this::onCardAdd)
                  .onFail(ErrorHandler.<AddRecordCommand>builder(getContext())
                        // this changes need for improve error handling in feature
                        .handle(CardNameFormatException.class, R.string.wallet_add_card_details_error_message)
                        .handle(CvvFormatException.class, R.string.wallet_add_card_details_error_message)
                        .handle(AddressFormatException.class, R.string.wallet_add_card_details_error_message)
                        .build())
                  .wrap());
   }

   private void onCardAdd(AddRecordCommand command) {
      if (command.setAsDefaultRecord()) trackSetAsDefault(command.getResult());
      trackAddedCard(record, command.setAsDefaultRecord());
      navigator.single(new CardListPath(), Direction.REPLACE);
   }

   private void trackSetAsDefault(Record record) {
      analyticsInteractor.walletAnalyticsCommandPipe()
            .send(new WalletAnalyticsCommand(SetDefaultCardAction.forBankCard(record)));
   }

   private void trackAddedCard(Record record, boolean setAsDefault) {
      analyticsInteractor.walletAnalyticsCommandPipe()
            .send(new WalletAnalyticsCommand(CardDetailsOptionsAction.forBankCard(record, setAsDefault)));
   }

   private void loadDataFromDevice() {
      smartCardInteractor.getDefaultAddressCommandPipe().send(new GetDefaultAddressCommand());
   }

   public void onCardInfoConfirmed(AddressInfo addressInfo, String cvv, String cardName, boolean setAsDefaultCard) {
      smartCardInteractor.saveCardDetailsDataPipe()
            .send(new AddRecordCommand.Builder().setRecord(record)
                  .setManualAddressInfo(addressInfo)
                  .setCardName(cardName)
                  .setCvv(cvv)
                  .setSetAsDefaultCard(setAsDefaultCard)
                  .create());
   }

   private void onSetAsDefaultCard(boolean setDefaultCard) {
      if (!setDefaultCard) return;

      smartCardInteractorHelper.sendSingleDefaultCardTask(defaultCard -> {
         if (!WalletRecordUtil.isRealRecord(defaultCard)) return;
         getView().showChangeCardDialog(defaultCard);
      }, bindViewIoToMainComposer());
   }

   public void defaultCardDialogConfirmed(boolean confirmed) {
      if (!confirmed) getView().defaultPaymentCard(false);
   }

   public void goBack() {
      navigator.goBack();
   }

   private Observable<Boolean> observeCardNickName() {
      return getView().getCardNicknameObservable()
            .filter(cardName -> !TextUtils.isEmpty(cardName))
            .map(this::validateNickName);
   }

   private boolean validateNickName(String nickname) {
      if (WalletValidateHelper.validateCardName(nickname)) {
         getView().hideCardNameError();
         getView().setCardName(nickname);
         return true;
      } else {
         getView().showCardNameError();
         return false;
      }
   }

   private void observeMandatoryFields() {
      final Screen screen = getView();
      //noinspection ConstantConditions
      Observable.combineLatest(
            observeCardNickName(),
            screen.getAddress1Observable(),
            screen.getCityObservable(),
            screen.getZipObservable(),
            screen.getStateObservable(),
            screen.getCvvObservable(),
            this::checkMandatoryFields)
            .compose(bindView())
            .subscribe(screen::setEnableButton);
   }

   private boolean checkMandatoryFields(boolean cardNameValid, String address1, String city, String zipCode, String state, String cvv) {
      return cardNameValid
            && getTrimmedLength(address1) > 0
            && getTrimmedLength(city) > 0
            && getTrimmedLength(zipCode) > 0
            && getTrimmedLength(state) > 0
            && cvv.length() == WalletRecordUtil.obtainRequiredCvvLength(record.number());
   }

   public interface Screen extends WalletScreen {

      void setCardBank(Record record);

      void setCardName(String cardName);

      Observable<String> getCardNicknameObservable();

      Observable<String> getAddress1Observable();

      Observable<String> getStateObservable();

      Observable<String> getZipObservable();

      Observable<String> getCityObservable();

      Observable<String> getCvvObservable();

      void defaultAddress(AddressInfoWithLocale defaultAddress);

      void defaultPaymentCard(boolean defaultPaymentCard);

      void showChangeCardDialog(Record record);

      Observable<Boolean> setAsDefaultPaymentCardCondition();

      void setEnableButton(boolean enable);

      void showCardNameError();

      void hideCardNameError();
   }
}
