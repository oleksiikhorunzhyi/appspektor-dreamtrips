package com.worldventures.wallet.analytics.oncard;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.janet.injection.InjectableAction;
import com.worldventures.wallet.util.TimeUtils;
import com.worldventures.wallet.util.WalletBuildConfigHelper;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import io.techery.janet.Command;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import io.techery.janet.smartcard.action.support.GetCardAnalyticLogsAction;
import io.techery.janet.smartcard.model.analytics.AnalyticsLog;
import timber.log.Timber;

import static com.worldventures.wallet.di.WalletJanetModule.JANET_WALLET;

@CommandAction
public class GetOnCardAnalyticsCommand extends Command<List<AnalyticsLog>> implements InjectableAction {

   @Inject @Named(JANET_WALLET) Janet janet;
   @Inject WalletBuildConfigHelper walletBuildConfigHelper;

   private static final boolean CLEAR_LOGS = true;

   @Override
   protected void run(CommandCallback<List<AnalyticsLog>> callback) throws Throwable {
      janet.createPipe(GetCardAnalyticLogsAction.class)
            .createObservableResult(GetCardAnalyticLogsAction.request(CLEAR_LOGS))
            .map(GetCardAnalyticLogsAction::getAnalyticsLogs)
            .doOnNext(this::printLogs)
            .subscribe(callback::onSuccess, callback::onFail);
   }

   private void printLogs(List<AnalyticsLog> analyticsLogs) {
      if (walletBuildConfigHelper.isDebug()) {
         Timber.d("On-card analytics logs:");
         Queryable.from(analyticsLogs).forEachR(analyticsLog -> Timber.d("%s, Formatted time = [%s]",
               analyticsLog, TimeUtils.formatToIso(analyticsLog.timestampMillis())));
      }
   }

}