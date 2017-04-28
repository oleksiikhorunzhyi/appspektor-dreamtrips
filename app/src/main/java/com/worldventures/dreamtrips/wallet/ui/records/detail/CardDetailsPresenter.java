package com.worldventures.dreamtrips.wallet.ui.records.detail;

import android.content.Context;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.innahema.collections.query.queriables.Queryable;
import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.core.janet.composer.ActionPipeCacheWiper;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsInteractor;
import com.worldventures.dreamtrips.wallet.analytics.CardDetailsAction;
import com.worldventures.dreamtrips.wallet.analytics.ChangeDefaultCardAction;
import com.worldventures.dreamtrips.wallet.analytics.PaycardAnalyticsCommand;
import com.worldventures.dreamtrips.wallet.analytics.WalletAnalyticsCommand;
import com.worldventures.dreamtrips.wallet.domain.entity.AddressInfo;
import com.worldventures.dreamtrips.wallet.domain.entity.ConnectionStatus;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardStatus;
import com.worldventures.dreamtrips.wallet.domain.entity.record.ImmutableRecord;
import com.worldventures.dreamtrips.wallet.domain.entity.record.Record;
import com.worldventures.dreamtrips.wallet.service.RecordInteractor;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.service.WalletNetworkService;
import com.worldventures.dreamtrips.wallet.service.command.RecordListCommand;
import com.worldventures.dreamtrips.wallet.service.command.SetDefaultCardOnDeviceCommand;
import com.worldventures.dreamtrips.wallet.service.command.SetPaymentCardAction;
import com.worldventures.dreamtrips.wallet.service.command.device.DeviceStateCommand;
import com.worldventures.dreamtrips.wallet.service.command.offline_mode.OfflineModeStatusCommand;
import com.worldventures.dreamtrips.wallet.service.command.record.DefaultRecordIdCommand;
import com.worldventures.dreamtrips.wallet.service.command.record.DeleteRecordCommand;
import com.worldventures.dreamtrips.wallet.service.command.record.UpdateRecordCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;
import com.worldventures.dreamtrips.wallet.ui.records.address.EditBillingAddressPath;
import com.worldventures.dreamtrips.wallet.util.WalletRecordUtil;
import com.worldventures.dreamtrips.wallet.util.WalletValidateHelper;

import javax.inject.Inject;

import io.techery.janet.ActionState;
import io.techery.janet.Command;
import io.techery.janet.operationsubscriber.OperationActionSubscriber;
import io.techery.janet.operationsubscriber.view.OperationView;
import rx.Observable;
import rx.functions.Action1;
import timber.log.Timber;

public class CardDetailsPresenter extends WalletPresenter<CardDetailsPresenter.Screen, Parcelable> {

   @Inject Navigator navigator;
   @Inject SmartCardInteractor smartCardInteractor;
   @Inject RecordInteractor recordInteractor;
   @Inject AnalyticsInteractor analyticsInteractor;
   @Inject WalletNetworkService networkService;

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

