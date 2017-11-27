package com.worldventures.wallet.ui.records.add.impl;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.wallet.analytics.AddCardDetailsAction;
import com.worldventures.wallet.analytics.CardDetailsOptionsAction;
import com.worldventures.wallet.analytics.SetDefaultCardAction;
import com.worldventures.wallet.analytics.WalletAnalyticsCommand;
import com.worldventures.wallet.domain.entity.record.Record;
import com.worldventures.wallet.service.RecordInteractor;
import com.worldventures.wallet.service.SmartCardInteractor;
import com.worldventures.wallet.service.WalletAnalyticsInteractor;
import com.worldventures.wallet.service.WizardInteractor;
import com.worldventures.wallet.service.command.RecordListCommand;
import com.worldventures.wallet.service.command.record.AddRecordCommand;
import com.worldventures.wallet.service.command.record.DefaultRecordIdCommand;
import com.worldventures.wallet.service.provisioning.PinOptionalCommand;
import com.worldventures.wallet.ui.common.base.WalletDeviceConnectionDelegate;
import com.worldventures.wallet.ui.common.base.WalletPresenterImpl;
import com.worldventures.wallet.ui.common.navigation.Navigator;
import com.worldventures.wallet.ui.records.add.AddCardDetailsPresenter;
import com.worldventures.wallet.ui.records.add.AddCardDetailsScreen;
import com.worldventures.wallet.ui.records.add.RecordBundle;
import com.worldventures.wallet.util.WalletRecordUtil;
import com.worldventures.wallet.util.WalletValidateHelper;

import java.util.List;

import io.techery.janet.Command;
import io.techery.janet.operationsubscriber.OperationActionSubscriber;
import io.techery.janet.smartcard.action.settings.CheckPinStatusAction;
import io.techery.janet.smartcard.event.PinStatusEvent;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import timber.log.Timber;

import static com.worldventures.wallet.ui.records.add.UtilsKt.toRecord;
import static com.worldventures.wallet.ui.records.add.UtilsKt.toRecordViewModel;

public class AddCardDetailsPresenterImpl extends WalletPresenterImpl<AddCardDetailsScreen> implements AddCardDetailsPresenter {

   private final SmartCardInteractor smartCardInteractor;
   private final WalletAnalyticsInteractor analyticsInteractor;
   private final RecordInteractor recordInteractor;
   private final WizardInteractor wizardInteractor;

   private RecordBundle recordBundle;
   private String recordTitle = "";

   public AddCardDetailsPresenterImpl(Navigator navigator, WalletDeviceConnectionDelegate deviceConnectionDelegate,
         SmartCardInteractor smartCardInteractor, WalletAnalyticsInteractor analyticsInteractor, RecordInteractor recordInteractor,
         WizardInteractor wizardInteractor) {
      super(navigator, deviceConnectionDelegate);
      this.smartCardInteractor = smartCardInteractor;
      this.analyticsInteractor = analyticsInteractor;
      this.recordInteractor = recordInteractor;
      this.wizardInteractor = wizardInteractor;
   }

   @Override
   public void attachView(AddCardDetailsScreen view) {
      super.attachView(view);
      recordBundle = getView().getRecordBundle();
      observeDefaultCardChangeByUser();
      observeCardNameValidation();
      observeSavingCardDetailsData();
      observeMandatoryFields();
      observePinOptions();
      trackScreen();
   }

   @Override
   public void fetchRecordViewModel() {
      getView().setCardBank(toRecordViewModel(recordBundle));
      presetRecordToDefaultIfNeeded();
   }

   private void observePinOptions() {
      Observable.combineLatest(
            smartCardInteractor.pinStatusEventPipe()
                  .observeSuccess()
                  .map(pinStatusEvent -> pinStatusEvent.pinStatus != PinStatusEvent.PinStatus.DISABLED),
            wizardInteractor.pinOptionalActionPipe()
                  .observeSuccess()
                  .map(Command::getResult), (isEnabled, shouldAsk) -> !isEnabled && shouldAsk)
            .compose(getView().bindUntilDetach())
            .observeOn(AndroidSchedulers.mainThread())
            .take(1)
            .subscribe(this::handlePinOptions);
   }

   private void handlePinOptions(Boolean shouldShowPinSuggestion) {
      if (shouldShowPinSuggestion) {
         getNavigator().goPinProposalRecords(recordTitle);
      } else {
         getNavigator().goCardList();
      }
   }

