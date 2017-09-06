package com.worldventures.dreamtrips.wallet.ui.records.detail.impl;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.worldventures.dreamtrips.core.janet.composer.ActionPipeCacheWiper;
import com.worldventures.dreamtrips.wallet.service.WalletAnalyticsInteractor;
import com.worldventures.dreamtrips.wallet.analytics.CardDetailsAction;
import com.worldventures.dreamtrips.wallet.analytics.PaycardAnalyticsCommand;
import com.worldventures.dreamtrips.wallet.analytics.WalletAnalyticsCommand;
import com.worldventures.dreamtrips.wallet.domain.entity.ConnectionStatus;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardStatus;
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
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenterImpl;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;
import com.worldventures.dreamtrips.wallet.ui.records.detail.CardDetailsPresenter;
import com.worldventures.dreamtrips.wallet.ui.records.detail.CardDetailsScreen;
import com.worldventures.dreamtrips.wallet.ui.records.detail.RecordDetailViewModel;
import com.worldventures.dreamtrips.wallet.util.WalletValidateHelper;

import io.techery.janet.ActionState;
import io.techery.janet.Command;
import io.techery.janet.operationsubscriber.OperationActionSubscriber;
import rx.Observable;
import rx.functions.Action1;
import timber.log.Timber;

import static com.worldventures.dreamtrips.wallet.analytics.ChangeDefaultCardAction.forBankCard;
import static com.worldventures.dreamtrips.wallet.util.WalletRecordUtil.equalsRecordId;
import static com.worldventures.dreamtrips.wallet.util.WalletRecordUtil.findRecord;

public class CardDetailsPresenterImpl extends WalletPresenterImpl<CardDetailsScreen> implements CardDetailsPresenter {

   private final RecordInteractor recordInteractor;
   private final WalletAnalyticsInteractor analyticsInteractor;

   private RecordDetailViewModel recordDetailViewModel;

   public CardDetailsPresenterImpl(Navigator navigator, SmartCardInteractor smartCardInteractor,
         WalletNetworkService networkService, RecordInteractor recordInteractor, WalletAnalyticsInteractor analyticsInteractor) {
      super(navigator, smartCardInteractor, networkService);
      this.recordInteractor = recordInteractor;
      this.analyticsInteractor = analyticsInteractor;
   }

   @Override
   public void attachView(CardDetailsScreen view) {
      super.attachView(view);
      recordDetailViewModel = view.getDetailViewModel();
      connectToDeleteCardPipe();
      connectToSetDefaultCardIdPipe();
      connectSetPaymentCardPipe();
      observeSaveCardData(view);
      trackScreen();
   }

   private void observeSaveCardData(CardDetailsScreen view) {
      recordInteractor.updateRecordPipe()
            .observe()
            .filter(state -> equalsRecordId(recordDetailViewModel.getRecordId(), state.action.getRecord()))
            .compose(bindViewIoToMainComposer())
            .subscribe(OperationActionSubscriber.forView(view.provideOperationSaveCardData()).create());
   }

   @Override
   public void updateNickName() {
      fetchOfflineModeState(offlineModeEnabled -> {
         if (offlineModeEnabled || getNetworkService().isAvailable()) {
            if (!recordDetailViewModel.isErrorShown() && recordDetailViewModel.isChanged()) {
               nicknameUpdated(recordDetailViewModel.getRecordName().trim());
            } else {
               getView().notifyCardDataIsSaved();
            }
         } else {
            getView().showNetworkConnectionErrorDialog();
         }
      });
   }

   private void fetchOfflineModeState(Action1<Boolean> action) {
      getSmartCardInteractor().offlineModeStatusPipe()
            .createObservable(OfflineModeStatusCommand.fetch())
            .filter(actionState -> actionState.status == ActionState.Status.SUCCESS)
            .map(actionState -> actionState.action.getResult())
            .compose(bindViewIoToMainComposer())
            .subscribe(action);
   }

   private void trackScreen() {
      fetchRecord(recordDetailViewModel.getRecordId(), record -> analyticsInteractor.paycardAnalyticsPipe()
            .send(new PaycardAnalyticsCommand(new CardDetailsAction(record.nickName()), record)));
   }

   private void connectToDeleteCardPipe() {
      recordInteractor.deleteRecordPipe()
            .observeWithReplay()
            .filter(state -> state.action.getRecordId().equals(recordDetailViewModel.getRecordId()))
            .compose(bindViewIoToMainComposer())
            .compose(new ActionPipeCacheWiper<>(recordInteractor.deleteRecordPipe()))
            .subscribe(OperationActionSubscriber.forView(getView().provideOperationDeleteRecord())
                  .onSuccess(deleteRecordAction -> getNavigator().goCardList())
                  .create());
   }

   private void connectToSetDefaultCardIdPipe() {
      recordInteractor.setDefaultCardOnDeviceCommandPipe()
            .observe()
            .compose(bindViewIoToMainComposer())
            .subscribe(OperationActionSubscriber.forView(getView().provideOperationSetDefaultOnDevice())
                  .onSuccess(command -> getView()
                        .defaultCardChanged(equalsRecordId(recordDetailViewModel.getRecordId(), command.getResult())))
                  .create());
   }

