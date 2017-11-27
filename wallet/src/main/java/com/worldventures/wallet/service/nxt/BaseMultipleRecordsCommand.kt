package com.worldventures.wallet.service.nxt

import com.worldventures.janet.injection.InjectableAction
import com.worldventures.wallet.di.JanetNxtModule.JANET_NXT
import com.worldventures.wallet.domain.entity.record.Record
import com.worldventures.wallet.service.nxt.MultifunctionNxtHttpAction.MULTIFUNCTION_REQUEST_ELEMENTS_LIMIT
import com.worldventures.wallet.service.nxt.model.MultiErrorResponse
import com.worldventures.wallet.service.nxt.model.MultiRequestBody
import com.worldventures.wallet.service.nxt.model.MultiRequestElement
import com.worldventures.wallet.service.nxt.model.MultiResponseBody
import com.worldventures.wallet.service.nxt.util.NxtBankCardHelper
import com.worldventures.wallet.service.nxt.util.NxtRecord
import com.worldventures.wallet.util.NxtMultifunctionException
import io.techery.janet.Command
import io.techery.janet.Janet
import rx.Observable
import rx.schedulers.Schedulers
import java.util.ArrayList
import javax.inject.Inject
import javax.inject.Named

abstract class BaseMultipleRecordsCommand internal constructor(records: List<Record>,
                                                               private val skipTokenizationErrors: Boolean,
                                                               private val tokenize: Boolean) : Command<List<Record>>(), InjectableAction {

   @field:[Inject Named(JANET_NXT)] lateinit var janet: Janet

   private val records = ArrayList<Record>(records)
   private val recordsProcessedWithErrors = ArrayList<Record>()

   @Throws(Exception::class)
   override fun run(callback: Command.CommandCallback<List<Record>>) {
      val requestElements = records
            .map { this.prepareMultiRequestElements(it) }
            .fold(emptyList<MultiRequestElement>(), { l, r -> l + r })
      if (requestElements.isEmpty()) {
         callback.onSuccess(emptyList())
         return
      }

      Observable.from<MultiRequestElement>(requestElements)
            .buffer(MULTIFUNCTION_REQUEST_ELEMENTS_LIMIT)
            .map {
               janet.createPipe(MultifunctionNxtHttpAction::class.java, Schedulers.io())
                     .createObservableResult(MultifunctionNxtHttpAction(createRequestBody(it)))
            }
            .reduce { obj, t1 -> obj.concatWith(t1) }
            .flatMap { it }
            .map { it.getResponse() }
            .toList()
            .map({ nxtResponses -> createResponseBody(this.records, nxtResponses) })
            .flatMap(handleMultipleRecordsResult())
            .subscribe({ callback.onSuccess(it) }, { callback.onFail(it) })
   }

   internal abstract fun prepareMultiRequestElements(record: Record): List<MultiRequestElement>

   private fun createRequestBody(requestElements: List<MultiRequestElement>) = MultiRequestBody(multiRequestElements = requestElements)

   internal abstract fun createResponseBody(records: List<Record>, nxtResponses: List<MultiResponseBody>): List<NxtRecord>

   private fun handleMultipleRecordsResult(): (List<NxtRecord>) -> Observable<List<Record>> {
      return lambda@ { nxtRecords ->
         val resultsProcessedWithErrors = nxtRecords.filter { nxtRecord -> !nxtRecord.responseErrors.isEmpty() }
         recordsProcessedWithErrors.addAll(
               resultsProcessedWithErrors
                     .map { nxtRecord -> if (tokenize) nxtRecord.tokenizedRecord else nxtRecord.detokenizedRecord }
         )

         if (skipTokenizationErrors || resultsProcessedWithErrors.isEmpty()) {
            return@lambda Observable.from<NxtRecord>(nxtRecords)
                  .filter { nxtRecord -> nxtRecord.responseErrors.isEmpty() }
                  .map { nxtRecord -> if (tokenize) nxtRecord.tokenizedRecord else nxtRecord.detokenizedRecord }
                  .toList()
         } else {
            val errorResponses = resultsProcessedWithErrors
                  .map { it.responseErrors }
                  .fold(emptyList<MultiErrorResponse>(), { l, r -> l + r })
            return@lambda Observable.error<List<Record>>(
                  NxtMultifunctionException(NxtBankCardHelper.getResponseErrorMessage(errorResponses))
            )
         }
      }
   }

   fun getRecordsProcessedWithErrors(): List<Record> {
      return recordsProcessedWithErrors
   }

}