   private void observeDefaultCardChangeByUser() {
      getView().setAsDefaultPaymentCardCondition()
            .compose(getView().bindUntilDetach())
            .subscribe(this::onUpdateStatusDefaultCard);
   }

   private void observeSavingCardDetailsData() {
      recordInteractor.addRecordPipe()
            .observe()
            .compose(getView().bindUntilDetach())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(OperationActionSubscriber.forView(getView().provideOperationAddRecord())
                  .onSuccess(this::onCardAdd)
                  .create());
   }

   private void onCardAdd(AddRecordCommand command) {
      if (command.setAsDefaultRecord()) {
         trackSetAsDefault(command.getResult());
      }
      trackAddedCard(command.getResult(), command.setAsDefaultRecord());
      smartCardInteractor.checkPinStatusActionPipe().send(new CheckPinStatusAction());
      wizardInteractor.pinOptionalActionPipe().send(PinOptionalCommand.fetch());
   }

   @Override
   public void onCardInfoConfirmed(String cvv, String cardName, boolean setAsDefaultCard) {
      this.recordTitle = cardName;
      recordInteractor.addRecordPipe()
            .send(new AddRecordCommand.Builder().setRecord(toRecord(recordBundle))
                  .setRecordName(cardName)
                  .setCvv(cvv)
                  .setSetAsDefaultRecord(setAsDefaultCard)
                  .create());
   }

   private void presetRecordToDefaultIfNeeded() {
      Observable.zip(fetchLocalRecords(), fetchDefaultRecordId(),
            (records, defaultRecordId) -> (records.isEmpty() || defaultRecordId == null))
            .compose(getView().bindUntilDetach())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(shouldBeDefault -> getView().defaultPaymentCard(shouldBeDefault), Timber::e);
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
                  Queryable.from(records).firstOrDefault(element -> defaultRecordId.equals(element.getId()))))
            .filter(defaultRecord -> defaultRecord != null)
            .compose(getView().bindUntilDetach())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(defaultRecord -> getView().showChangeCardDialog(defaultRecord), Timber::e);
   }

   private Observable<List<Record>> fetchLocalRecords() {
      return recordInteractor.cardsListPipe()
            .createObservableResult(RecordListCommand.Companion.fetch())
            .map(command -> (List<Record>) command.getResult());
   }

   private Observable<String> fetchDefaultRecordId() {
      return recordInteractor.defaultRecordIdPipe()
            .createObservableResult(DefaultRecordIdCommand.fetch())
            .map(Command::getResult);
   }

   @Override
   public void onCardToDefaultClick(boolean confirmed) {
      if (!confirmed) {
         getView().defaultPaymentCard(false);
      }
   }

   @Override
   public void goBack() {
      getNavigator().goBack();
   }

   private void observeCardNameValidation() {
      getView().getCardNicknameObservable()
            .skip(1)
            .compose(getView().bindUntilDetach())
            .subscribe(this::validateCardName, Timber::e);
   }

   private void validateCardName(String cardName) {
      if (WalletValidateHelper.isValidCardName(cardName)) {
         getView().hideCardNameError();
         getView().setCardName(cardName);
      } else {
         getView().showCardNameError();
      }
   }

   private void observeMandatoryFields() {
      AddCardDetailsScreen view = getView();
      Observable.combineLatest(
            view.getCardNicknameObservable(),
            view.getCvvObservable(),
            this::checkMandatoryFields
      )
            .compose(getView().bindUntilDetach())
            .subscribe(getView()::setEnableButton);
   }

   private boolean checkMandatoryFields(String cardName, String cvv) {
      return WalletValidateHelper.isValidCardName(cardName) && WalletRecordUtil.Companion.validationMandatoryFields(recordBundle
            .getCardNumber(), cvv);
   }

   private void trackScreen() {
      analyticsInteractor.walletAnalyticsPipe()
            .send(new WalletAnalyticsCommand(new AddCardDetailsAction(toRecord(recordBundle))));
   }

   private void trackSetAsDefault(Record record) {
      analyticsInteractor.walletAnalyticsPipe()
            .send(new WalletAnalyticsCommand(new SetDefaultCardAction(record)));
   }

   private void trackAddedCard(Record record, boolean setAsDefault) {
      analyticsInteractor.walletAnalyticsPipe()
            .send(new WalletAnalyticsCommand(new CardDetailsOptionsAction(record, setAsDefault)));
   }
}
