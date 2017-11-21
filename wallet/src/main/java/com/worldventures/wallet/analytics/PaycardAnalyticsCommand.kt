package com.worldventures.wallet.analytics

import android.text.TextUtils
import com.worldventures.janet.injection.InjectableAction
import com.worldventures.wallet.domain.entity.record.Record
import com.worldventures.wallet.service.RecordInteractor
import com.worldventures.wallet.service.WalletAnalyticsInteractor
import io.techery.janet.Command
import io.techery.janet.command.annotations.CommandAction
import javax.inject.Inject

@CommandAction
class PaycardAnalyticsCommand(private val cardDetailsWithDefaultAction: BaseCardDetailsWithDefaultAction, private val record: Record) : Command<Void>(), InjectableAction {

   @Inject lateinit var analyticsInteractor: WalletAnalyticsInteractor
   @Inject lateinit var recordInteractor: RecordInteractor

   @Throws(Throwable::class)
   override fun run(callback: Command.CommandCallback<Void>) {
      recordInteractor.defaultRecordIdPipe()
            .observeSuccessWithReplay()
            .take(1)
            .flatMap { command ->
               val isDefault = TextUtils.equals(record.id, command.result)
               cardDetailsWithDefaultAction.fillPaycardInfo(record, isDefault)
               analyticsInteractor.walletAnalyticsPipe()
                     .createObservableResult(WalletAnalyticsCommand(cardDetailsWithDefaultAction))
            }
            .map { it.result }
            .subscribe({ callback.onSuccess(it) }, { callback.onFail(it) })
   }
}
