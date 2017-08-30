package com.worldventures.dreamtrips.wallet.ui.records.add;

import android.content.Context;
import android.os.Parcelable;
import android.text.TextUtils;

import com.innahema.collections.query.queriables.Queryable;
import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsInteractor;
import com.worldventures.dreamtrips.core.utils.HttpErrorHandlingUtil;
import com.worldventures.dreamtrips.wallet.analytics.AddCardDetailsAction;
import com.worldventures.dreamtrips.wallet.analytics.CardDetailsOptionsAction;
import com.worldventures.dreamtrips.wallet.analytics.SetDefaultCardAction;
import com.worldventures.dreamtrips.wallet.analytics.WalletAnalyticsCommand;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardStatus;
import com.worldventures.dreamtrips.wallet.domain.entity.record.Record;
import com.worldventures.dreamtrips.wallet.service.RecordInteractor;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.service.WizardInteractor;
import com.worldventures.dreamtrips.wallet.service.command.RecordListCommand;
import com.worldventures.dreamtrips.wallet.service.command.record.AddRecordCommand;
import com.worldventures.dreamtrips.wallet.service.command.record.DefaultRecordIdCommand;
import com.worldventures.dreamtrips.wallet.service.provisioning.PinOptionalCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;
import com.worldventures.dreamtrips.wallet.ui.dashboard.CardListPath;
import com.worldventures.dreamtrips.wallet.ui.wizard.pin.proposal.PinProposalAction;
import com.worldventures.dreamtrips.wallet.ui.wizard.pin.proposal.PinProposalPath;
import com.worldventures.dreamtrips.wallet.util.WalletRecordUtil;
import com.worldventures.dreamtrips.wallet.util.WalletValidateHelper;

import java.util.List;

import javax.inject.Inject;

import flow.Flow.Direction;
import io.techery.janet.Command;
import io.techery.janet.operationsubscriber.OperationActionSubscriber;
import io.techery.janet.operationsubscriber.view.OperationView;
import io.techery.janet.smartcard.action.settings.CheckPinStatusAction;
import io.techery.janet.smartcard.event.PinStatusEvent;
import rx.Observable;
import timber.log.Timber;

public class AddCardDetailsPresenter extends WalletPresenter<AddCardDetailsPresenter.Screen, Parcelable> {

   @Inject Navigator navigator;
   @Inject SmartCardInteractor smartCardInteractor;
   @Inject AnalyticsInteractor analyticsInteractor;
   @Inject RecordInteractor recordInteractor;
   @Inject WizardInteractor wizardInteractor;
   @Inject HttpErrorHandlingUtil httpErrorHandlingUtil;

   private final Record record;
   private String cardNickname;

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
      observeSavingCardDetailsData();
      observeMandatoryFields();
      observePinOptions();
   }

   private void observePinOptions() {
      Observable.combineLatest(
            smartCardInteractor.pinStatusEventPipe()
                  .observeSuccess()
                  .map(pinStatusEvent -> pinStatusEvent.pinStatus != PinStatusEvent.PinStatus.DISABLED),
            wizardInteractor.pinOptionalActionPipe()
                  .observeSuccess()
                  .map(Command::getResult), (isEnabled, shouldAsk) -> !isEnabled && shouldAsk)
            .compose(bindViewIoToMainComposer())
            .take(1)
            .subscribe(this::handlePinOptions);
   }

   private void handlePinOptions(Boolean shouldShowPinSuggestion) {
      if (shouldShowPinSuggestion) {
         navigator.withoutLast(new PinProposalPath(PinProposalAction.RECORDS, cardNickname));
      } else {
         navigator.single(new CardListPath(), Direction.REPLACE);
      }
   }

   private void observeDefaultCardChangeByUser() {
      getView().setAsDefaultPaymentCardCondition()
            .compose(bindView())
            .subscribe(this::onUpdateStatusDefaultCard);
   }

   private void observeSavingCardDetailsData() {
      recordInteractor.addRecordPipe()
            .observe()
            .compose(bindViewIoToMainComposer())
            .subscribe(OperationActionSubscriber.forView(getView().provideOperationAddRecord())
                  .onSuccess(this::onCardAdd)
                  .create());
   }

   private void onCardAdd(AddRecordCommand command) {
      if (command.setAsDefaultRecord()) trackSetAsDefault(command.getResult());
      trackAddedCard(record, command.setAsDefaultRecord());
      smartCardInteractor.checkPinStatusActionPipe().send(new CheckPinStatusAction());
      wizardInteractor.pinOptionalActionPipe().send(PinOptionalCommand.fetch());
   }

   void onCardInfoConfirmed(String cvv, String cardName, boolean setAsDefaultCard) {
      this.cardNickname = cardName;
      recordInteractor.addRecordPipe()
            .send(new AddRecordCommand.Builder().setRecord(record)
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
      return recordInteractor.cardsListPipe()
            .createObservableResult(RecordListCommand.fetch())
            .map(Command::getResult);
   }

   private Observable<String> fetchDefaultRecordId() {
      return recordInteractor.defaultRecordIdPipe()
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
      if (WalletValidateHelper.isValidCardName(nickname)) {
         getView().hideCardNameError();
         getView().setCardName(nickname);
         return true;
      } else {
         getView().showCardNameError();
         return false;
      }
   }

   private void observeMandatoryFields() {
      Observable.combineLatest(observeCardNickName(), getView().getCvvObservable(), this::checkMandatoryFields)
            .compose(bindView())
            .subscribe(getView()::setEnableButton);
   }

   private boolean checkMandatoryFields(boolean cardNameValid, String cvv) {
      return cardNameValid && WalletRecordUtil.validationMandatoryFields(record.number(), cvv);
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

   public HttpErrorHandlingUtil httpErrorHandlingUtil() {
      return httpErrorHandlingUtil;
   }

   public interface Screen extends WalletScreen {

      void setCardBank(Record record);

      void setCardName(String cardName);

      Observable<String> getCardNicknameObservable();

      Observable<String> getCvvObservable();

      void defaultPaymentCard(boolean defaultPaymentCard);

      void showChangeCardDialog(Record record);

      Observable<Boolean> setAsDefaultPaymentCardCondition();

      void setEnableButton(boolean enable);

      void showCardNameError();

      void hideCardNameError();

      OperationView<AddRecordCommand> provideOperationAddRecord();
   }
}
