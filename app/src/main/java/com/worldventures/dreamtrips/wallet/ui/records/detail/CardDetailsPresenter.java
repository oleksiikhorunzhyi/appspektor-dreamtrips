package com.worldventures.dreamtrips.wallet.ui.records.detail;

import android.content.Context;
import android.os.Parcelable;
import android.text.TextUtils;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.core.janet.composer.ActionPipeCacheWiper;
import com.worldventures.dreamtrips.core.utils.LocaleHelper;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsInteractor;
import com.worldventures.dreamtrips.wallet.analytics.CardDetailsAction;
import com.worldventures.dreamtrips.wallet.analytics.ChangeDefaultCardAction;
import com.worldventures.dreamtrips.wallet.analytics.PaycardAnalyticsCommand;
import com.worldventures.dreamtrips.wallet.analytics.WalletAnalyticsCommand;
import com.worldventures.dreamtrips.wallet.domain.entity.AddressInfoWithLocale;
import com.worldventures.dreamtrips.wallet.domain.entity.ConnectionStatus;
import com.worldventures.dreamtrips.wallet.domain.entity.ImmutableAddressInfoWithLocale;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardStatus;
import com.worldventures.dreamtrips.wallet.domain.entity.record.ImmutableRecord;
import com.worldventures.dreamtrips.wallet.domain.entity.record.Record;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.service.command.RecordListCommand;
import com.worldventures.dreamtrips.wallet.service.command.SetDefaultCardOnDeviceCommand;
import com.worldventures.dreamtrips.wallet.service.command.SetPaymentCardAction;
import com.worldventures.dreamtrips.wallet.service.command.UpdateRecordCommand;
import com.worldventures.dreamtrips.wallet.service.command.device.DeviceStateCommand;
import com.worldventures.dreamtrips.wallet.service.command.record.DefaultRecordIdCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;
import com.worldventures.dreamtrips.wallet.ui.records.address.EditBillingAddressPath;
import com.worldventures.dreamtrips.wallet.util.WalletRecordUtil;
import com.worldventures.dreamtrips.wallet.util.WalletValidateHelper;

import javax.inject.Inject;

import io.techery.janet.Command;
import io.techery.janet.operationsubscriber.OperationActionSubscriber;
import io.techery.janet.operationsubscriber.view.OperationView;
import io.techery.janet.smartcard.action.records.DeleteRecordAction;
import rx.Observable;
import rx.functions.Action1;
import timber.log.Timber;

import static java.lang.Integer.valueOf;

public class CardDetailsPresenter extends WalletPresenter<CardDetailsPresenter.Screen, Parcelable> {

   @Inject Navigator navigator;
   @Inject SmartCardInteractor smartCardInteractor;
   @Inject AnalyticsInteractor analyticsInteractor;

   private Record record;

   public CardDetailsPresenter(Context context, Injector injector, Record record) {
      super(context, injector);
      this.record = record;
   }

   @Override
   public void onAttachedToWindow() {
      super.onAttachedToWindow();
      trackScreen();
      Screen view = getView();

      view.showWalletRecord(record);
      view.showDefaultAddress(obtainAddressWithCountry());

      updateCardConditionState();
      connectToDeleteCardPipe();
      connectToSetDefaultCardIdPipe();
      connectSetPaymentCardPipe();
      observeCardNickName();
      observeDefaultCard();
      observeSaveCardData();
   }

   private void observeSaveCardData() {
      smartCardInteractor.updateRecordPipe()
            .observe()
            .compose(bindViewIoToMainComposer())
            .subscribe(OperationActionSubscriber.forView(getView().provideOperationSaveCardData())
                  .onSuccess(command -> updateRecordVar(command.getResult()))
                  .create());
   }

   private void updateRecordVar(Record updatedRecord) {
      record = ImmutableRecord.builder()
            .from(record)
            .nickName(updatedRecord.nickName())
            .addressInfo(updatedRecord.addressInfo())
            .build();
   }

