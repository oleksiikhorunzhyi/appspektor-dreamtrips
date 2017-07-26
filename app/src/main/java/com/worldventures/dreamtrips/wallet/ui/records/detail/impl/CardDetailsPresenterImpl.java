package com.worldventures.dreamtrips.wallet.ui.records.detail.impl;


import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.core.janet.composer.ActionPipeCacheWiper;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsInteractor;
import com.worldventures.dreamtrips.util.HttpErrorHandlingUtil;
import com.worldventures.dreamtrips.wallet.analytics.CardDetailsAction;
import com.worldventures.dreamtrips.wallet.analytics.ChangeDefaultCardAction;
import com.worldventures.dreamtrips.wallet.analytics.PaycardAnalyticsCommand;
import com.worldventures.dreamtrips.wallet.analytics.WalletAnalyticsCommand;
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
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenterImpl;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;
import com.worldventures.dreamtrips.wallet.ui.dashboard.util.model.TransitionModel;
import com.worldventures.dreamtrips.wallet.ui.records.detail.CardDetailsPresenter;
import com.worldventures.dreamtrips.wallet.ui.records.detail.CardDetailsScreen;
import com.worldventures.dreamtrips.wallet.ui.records.model.RecordViewModel;
import com.worldventures.dreamtrips.wallet.util.WalletRecordUtil;
import com.worldventures.dreamtrips.wallet.util.WalletValidateHelper;

import io.techery.janet.ActionState;
import io.techery.janet.Command;
import io.techery.janet.helper.ActionStateToActionTransformer;
import io.techery.janet.operationsubscriber.OperationActionSubscriber;
import rx.functions.Action1;
import timber.log.Timber;

public class CardDetailsPresenterImpl extends WalletPresenterImpl<CardDetailsScreen> implements CardDetailsPresenter {

   private final RecordInteractor recordInteractor;
   private final AnalyticsInteractor analyticsInteractor;
   private final HttpErrorHandlingUtil httpErrorHandlingUtil;

   private Record record;

   public CardDetailsPresenterImpl(Navigator navigator, SmartCardInteractor smartCardInteractor,
         WalletNetworkService networkService, RecordInteractor recordInteractor, AnalyticsInteractor analyticsInteractor,
         HttpErrorHandlingUtil httpErrorHandlingUtil) {
      super(navigator, smartCardInteractor, networkService);
      this.recordInteractor = recordInteractor;
      this.analyticsInteractor = analyticsInteractor;
      this.httpErrorHandlingUtil = httpErrorHandlingUtil;
   }

   @Override
   public void attachView(CardDetailsScreen view) {
      super.attachView(view);
      getView().showWalletRecord(getView().getRecordViewModel());
      final TransitionModel transitionModel = getView().getTransitionModel();
      getView().animateCard(transitionModel);
      observeCardList();
      updateCardConditionState();
      connectToDeleteCardPipe();
      connectToSetDefaultCardIdPipe();
      connectSetPaymentCardPipe();
      observeCardNickName();
      observeDefaultCardSwitcher();
      observeSaveCardData();
      recordInteractor.cardsListPipe().send(RecordListCommand.fetch());
   }

   private void observeCardList() {
      recordInteractor.cardsListPipe()
            .observeWithReplay()
            .compose(new ActionPipeCacheWiper<>(recordInteractor.cardsListPipe()))
            .compose(new ActionStateToActionTransformer<>())
            .compose(bindViewIoToMainComposer())
            .map(Command::getResult)
            .subscribe(records -> {
               final RecordViewModel recordViewModel = getView().getRecordViewModel();
               this.record = Queryable.from(records).first(card -> card.id() != null && card.id().equals(recordViewModel.getId()));
               trackScreen();
            }, Timber::e);
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
      final Record resultRecord = ImmutableRecord.builder()
            .from(record)
            .nickName(updatedRecord.nickName())
            .build();
      getView().showWalletRecord(WalletRecordUtil.prepareRecordViewModel(resultRecord));
   }

   @Override
   public void updateNickName() {
      fetchOfflineModeState(offlineModeEnabled -> {
         if (offlineModeEnabled || getNetworkService().isAvailable()) {
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
      getSmartCardInteractor().offlineModeStatusPipe()
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
                  .onSuccess(deleteRecordAction -> getNavigator().goCardList())
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
      getView().getCardNicknameObservable()
            .compose(bindView())
            .subscribe(this::handleCardNickname);
   }

   private void handleCardNickname(String cardName) {
      if (WalletValidateHelper.isValidCardName(cardName)) {
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
            recordInteractor.setPaymentCardActionActionPipe().send(new SetPaymentCardAction(record));
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
   public void onDeleteCardConfirmed() {
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

   @Override
   public void onChangeDefaultCardConfirmed() {
      applyDefaultId();
   }

   @Override
   public void onChangeDefaultCardCanceled() {
      bindDefaultStatus(false);
   }

   private void trackSetAsDefault() {
      analyticsInteractor.walletAnalyticsCommandPipe()
            .send(new WalletAnalyticsCommand(ChangeDefaultCardAction.forBankCard(record)));
   }

   public void goBack() {
      getNavigator().goBack();
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
      getSmartCardInteractor().deviceStatePipe()
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

   public HttpErrorHandlingUtil httpErrorHandlingUtil() {
      return httpErrorHandlingUtil;
   }

}
