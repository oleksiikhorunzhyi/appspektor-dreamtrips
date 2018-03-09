package com.worldventures.wallet.ui.records.detail.impl

import com.worldventures.core.janet.composer.ActionPipeCacheWiper
import com.worldventures.wallet.analytics.CardDetailsAction
import com.worldventures.wallet.analytics.ChangeDefaultCardAction
import com.worldventures.wallet.analytics.PaycardAnalyticsCommand
import com.worldventures.wallet.analytics.WalletAnalyticsCommand
import com.worldventures.wallet.domain.entity.ConnectionStatus
import com.worldventures.wallet.domain.entity.record.Record
import com.worldventures.wallet.service.RecordInteractor
import com.worldventures.wallet.service.SmartCardInteractor
import com.worldventures.wallet.service.WalletAnalyticsInteractor
import com.worldventures.wallet.service.command.RecordListCommand
import com.worldventures.wallet.service.command.SetDefaultCardOnDeviceCommand
import com.worldventures.wallet.service.command.SetPaymentCardAction
import com.worldventures.wallet.service.command.device.DeviceStateCommand
import com.worldventures.wallet.service.command.offline_mode.OfflineModeStatusCommand
import com.worldventures.wallet.service.command.record.DefaultRecordIdCommand
import com.worldventures.wallet.service.command.record.DeleteRecordCommand
import com.worldventures.wallet.service.command.record.UpdateRecordCommand
import com.worldventures.wallet.ui.common.base.WalletDeviceConnectionDelegate
import com.worldventures.wallet.ui.common.base.WalletNetworkDelegate
import com.worldventures.wallet.ui.common.base.WalletPresenterImpl
import com.worldventures.wallet.ui.common.navigation.Navigator
import com.worldventures.wallet.ui.records.detail.CardDetailsPresenter
import com.worldventures.wallet.ui.records.detail.CardDetailsScreen
import com.worldventures.wallet.ui.records.detail.DefaultRecordDetail
import com.worldventures.wallet.util.WalletRecordUtil
import com.worldventures.wallet.util.WalletRecordUtil.Companion.bankNameWithCardNumber
import com.worldventures.wallet.util.WalletValidateHelper
import io.techery.janet.ActionState
import io.techery.janet.operationsubscriber.OperationActionSubscriber
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import timber.log.Timber