   public void updateNickName() {
      if (!TextUtils.equals(getView().getUpdateNickname(), record.nickName())) {
         nicknameUpdated(getView().getUpdateNickname());
      } else {
         getView().notifyCardDataIsSaved();
      }
   }

   private void trackScreen() {
      analyticsInteractor.paycardAnalyticsCommandPipe()
            .send(new PaycardAnalyticsCommand(new CardDetailsAction(record.nickName()), record));
   }

   private void connectToDeleteCardPipe() {
      smartCardInteractor.deleteRecordPipe()
            .observeWithReplay()
            .filter(state -> valueOf(record.id()).equals(state.action.recordId))
            .compose(bindViewIoToMainComposer())
            .compose(new ActionPipeCacheWiper<>(smartCardInteractor.deleteRecordPipe()))
            .subscribe(OperationActionSubscriber.forView(getView().provideOperationDeleteRecord())
                  .onSuccess(deleteRecordAction -> navigator.goBack())
                  .create());
   }

   private void connectToSetDefaultCardIdPipe() {
      smartCardInteractor.setDefaultCardOnDeviceCommandPipe()
            .observe()
            .compose(bindViewIoToMainComposer())
            .subscribe(OperationActionSubscriber.forView(getView().provideOperationSetDefaultOnDevice())
                  .create());
   }

   private void connectSetPaymentCardPipe() {
      smartCardInteractor.setPaymentCardActionActionPipe()
            .observeWithReplay()
            .compose(bindViewIoToMainComposer())
            .compose(new ActionPipeCacheWiper<>(smartCardInteractor.setPaymentCardActionActionPipe()))
            .subscribe(OperationActionSubscriber.forView(getView().provideOperationSetPaymentCardAction())
                  .onSuccess(action -> getView().showCardIsReadyDialog(record.nickName()))
                  .create());
   }

   private void observeCardNickName() {
      final Screen view = getView();
      view.getCardNicknameObservable()
            .compose(bindView())
            .filter(cardName -> !cardName.isEmpty())
            .subscribe(this::handleCardNickname);
   }

   private void handleCardNickname(String cardName) {
      if (WalletValidateHelper.validateCardName(cardName)) {
         getView().hideCardNameError();
         getView().setCardNickname(cardName);
      } else {
         getView().showCardNameError();
      }
   }

   private void observeDefaultCard() {
      getView().setAsDefaultPaymentCardCondition()
            .compose(bindView())
            .subscribe(this::onSetAsDefaultCard);
   }

   private AddressInfoWithLocale obtainAddressWithCountry() {
      return ImmutableAddressInfoWithLocale.builder()
            .addressInfo(record.addressInfo())
            .locale(LocaleHelper.getDefaultLocale())
            .build();
   }

   void onDeleteCardClick() {
      fetchConnectionStats(connectionStatus -> {
         if (connectionStatus.isConnected()) {
            getView().showDeleteCardDialog();
         } else {
            getView().showSCNonConnectionDialog();
         }
      });
   }

   void editAddress() {
      fetchConnectionStats(connectionStatus -> {
         if (connectionStatus.isConnected()) {
            navigator.go(new EditBillingAddressPath(record));
         } else {
            getView().showConnectionErrorDialog();
         }
      });
   }

   void payThisCard() {
      fetchConnectionStats(connectionStatus -> {
         if (connectionStatus.isConnected()) {
            smartCardInteractor.setPaymentCardActionActionPipe().send(new SetPaymentCardAction(record));
         } else {
            getView().showSCNonConnectionDialog();
         }
      });
   }

   void onCardIsReadyDialogShown() {
      navigator.goBack();
   }

   void onDeleteCardConfirmed() {
      smartCardInteractor.deleteRecordPipe().send(new DeleteRecordAction(valueOf(record.id())));
   }

   private void unsetCardAsDefault() {
      fetchDefaultRecordId(defaultRecordId -> {
         if (WalletRecordUtil.equals(defaultRecordId, record)) {
            smartCardInteractor.setDefaultCardOnDeviceCommandPipe()
                  .send(SetDefaultCardOnDeviceCommand.unsetDefaultCard());
         }
      });
   }

