package com.worldventures.wallet.service.command.record

import com.worldventures.janet.injection.InjectableAction
import com.worldventures.wallet.analytics.tokenization.ActionType
import com.worldventures.wallet.analytics.tokenization.TokenizationAnalyticsLocationCommand
import com.worldventures.wallet.analytics.tokenization.TokenizationCardAction
import com.worldventures.wallet.domain.entity.record.Record
import com.worldventures.wallet.domain.storage.disk.RecordsStorage
import com.worldventures.wallet.service.WalletAnalyticsInteractor
import com.worldventures.wallet.service.nxt.BaseMultipleRecordsCommand
import com.worldventures.wallet.service.nxt.DetokenizeMultipleRecordsCommand
import com.worldventures.wallet.service.nxt.NxtInteractor
import com.worldventures.wallet.service.nxt.TokenizeMultipleRecordsCommand
import com.worldventures.wallet.util.WalletFeatureHelper
import io.techery.janet.ActionState
import io.techery.janet.Command
import io.techery.janet.command.annotations.CommandAction
import io.techery.janet.helper.ActionStateToActionTransformer
import rx.Observable
import java.util.ArrayList
import javax.inject.Inject

@CommandAction
class SecureMultipleRecordsCommand private constructor(private val records: List<Record>, private val secureForLocalStorage: Boolean,
                                                       private val skipTokenizationErrors: Boolean, private val actionType: ActionType?) : Command<List<Record>>(), InjectableAction {

   @Inject lateinit var nxtInteractor: NxtInteractor
   @Inject lateinit var analyticsInteractor: WalletAnalyticsInteractor
   @Inject lateinit var recordsStorage: RecordsStorage
   @Inject lateinit var featureHelper: WalletFeatureHelper

   @Throws(Throwable::class)
   override fun run(callback: Command.CommandCallback<List<Record>>) {
      val offlineModeEnabled = featureHelper.offlineModeState(recordsStorage.readOfflineModeState())

      if (offlineModeEnabled) {
         callback.onSuccess(records)
      } else {
         if (secureForLocalStorage) {
            tokenizeRecords(records).subscribe({ callback.onSuccess(it) }) { callback.onFail(it) }
         } else {
            detokenizeRecords(records).subscribe({ callback.onSuccess(it) }) { callback.onFail(it) }
         }
      }
   }

   private fun tokenizeRecords(records: List<Record>): Observable<List<Record>> {
      return nxtInteractor.tokenizeMultipleRecordsPipe()
            .createObservable(TokenizeMultipleRecordsCommand(records, skipTokenizationErrors))
            .doOnNext(processResultForAnalytics())
            .compose(ActionStateToActionTransformer())
            .map { it.result }
   }

   private fun detokenizeRecords(records: List<Record>): Observable<List<Record>> {
      return nxtInteractor.detokenizeMultipleRecordsPipe()
            .createObservable(DetokenizeMultipleRecordsCommand(records, skipTokenizationErrors))
            .doOnNext(processResultForAnalytics())
            .compose(ActionStateToActionTransformer())
            .map { it.result }
   }

   private fun processResultForAnalytics(): (ActionState<out BaseMultipleRecordsCommand>) -> Unit {
      return { actionState ->
         if (actionState.status == ActionState.Status.SUCCESS) {
            sendTokenizationAnalytics(actionState.action.result, true)
         }
         if (actionState.status == ActionState.Status.FAIL || actionState.status == ActionState.Status.SUCCESS) {
            sendTokenizationAnalytics(actionState.action.getRecordsProcessedWithErrors(), false)
         }
      }
   }

   private fun sendTokenizationAnalytics(records: List<Record>, success: Boolean) {
      if (actionType == null || records.isEmpty()) {
         return
      }

      records.forEach { recordWithError ->
         analyticsInteractor.walletAnalyticsPipe()
               .send(TokenizationAnalyticsLocationCommand(
                     TokenizationCardAction.from(recordWithError, success, actionType, secureForLocalStorage)
               ))
      }
   }

   class Builder private constructor(records: List<Record>, private val secureForLocalStorage: Boolean) {

      private val records = ArrayList<Record>()

      private var skipTokenizationErrors: Boolean = false
      private var actionType: ActionType? = null

      init {
         this.records.addAll(records)
      }

      fun withAnalyticsActionType(actionType: ActionType): Builder {
         this.actionType = actionType
         return this
      }

      fun skipTokenizationErrors(skipTokenizationErrors: Boolean): Builder {
         this.skipTokenizationErrors = skipTokenizationErrors
         return this
      }

      fun create(): SecureMultipleRecordsCommand {
         return SecureMultipleRecordsCommand(records, secureForLocalStorage, skipTokenizationErrors, actionType)
      }

      companion object {

         fun prepareRecordForLocalStorage(records: List<Record>): Builder {
            return Builder(records, true)
         }

         fun prepareRecordForSmartCard(records: List<Record>): Builder {
            return Builder(records, false)
         }
      }

   }

}
