package com.worldventures.wallet.service.nxt

import com.worldventures.janet.injection.InjectableAction
import com.worldventures.wallet.di.JanetNxtModule.JANET_NXT
import com.worldventures.wallet.domain.entity.record.Record
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
import javax.inject.Inject
import javax.inject.Named

abstract class BaseRecordCommand internal constructor(private val record: Record,
                                                      private val tokenize: Boolean) : Command<Record>(), InjectableAction {
   @field:[Inject Named(JANET_NXT)] lateinit var janet: Janet

   @Throws(Exception::class)
   override fun run(callback: Command.CommandCallback<Record>) {
      janet.createPipe(MultifunctionNxtHttpAction::class.java, Schedulers.io())
            .createObservableResult(MultifunctionNxtHttpAction(createRequestBody()))
            .map { actionResponse -> createResponseBody(this.record, actionResponse.getResponse()) }
            .flatMap(handleRecordResult())
            .subscribe({ callback.onSuccess(it) }, { callback.onFail(it) })
   }

   private fun createRequestBody(): MultiRequestBody {
      return MultiRequestBody(multiRequestElements = prepareMultiRequestElements(record))
   }

   internal abstract fun prepareMultiRequestElements(record: Record): List<MultiRequestElement>

   internal abstract fun createResponseBody(record: Record, nxtResponse: MultiResponseBody): NxtRecord

   private fun handleRecordResult(): (NxtRecord) -> Observable<Record> {
      return { nxtRecord ->
         if (nxtRecord.responseErrors.isEmpty()) {
            Observable.just<Record>(if (tokenize) nxtRecord.tokenizedRecord else nxtRecord.detokenizedRecord)
         } else {
            Observable.error<Record>(NxtMultifunctionException(
                  NxtBankCardHelper.getResponseErrorMessage(nxtRecord.responseErrors)))
         }
      }
   }

}