   private void setCardAsDefault() {
      fetchDefaultRecordId(defaultRecordId -> {
         if (WalletRecordUtil.isRealRecordId(defaultRecordId)) {
            smartCardInteractor.cardsListPipe()
                  .createObservableResult(RecordListCommand.fetchById(defaultRecordId))
                  .compose(bindViewIoToMainComposer())
                  .filter(command -> command.getResult() != null && !command.getResult().isEmpty())
                  .subscribe(command -> getView().showDefaultCardDialog(command.getResult().get(0)),
                        throwable -> Timber.e(throwable, ""));
         } else {
            trackSetAsDefault();
            smartCardInteractor.setDefaultCardOnDeviceCommandPipe()
                  .send(SetDefaultCardOnDeviceCommand.setAsDefault(record.id()));
         }
      });
   }

   void defaultCardDialogConfirmed(boolean confirmed) {
      if (!confirmed) {
         updateCardConditionState();
      } else {
         trackSetAsDefault();
         smartCardInteractor.setDefaultCardOnDeviceCommandPipe()
               .send(SetDefaultCardOnDeviceCommand.setAsDefault(record.id()));
      }
   }

   private void trackSetAsDefault() {
      analyticsInteractor.walletAnalyticsCommandPipe()
            .send(new WalletAnalyticsCommand(ChangeDefaultCardAction.forBankCard(record)));
   }

   public void goBack() {
      navigator.goBack();
   }

   private void nicknameUpdated(String nickName) {
      smartCardInteractor.updateRecordPipe().send(UpdateRecordCommand.updateNickName(record, nickName));
   }

   private void onSetAsDefaultCard(boolean setDefaultCard) {
      fetchConnectionStats(connectionStatus -> {
         if (connectionStatus.isConnected()) {
            if (setDefaultCard) {
               setCardAsDefault();
            } else {
               unsetCardAsDefault();
            }
         } else {
            updateCardConditionState();
            getView().showSCNonConnectionDialog();
         }
      });
   }

   private void updateCardConditionState() {
      fetchDefaultRecordId(defaultRecordId -> getView().setDefaultCardCondition(WalletRecordUtil.equals(defaultRecordId, record)));
   }

   private void fetchConnectionStats(Action1<ConnectionStatus> action) {
      smartCardInteractor.deviceStatePipe()
            .createObservableResult(DeviceStateCommand.fetch())
            .map(Command::getResult)
            .compose(bindViewIoToMainComposer())
            .map(SmartCardStatus::connectionStatus)
            .subscribe(action, throwable -> Timber.e(throwable, ""));
   }

   private void fetchDefaultRecordId(Action1<String> action) {
      smartCardInteractor.defaultRecordIdPipe()
            .createObservableResult(DefaultRecordIdCommand.fetch())
            .map(Command::getResult)
            .compose(bindViewIoToMainComposer())
            .subscribe(action, throwable -> Timber.e(throwable, ""));
   }

   public interface Screen extends WalletScreen {
      void showWalletRecord(Record record);

      void showDefaultAddress(AddressInfoWithLocale addressInfoWithLocale);

      void showDefaultCardDialog(Record defaultRecord);

      void showDeleteCardDialog();

      void showConnectionErrorDialog();

      void setDefaultCardCondition(boolean defaultCard);

      void showCardIsReadyDialog(String cardName);

      void setCardNickname(String cardNickname);

      Observable<Boolean> setAsDefaultPaymentCardCondition();

      Observable<String> getCardNicknameObservable();

      String getUpdateNickname();

      void showSCNonConnectionDialog();

      void showCardNameError();

      void hideCardNameError();

      OperationView<UpdateRecordCommand> provideOperationSaveCardData();

      void notifyCardDataIsSaved();

      OperationView<DeleteRecordAction> provideOperationDeleteRecord();

      OperationView<SetDefaultCardOnDeviceCommand> provideOperationSetDefaultOnDevice();

      OperationView<SetPaymentCardAction> provideOperationSetPaymentCardAction();
   }
}
