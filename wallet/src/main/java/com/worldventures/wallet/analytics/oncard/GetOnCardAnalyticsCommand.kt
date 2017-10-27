package com.worldventures.wallet.analytics.oncard

import com.worldventures.janet.injection.InjectableAction
import com.worldventures.wallet.di.WalletJanetModule.JANET_WALLET
import com.worldventures.wallet.util.TimeUtils
import com.worldventures.wallet.util.WalletBuildConfigHelper
import io.techery.janet.Command
import io.techery.janet.Janet
import io.techery.janet.command.annotations.CommandAction
import io.techery.janet.smartcard.action.support.GetCardAnalyticLogsAction
import io.techery.janet.smartcard.model.analytics.AnalyticsLog
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Named

@CommandAction
class GetOnCardAnalyticsCommand : Command<List<AnalyticsLog>>(), InjectableAction {

   @field:[Inject Named(JANET_WALLET)] lateinit var janet: Janet
   @Inject lateinit var walletBuildConfigHelper: WalletBuildConfigHelper

   @Throws(Throwable::class)
   override fun run(callback: Command.CommandCallback<List<AnalyticsLog>>) {
      janet.createPipe(GetCardAnalyticLogsAction::class.java)
            .createObservableResult(GetCardAnalyticLogsAction.request(CLEAR_LOGS))
            .map { it.analyticsLogs }
            .doOnNext { this.printLogs(it) }
            .subscribe({ callback.onSuccess(it) }, { callback.onFail(it) })
   }

   private fun printLogs(analyticsLogs: List<AnalyticsLog>) {
      if (walletBuildConfigHelper.isDebug) {
         Timber.d("On-card analytics logs:")
         analyticsLogs.forEach { Timber.d("%s, Formatted time = [%s]", it, TimeUtils.formatToIso(it.timestampMillis())) }
      }
   }

   companion object {

      private val CLEAR_LOGS = true
   }

}