      fetchAddressByRecordId(record.id());
      updateCardConditionState();
      connectToDeleteCardPipe();
      connectToSetDefaultCardIdPipe();
      connectSetPaymentCardPipe();
      observeCardNickName();
      observeDefaultCardSwitcher();
      observeSaveCardData();
   }

   private void fetchAddressByRecordId(final String recordId) {
      recordInteractor.cardsListPipe()
            .createObservableResult(RecordListCommand.fetch())
            .compose(bindViewIoToMainComposer())
            .map(command -> Queryable.from(command.getResult()).first(element -> element.id().equals(recordId)))
            .subscribe(record -> {
                     this.record = record;
                     getView().showDefaultAddress(record.addressInfo()); },
                  throwable -> Timber.e(throwable, ""));
   }

   private void observeSaveCardData() {
      recordInteractor.updateRecordPipe()
            .observe()
            .filter(state -> record.id().equals(state.action.getRecord().id()))
            .compose(bindViewIoToMainComposer())
            .subscribe(OperationActionSubscriber.forView(getView().provideOperationSaveCardData())
                  .onSuccess(command -> updateRecordVar(command.getRecord()))
                  .create());
   }

   private void updateRecordVar(Record updatedRecord) {
      record = ImmutableRecord.builder()
            .from(record)
            .nickName(updatedRecord.nickName())
            .addressInfo(updatedRecord.addressInfo())
            .build();
   }

   void updateNickName() {
      fetchOfflineModeState(offlineModeEnabled -> {
         if (offlineModeEnabled || networkService.isAvailable()) {
            if (!TextUtils.equals(getView().getUpdateNickname(), record.nickName())) {
               nicknameUpdated(getView().getUpdateNickname());
            } else {
               getView().notifyCardDataIsSaved();
            }
         } else {
            getView().showNetworkConnectionErrorDialog();
         }
      });
   }

   private void fetchOfflineModeState(Action1<Boolean> action) {
      smartCardInteractor.offlineModeStatusPipe()
            .createObservable(OfflineModeStatusCommand.fetch())
            .filter(actionState -> actionState.status == ActionState.Status.SUCCESS)
            .map(actionState -> actionState.action.getResult())
            .compose(bindViewIoToMainComposer())
            .subscribe(action);
   }

   private void trackScreen() {
      analyticsInteractor.paycardAnalyticsCommandPipe()
            .send(new PaycardAnalyticsCommand(new CardDetailsAction(record.nickName()), record));
   }

   private void connectToDeleteCardPipe() {
      recordInteractor.deleteRecordPipe()
            .observeWithReplay()
            .filter(state -> state.action.getRecordId().equals(record.id()))
            .compose(bindViewIoToMainComposer())
            .compose(new ActionPipeCacheWiper<>(recordInteractor.deleteRecordPipe()))
            .subscribe(OperationActionSubscriber.forView(getView().provideOperationDeleteRecord())
                  .onSuccess(deleteRecordAction -> navigator.goBack())
                  .create());
   }

   private void connectToSetDefaultCardIdPipe() {
      recordInteractor.setDefaultCardOnDeviceCommandPipe()
            .observe()
            .compose(bindViewIoToMainComposer())
            .subscribe(OperationActionSubscriber.forView(getView().provideOperationSetDefaultOnDevice())
                  .create());
   }

   private void connectSetPaymentCardPipe() {
      recordInteractor.setPaymentCardActionActionPipe()
            .observeWithReplay()
            .compose(bindViewIoToMainComposer())
            .compose(new ActionPipeCacheWiper<>(recordInteractor.setPaymentCardActionActionPipe()))
            .subscribe(OperationActionSubscriber.forView(getView().provideOperationSetPaymentCardAction())
                  .onSuccess(action -> getView().showCardIsReadyDialog(record.nickName()))
                  .create());
   }

   private void observeCardNickName() {
      final Screen view = getView();
      view.getCardNicknameObservable()
            .compose(bindView())
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

   private void observeDefaultCardSwitcher() {
      //noinspection ConstantConditions
      getView().setAsDefaultPaymentCardCondition()
            .compose(bindView())
            .subscribe(this::defaultCardSwitcherChanged);
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
            recordInteractor.setPaymentCardActionActionPipe().send(new SetPaymentCardAction(record));
         } else {
            getView().showSCNonConnectionDialog();
         }
      });
   }

   void onCardIsReadyDialogShown() {
      navigator.goBack();
   }

   void onDeleteCardConfirmed() {
      recordInteractor.deleteRecordPipe().send(new DeleteRecordCommand(record.id()));
   }

   private void unsetCardAsDefault() {
      fetchDefaultRecordId(defaultRecordId -> {
         if (WalletRecordUtil.equals(defaultRecordId, record)) {
            recordInteractor.setDefaultCardOnDeviceCommandPipe()
                  .send(SetDefaultCardOnDeviceCommand.unsetDefaultCard());
         }
      });
   }

   private void setCardAsDefault() {
      fetchDefaultRecordId(this::changeDefaultRecord);
   }

   private void changeDefaultRecord(@Nullable String defaultRecordId) {
      if (defaultRecordId == null) {
         applyDefaultId();
      } else {
         recordInteractor.cardsListPipe()
               .createObservable(RecordListCommand.fetch())
               .filter(actionState -> actionState.status == ActionState.Status.SUCCESS)
               .map(actionState -> Queryable.from(actionState.action.getResult())
                     .firstOrDefault(c -> TextUtils.equals(c.id(), defaultRecordId)))
               .compose(bindViewIoToMainComposer())
               .subscribe(this::showChangeDefaultIdConfirmDialog);
      }
   }

   private void applyDefaultId() {
      trackSetAsDefault();
      recordInteractor.setDefaultCardOnDeviceCommandPipe()
            .send(SetDefaultCardOnDeviceCommand.setAsDefault(record.id()));
   }

   private void showChangeDefaultIdConfirmDialog(Record record) {
      if (record != null) {
         //noinspection ConstantConditions
         getView().showDefaultCardDialog(record);
      } else {
         applyDefaultId();
      }
   }

   void onChangeDefaultCardConfirmed() {
      applyDefaultId();
   }

   void onChangeDefaultCardCanceled() {
      bindDefaultStatus(false);
   }

   private void trackSetAsDefault() {
      analyticsInteractor.walletAnalyticsCommandPipe()
            .send(new WalletAnalyticsCommand(ChangeDefaultCardAction.forBankCard(record)));
   }

   public void goBack() {
      navigator.goBack();
   }

   private void nicknameUpdated(String nickName) {
      recordInteractor.updateRecordPipe().send(UpdateRecordCommand.updateNickName(record, nickName));
   }

   private void defaultCardSwitcherChanged(boolean setDefaultCard) {
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
      fetchDefaultRecordId(defaultRecordId -> bindDefaultStatus(WalletRecordUtil.equals(defaultRecordId, record)));
   }

   private void bindDefaultStatus(boolean isDefault) {
      //noinspection ConstantConditions
      getView().setDefaultCardCondition(isDefault);
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
      recordInteractor.defaultRecordIdPipe()
            .createObservableResult(DefaultRecordIdCommand.fetch())
            .map(Command::getResult)
            .compose(bindViewIoToMainComposer())
            .subscribe(action, throwable -> Timber.e(throwable, ""));
   }

   public interface Screen extends WalletScreen {
      void showWalletRecord(Record record);

      void showDefaultAddress(AddressInfo addressInfo);

      void showDefaultCardDialog(Record defaultRecord);

      void showDeleteCardDialog();

      void showConnectionErrorDialog();

      void showNetworkConnectionErrorDialog();

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

      OperationView<DeleteRecordCommand> provideOperationDeleteRecord();

      OperationView<SetDefaultCardOnDeviceCommand> provideOperationSetDefaultOnDevice();

      OperationView<SetPaymentCardAction> provideOperationSetPaymentCardAction();
   }
}
