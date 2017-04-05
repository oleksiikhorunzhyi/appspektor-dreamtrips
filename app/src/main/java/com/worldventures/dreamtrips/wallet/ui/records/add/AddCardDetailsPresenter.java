package com.worldventures.dreamtrips.wallet.ui.records.add;

import android.content.Context;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.innahema.collections.query.queriables.Queryable;
import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsInteractor;
import com.worldventures.dreamtrips.wallet.analytics.AddCardDetailsAction;
import com.worldventures.dreamtrips.wallet.analytics.CardDetailsOptionsAction;
import com.worldventures.dreamtrips.wallet.analytics.SetDefaultCardAction;
import com.worldventures.dreamtrips.wallet.analytics.WalletAnalyticsCommand;
import com.worldventures.dreamtrips.wallet.domain.entity.AddressInfo;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardStatus;
import com.worldventures.dreamtrips.wallet.domain.entity.record.Record;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.service.command.GetDefaultAddressCommand;
import com.worldventures.dreamtrips.wallet.service.command.RecordListCommand;
import com.worldventures.dreamtrips.wallet.service.command.record.AddRecordCommand;
import com.worldventures.dreamtrips.wallet.service.command.record.DefaultRecordIdCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;
import com.worldventures.dreamtrips.wallet.ui.dashboard.CardListPath;
import com.worldventures.dreamtrips.wallet.util.WalletRecordUtil;
import com.worldventures.dreamtrips.wallet.util.WalletValidateHelper;

import java.util.List;

import javax.inject.Inject;

import flow.Flow.Direction;
import io.techery.janet.Command;
import io.techery.janet.operationsubscriber.OperationActionSubscriber;
import io.techery.janet.operationsubscriber.view.OperationView;
import rx.Observable;
import timber.log.Timber;

public class AddCardDetailsPresenter extends WalletPresenter<AddCardDetailsPresenter.Screen, Parcelable> {

   @Inject Navigator navigator;
   @Inject SmartCardInteractor smartCardInteractor;
   @Inject AnalyticsInteractor analyticsInteractor;

   private final Record record;

   public AddCardDetailsPresenter(Context context, Injector injector, Record record) {
      super(context, injector);
      this.record = record;
   }

   @Override
   public void attachView(Screen view) {
      super.attachView(view);
      view.setCardBank(record);
   }

   @Override
   public void onAttachedToWindow() {
      super.onAttachedToWindow();
      trackScreen();
      presetRecordToDefaultIfNeeded();
      observeDefaultCardChangeByUser();
      observeFetchingDefaultAddress();
      observeSavingCardDetailsData();
      loadDataFromDevice();
      observeMandatoryFields();
   }

   private void observeDefaultCardChangeByUser() {
      getView().setAsDefaultPaymentCardCondition()
            .compose(bindView())
            .subscribe(this::onUpdateStatusDefaultCard);
   }

   private void observeFetchingDefaultAddress() {
      smartCardInteractor.getDefaultAddressCommandPipe()
            .observeWithReplay()
            .compose(bindViewIoToMainComposer())
            .subscribe(OperationActionSubscriber.forView(getView().provideOperationGetDefaultAddress())
                  .onSuccess(command -> setDefaultAddress(command.getResult()))
                  .create()
            );
   }

   private void setDefaultAddress(@Nullable AddressInfo defaultAddress) {
      if (defaultAddress == null) return;
      getView().defaultAddress(defaultAddress);
   }

   private void observeSavingCardDetailsData() {
      smartCardInteractor.addRecordPipe()
            .observe()
            .compose(bindViewIoToMainComposer())
            .subscribe(OperationActionSubscriber.forView(getView().provideOperationAddRecord())
                  .onSuccess(this::onCardAdd)
                  .create());
   }

   private void onCardAdd(AddRecordCommand command) {
      if (command.setAsDefaultRecord()) trackSetAsDefault(command.getResult());
      trackAddedCard(record, command.setAsDefaultRecord());
      navigator.single(new CardListPath(), Direction.REPLACE);
   }