   private void connectSetPaymentCardPipe() {
      recordInteractor.setPaymentCardPipe()
            .observeWithReplay()
            .compose(bindViewIoToMainComposer())
            .compose(new ActionPipeCacheWiper<>(recordInteractor.setPaymentCardPipe()))
            .subscribe(OperationActionSubscriber.forView(getView().provideOperationSetPaymentCardAction())
                  .onSuccess(action -> getView().showCardIsReadyDialog(recordDetailViewModel.getRecordName().trim()))
                  .create());
   }

   @Override
   public void onDeleteCardClick() {
      fetchConnectionStats(connectionStatus -> {
         if (connectionStatus.isConnected()) {
            getView().showDeleteCardDialog();
         } else {
            getView().showSCNonConnectionDialog();
         }
      });
   }

   @Override
   public void payThisCard() {
      fetchConnectionStats(connectionStatus -> {
         if (connectionStatus.isConnected()) {
            recordInteractor.setPaymentCardPipe().send(new SetPaymentCardAction(recordDetailViewModel.getRecordId()));
         } else {
            getView().showSCNonConnectionDialog();
         }
      });
   }

   @Override
   public void onCardIsReadyDialogShown() {
      getNavigator().goBack();
   }

   @Override
   public void validateRecordName(String name) {
      if (WalletValidateHelper.isValidCardName(name)) {
         getView().hideCardNameError();
      } else {
         getView().showCardNameError();
      }
   }

   @Override
   public void changeDefaultCard(boolean isDefault) {
      fetchConnectionStats(connectionStatus -> {
         if (connectionStatus.isConnected()) {
            if (isDefault) {
               setCardAsDefault();
            } else {
               unsetCardAsDefault();
            }
         } else {
            getView().undoDefaultCardChanges();
            getView().showSCNonConnectionDialog();
         }
      });
   }

   @Override
   public void onDeleteCardConfirmed() {
      recordInteractor.deleteRecordPipe().send(new DeleteRecordCommand(recordDetailViewModel.getRecordId()));
   }

   private void unsetCardAsDefault() {
      if (recordDetailViewModel.getRecordModel().isDefaultCard()) {
         recordInteractor.setDefaultCardOnDeviceCommandPipe()
               .send(SetDefaultCardOnDeviceCommand.unsetDefaultCard());
      }
   }

   private void setCardAsDefault() {
      fetchDefaultRecord(this::changeDefaultRecord);
   }

   private void changeDefaultRecord(@Nullable Record defaultRecord) {
      if (defaultRecord == null) {
         applyDefaultId();
      } else {
         getView().showDefaultCardDialog(defaultRecord);
      }
   }

   private void applyDefaultId() {
      trackSetAsDefault();
      recordInteractor.setDefaultCardOnDeviceCommandPipe()
            .send(SetDefaultCardOnDeviceCommand.setAsDefault(recordDetailViewModel.getRecordId()));
   }

   @Override
   public void onChangeDefaultCardConfirmed() {
      applyDefaultId();
   }

   @Override
   public void onChangeDefaultCardCanceled() {
      getView().undoDefaultCardChanges();
   }

   private void trackSetAsDefault() {
      fetchRecord(recordDetailViewModel.getRecordId(),
            record -> analyticsInteractor.walletAnalyticsPipe()
                  .send(new WalletAnalyticsCommand(forBankCard(record))));
   }

   public void goBack() {
      getNavigator().goBack();
   }

   private void nicknameUpdated(String nickName) {
      fetchRecord(recordDetailViewModel.getRecordId(),
            record -> recordInteractor.updateRecordPipe().send(UpdateRecordCommand.updateNickName(record, nickName)));
   }

   private void fetchRecord(@NonNull String recordId, @NonNull Action1<Record> recordAction) {
      recordInteractor.cardsListPipe()
            .createObservableResult(RecordListCommand.fetch())
            .map(command -> findRecord(command.getResult(), recordId))
            .compose(bindView())
            .subscribe(recordAction, throwable -> Timber.e(throwable, ""));
   }

   private void fetchConnectionStats(Action1<ConnectionStatus> action) {
      getSmartCardInteractor().deviceStatePipe()
            .createObservableResult(DeviceStateCommand.fetch())
            .map(Command::getResult)
            .compose(bindViewIoToMainComposer())
            .map(SmartCardStatus::connectionStatus)
            .subscribe(action, throwable -> Timber.e(throwable, ""));
   }

   private void fetchDefaultRecord(Action1<Record> action) {
      recordInteractor.defaultRecordIdPipe()
            .createObservableResult(DefaultRecordIdCommand.fetch())
            .map(Command::getResult)
            .flatMap(defaultId -> {
               if (defaultId == null) {
                  return Observable.just(null);
               } else {
                  return recordInteractor.cardsListPipe()
                        .createObservableResult(RecordListCommand.fetch())
                        .map(command -> findRecord(command.getResult(), defaultId));
               }
            })
            .onErrorReturn(null)
            .compose(bindViewIoToMainComposer())
            .subscribe(action, throwable -> Timber.e(throwable, ""));
   }
}