class CardDetailsPresenterImpl(navigator: Navigator,
                               deviceConnectionDelegate: WalletDeviceConnectionDelegate,
                               private val smartCardInteractor: SmartCardInteractor,
                               private val networkDelegate: WalletNetworkDelegate,
                               private val recordInteractor: RecordInteractor,
                               private val analyticsInteractor: WalletAnalyticsInteractor)
   : WalletPresenterImpl<CardDetailsScreen>(navigator, deviceConnectionDelegate), CardDetailsPresenter {

   override fun attachView(view: CardDetailsScreen) {
      super.attachView(view)
      networkDelegate.setup(view)
   }

   override fun observeRecordChanges(recordId: String) {
      connectToDeleteCardPipe(view, recordId)
      observeDefaultRecord()
      connectToSetDefaultCardIdPipe()
      connectSetPaymentCardPipe()
      observeSaveCardData(view, recordId)
      trackScreen(recordId)
   }

   private fun observeSaveCardData(view: CardDetailsScreen, recordId: String) {
      recordInteractor.updateRecordPipe()
            .observe()
            .filter { state ->
               WalletRecordUtil.equalsRecordId(recordId, state.action.record)
            }
            .compose(view.bindUntilDetach())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(OperationActionSubscriber.forView(view.provideOperationSaveCardData())
                  .onSuccess { view.notifyRecordDataIsSaved(it.record.nickname) }
                  .create())
   }

   private fun observeDefaultRecord() {
      recordInteractor.defaultRecordIdPipe()
            .observeSuccess()
            .map { it.result }
            .flatMap { findRecordById(it) }
            .map { convertToDefaultRecordDetail(it) }
            .compose(view.bindUntilDetach())
            .subscribe({ view.defaultRecordDetails = it }, { Timber.e(it) })
   }

   private fun findRecordById(recordId: String?): Observable<Record?> =
         if (recordId != null) {
            recordInteractor.cardsListPipe()
                  .createObservableResult(RecordListCommand.fetch())
                  .map { WalletRecordUtil.findRecord(it.result, recordId) }
         } else {
            Observable.just(null)
         }

   @Suppress("UnsafeCallOnNullableType")
   private fun convertToDefaultRecordDetail(record: Record?): DefaultRecordDetail? =
         record?.let { DefaultRecordDetail(recordId = record.id!!, recordName = bankNameWithCardNumber(record)) }

   override fun fetchDefaultRecord() {
      recordInteractor.defaultRecordIdPipe().send(DefaultRecordIdCommand.fetch())
   }

   override fun updateNickname(recordId: String, nickname: String) {
      fetchOfflineModeState { offlineModeEnabled ->
         if (offlineModeEnabled || networkDelegate.isAvailable) {
            sendUpdateNicknameCommand(recordId, nickname)
         } else {
            view.showNetworkConnectionErrorDialog()
         }
      }
   }

   private fun fetchOfflineModeState(action: (Boolean) -> Unit) {
      smartCardInteractor.offlineModeStatusPipe()
            .createObservable(OfflineModeStatusCommand.fetch())
            .filter { actionState -> actionState.status == ActionState.Status.SUCCESS }
            .map { actionState -> actionState.action.result }
            .compose(view.bindUntilDetach())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(action)
   }

   private fun trackScreen(recordId: String) {
      fetchRecord(recordId, { record ->
         analyticsInteractor.paycardAnalyticsPipe()
               .send(PaycardAnalyticsCommand(CardDetailsAction(record.nickname), record))
      })
   }

   private fun connectToDeleteCardPipe(view: CardDetailsScreen, recordId: String) {
      recordInteractor.deleteRecordPipe()
            .observeWithReplay()
            .filter { state -> state.action.recordId == recordId }
            .compose(view.bindUntilDetach())
            .observeOn(AndroidSchedulers.mainThread())
            .compose(ActionPipeCacheWiper(recordInteractor.deleteRecordPipe()))
            .subscribe(OperationActionSubscriber.forView(view.provideOperationDeleteRecord())
                  .onSuccess { navigator.goCardList() }
                  .create())
   }

   private fun connectToSetDefaultCardIdPipe() {
      val view = this.view
      recordInteractor.setDefaultCardOnDeviceCommandPipe()
            .observe()
            .compose(view.bindUntilDetach())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(OperationActionSubscriber.forView(view.provideOperationSetDefaultOnDevice()).create())
   }

   private fun connectSetPaymentCardPipe() {
      val view = this.view
      recordInteractor.setPaymentCardPipe()
            .observeWithReplay()
            .compose(view.bindUntilDetach())
            .observeOn(AndroidSchedulers.mainThread())
            .compose(ActionPipeCacheWiper(recordInteractor.setPaymentCardPipe()))
            .subscribe(OperationActionSubscriber.forView(view.provideOperationSetPaymentCardAction())
                  .onSuccess { view.showCardIsReadyDialog() }
                  .create())
   }

   override fun onDeleteCardClick() {
      fetchConnectionStats {
         if (it.isConnected) {
            view.showDeleteCardDialog()
         } else {
            view.showSCNonConnectionDialog()
         }
      }
   }

   override fun payThisCard(recordId: String) {
      fetchConnectionStats {
         if (it.isConnected) {
            recordInteractor.setPaymentCardPipe().send(SetPaymentCardAction(recordId))
         } else {
            view.showSCNonConnectionDialog()
         }
      }
   }

   override fun onCardIsReadyDialogShown() {
      navigator.goBack()
   }

   override fun validateRecordName(name: String) {
      val validName = WalletValidateHelper.isValidCardName(name)
      view.cardNameErrorVisible = !validName
      view.isSaveButtonEnabled = validName && view.isDataChanged
   }

   override fun changeDefaultCard(isDefault: Boolean, recordId: String, defaultRecordDetail: DefaultRecordDetail?) {
      fetchConnectionStats { connectionStatus ->
         if (connectionStatus.isConnected) {
            if (isDefault) {
               setCardAsDefault(recordId, defaultRecordDetail)
            } else {
               unsetCardAsDefault(recordId, defaultRecordDetail)
            }
         } else {
            view.undoDefaultCardChanges()
            view.showSCNonConnectionDialog()
         }
      }
   }

   override fun onDeleteCardConfirmed(recordId: String) {
      recordInteractor.deleteRecordPipe().send(DeleteRecordCommand(recordId))
   }

   private fun unsetCardAsDefault(recordId: String, defaultRecordDetail: DefaultRecordDetail?) {
      if (recordId == defaultRecordDetail?.recordId) {
         recordInteractor.setDefaultCardOnDeviceCommandPipe()
               .send(SetDefaultCardOnDeviceCommand.unsetDefaultCard())
      }
   }

   private fun setCardAsDefault(recordId: String, defaultRecordDetail: DefaultRecordDetail?) {
      if (defaultRecordDetail == null) {
         applyDefaultId(recordId)
      } else {
         view.showDefaultCardDialog()
      }
   }

   private fun applyDefaultId(recordId: String) {
      trackSetAsDefault(recordId)
      recordInteractor.setDefaultCardOnDeviceCommandPipe()
            .send(SetDefaultCardOnDeviceCommand.setAsDefault(recordId))
   }

   override fun onChangeDefaultCardConfirmed(recordId: String) {
      applyDefaultId(recordId)
   }

   override fun onChangeDefaultCardCanceled() {
      view.undoDefaultCardChanges()
   }

   private fun trackSetAsDefault(recordId: String) {
      fetchRecord(recordId) {
         analyticsInteractor.walletAnalyticsPipe().send(WalletAnalyticsCommand(ChangeDefaultCardAction(it)))
      }
   }

   override fun goBack() {
      navigator.goBack()
   }

   private fun sendUpdateNicknameCommand(recordId: String, nickName: String) {
      fetchConnectionStats { connectionStatus ->
         if (connectionStatus.isConnected) {
            fetchRecord(recordId) {
               recordInteractor.updateRecordPipe().send(UpdateRecordCommand.updateNickname(it, nickName))
            }
         } else {
            view.showSCNonConnectionDialog()
         }
      }
   }

   private fun fetchRecord(recordId: String, recordAction: (Record) -> Unit) {
      recordInteractor.cardsListPipe()
            .createObservableResult(RecordListCommand.fetch())
            .map { WalletRecordUtil.findRecord(it.result, recordId) }
            .compose(view.bindUntilDetach())
            .flatMap { if (it != null) Observable.just(it) else Observable.empty() }
            .subscribe(recordAction, { Timber.e(it) })
   }

   private fun fetchConnectionStats(action: (ConnectionStatus) -> Unit) {
      smartCardInteractor.deviceStatePipe()
            .createObservableResult(DeviceStateCommand.fetch())
            .map { it.result }
            .compose(view.bindUntilDetach())
            .observeOn(AndroidSchedulers.mainThread())
            .map { it.connectionStatus }
            .subscribe(action, { Timber.e(it) })
   }
}