   private void loadDataFromDevice() {
      smartCardInteractor.getDefaultAddressCommandPipe().send(new GetDefaultAddressCommand());
   }

   void onCardInfoConfirmed(AddressInfo addressInfo, String cvv, String cardName, boolean setAsDefaultCard) {
      smartCardInteractor.addRecordPipe()
            .send(new AddRecordCommand.Builder().setRecord(record)
                  .setManualAddressInfo(addressInfo)
                  .setRecordName(cardName)
                  .setCvv(cvv)
                  .setSetAsDefaultRecord(setAsDefaultCard)
                  .create());
   }

   private void presetRecordToDefaultIfNeeded() {
      Observable.zip(fetchLocalRecords(), fetchDefaultRecordId(),
            (records, defaultRecordId) -> (records.isEmpty() || defaultRecordId == null))
            .compose(bindViewIoToMainComposer())
            .subscribe(shouldBeDefault -> getView().defaultPaymentCard(shouldBeDefault), throwable -> Timber.e(throwable, ""));
   }

   private void onUpdateStatusDefaultCard(boolean setDefaultCard) {
      if (setDefaultCard) {
         setCardAsDefault();
      }
   }

   private void setCardAsDefault() {
      fetchDefaultRecordId()
            .filter(defaultRecordId -> defaultRecordId != null)
            .flatMap(defaultRecordId -> fetchLocalRecords().map(records ->
                  Queryable.from(records).firstOrDefault(element -> defaultRecordId.equals(element.id()))))
            .filter(defaultRecord -> defaultRecord != null)
            .compose(bindViewIoToMainComposer())
            .subscribe(defaultRecord -> getView().showChangeCardDialog(defaultRecord), throwable -> Timber.e(throwable, ""));
   }

   private Observable<List<Record>> fetchLocalRecords() {
      return smartCardInteractor.cardsListPipe()
            .createObservableResult(RecordListCommand.fetch())
            .map(Command::getResult);
   }

   private Observable<String> fetchDefaultRecordId() {
      return smartCardInteractor.defaultRecordIdPipe()
            .createObservableResult(DefaultRecordIdCommand.fetch())
            .map(Command::getResult);
   }

   void onCardToDefaultClick(boolean confirmed) {
      if (!confirmed) {
         getView().defaultPaymentCard(false);
      }
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
      return cardNameValid && WalletRecordUtil.validationMandatoryFields(
            record.number(),
            address1,
            city,
            zipCode,
            state,
            cvv
      );
   }

   private void trackScreen() {
      smartCardInteractor.deviceStatePipe()
            .observeSuccessWithReplay()
            .take(1)
            .map(Command::getResult)
            .map(SmartCardStatus::connectionStatus)
            .subscribe(connectionStatus -> analyticsInteractor.walletAnalyticsCommandPipe()
                  .send(new WalletAnalyticsCommand(AddCardDetailsAction
                        .forBankCard(record, connectionStatus.isConnected()))));
   }

   private void trackSetAsDefault(Record record) {
      analyticsInteractor.walletAnalyticsCommandPipe()
            .send(new WalletAnalyticsCommand(SetDefaultCardAction.forBankCard(record)));
   }

   private void trackAddedCard(Record record, boolean setAsDefault) {
      analyticsInteractor.walletAnalyticsCommandPipe()
            .send(new WalletAnalyticsCommand(CardDetailsOptionsAction.forBankCard(record, setAsDefault)));
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

      void defaultAddress(AddressInfo addressInfo);

      void defaultPaymentCard(boolean defaultPaymentCard);

      void showChangeCardDialog(Record record);

      Observable<Boolean> setAsDefaultPaymentCardCondition();

      void setEnableButton(boolean enable);

      void showCardNameError();

      void hideCardNameError();

      OperationView<GetDefaultAddressCommand> provideOperationGetDefaultAddress();

      OperationView<AddRecordCommand> provideOperationAddRecord();
   }
}
