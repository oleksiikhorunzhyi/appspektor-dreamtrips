package com.worldventures.dreamtrips.wallet.analytics.oncard;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.wallet.service.WalletAnalyticsInteractor;
import com.worldventures.dreamtrips.wallet.analytics.oncard.action.SmartCardAnalyticsAction;
import com.worldventures.dreamtrips.wallet.analytics.oncard.action.SmartCardPaymentAction;
import com.worldventures.dreamtrips.wallet.analytics.oncard.action.SmartCardUserAction;
import com.worldventures.dreamtrips.wallet.domain.storage.disk.RecordsStorage;
import com.worldventures.dreamtrips.wallet.service.WalletSocialInfoProvider;

import java.util.List;

import javax.inject.Inject;

import io.techery.janet.Command;
import io.techery.janet.command.annotations.CommandAction;
import io.techery.janet.smartcard.model.analytics.AnalyticsLog;
import rx.Observable;

@CommandAction
public class SendOnCardAnalyticsCommand extends Command<Void> implements InjectableAction {

   @Inject WalletAnalyticsInteractor analyticsInteractor;
   @Inject RecordsStorage recordsStorage;
   @Inject WalletSocialInfoProvider socialInfoProvider;

   private final List<AnalyticsLog> analyticsLogs;

   public SendOnCardAnalyticsCommand(List<AnalyticsLog> analyticsLogs) {
      this.analyticsLogs = analyticsLogs;
   }

   @Override
   protected void run(CommandCallback<Void> callback) throws Throwable {
      if (analyticsLogs.isEmpty()) {
         callback.onSuccess(null);
         return;
      }

      Observable.from(analyticsLogs)
            .map(SmartCardAnalyticsAction::from)
            .filter(analyticsAction -> analyticsAction != null)
            .doOnNext(analyticsAction -> {
               if (analyticsAction instanceof SmartCardPaymentAction) {
                  fillRecordDetails((SmartCardPaymentAction) analyticsAction);
               } else if (analyticsAction instanceof SmartCardUserAction) {
                  fillUserDetails((SmartCardUserAction) analyticsAction);
               }
            })
            .doOnNext(analyticsAction -> analyticsInteractor.analyticsActionPipe().send(analyticsAction))
            .toList()
            .map(analyticsAction -> (Void) null)
            .subscribe(callback::onSuccess, callback::onFail);
   }

   private void fillRecordDetails(SmartCardPaymentAction analyticsAction) {
      int paymentCardId = analyticsAction.getRecordId();
      if (paymentCardId >= 0) {
         final String storedRecordId = String.valueOf(paymentCardId);
         analyticsAction.setRecord(Queryable.from(recordsStorage.readRecords())
               .firstOrDefault(record -> storedRecordId.equals(record.id())));
      }
   }

   private void fillUserDetails(SmartCardUserAction analyticsAction) {
      analyticsAction.setUserId(socialInfoProvider.userId());
   }
}
