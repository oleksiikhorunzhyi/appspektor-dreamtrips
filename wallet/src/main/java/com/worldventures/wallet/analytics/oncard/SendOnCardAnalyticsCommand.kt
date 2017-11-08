package com.worldventures.wallet.analytics.oncard

import com.worldventures.janet.injection.InjectableAction
import com.worldventures.wallet.analytics.oncard.action.SmartCardAnalyticsAction
import com.worldventures.wallet.analytics.oncard.action.SmartCardPaymentAction
import com.worldventures.wallet.analytics.oncard.action.SmartCardUserAction
import com.worldventures.wallet.domain.storage.disk.RecordsStorage
import com.worldventures.wallet.service.WalletAnalyticsInteractor
import com.worldventures.wallet.service.WalletSocialInfoProvider
import io.techery.janet.Command
import io.techery.janet.command.annotations.CommandAction
import io.techery.janet.smartcard.model.analytics.AnalyticsLog
import rx.Observable
import javax.inject.Inject

@CommandAction
class SendOnCardAnalyticsCommand(private val analyticsLogs: List<AnalyticsLog>) : Command<Void>(), InjectableAction {

   @Inject lateinit var analyticsInteractor: WalletAnalyticsInteractor
   @Inject lateinit var recordsStorage: RecordsStorage
   @Inject lateinit var socialInfoProvider: WalletSocialInfoProvider

   @Throws(Throwable::class)
   override fun run(callback: Command.CommandCallback<Void>) {
      if (analyticsLogs.isEmpty()) {
         callback.onSuccess(null)
         return
      }

      Observable.from(analyticsLogs)
            .map { SmartCardAnalyticsAction.from(it) }
            .filter { analyticsAction -> analyticsAction != null }
            .doOnNext { analyticsAction ->
               if (analyticsAction is SmartCardPaymentAction) {
                  fillRecordDetails(analyticsAction)
               } else if (analyticsAction is SmartCardUserAction) {
                  fillUserDetails(analyticsAction)
               }
            }
            .doOnNext { analyticsAction -> analyticsInteractor.analyticsActionPipe().send(analyticsAction) }
            .toList()
            .map<Void> { _ -> null }
            .subscribe(callback::onSuccess, callback::onFail)
   }

   private fun fillRecordDetails(analyticsAction: SmartCardPaymentAction) {
      val paymentCardId = analyticsAction.recordId
      if (paymentCardId >= 0) {
         val storedRecordId = paymentCardId.toString()
         analyticsAction.setRecord(recordsStorage.readRecords().find { record -> storedRecordId == record.id })
      }
   }

   private fun fillUserDetails(analyticsAction: SmartCardUserAction) {
      analyticsAction.setUserId(socialInfoProvider.userId()!!)
   }
}
