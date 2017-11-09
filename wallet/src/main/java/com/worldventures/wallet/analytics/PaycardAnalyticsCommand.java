package com.worldventures.wallet.analytics;


import android.text.TextUtils;

import com.worldventures.janet.injection.InjectableAction;
import com.worldventures.wallet.domain.entity.record.Record;
import com.worldventures.wallet.service.RecordInteractor;
import com.worldventures.wallet.service.WalletAnalyticsInteractor;

import javax.inject.Inject;

import io.techery.janet.Command;
import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class PaycardAnalyticsCommand extends Command<Void> implements InjectableAction {

   @Inject WalletAnalyticsInteractor analyticsInteractor;
   @Inject RecordInteractor recordInteractor;

   private final BaseCardDetailsWithDefaultAction cardDetailsWithDefaultAction;
   private final Record record;

   public PaycardAnalyticsCommand(BaseCardDetailsWithDefaultAction cardDetailsWithDefaultAction, Record record) {
      this.cardDetailsWithDefaultAction = cardDetailsWithDefaultAction;
      this.record = record;
   }

   @Override
   protected void run(CommandCallback<Void> callback) throws Throwable {
      recordInteractor.defaultRecordIdPipe()
            .observeSuccessWithReplay()
            .take(1)
            .flatMap(command -> {
               boolean isDefault = TextUtils.equals(record.id(), command.getResult());
               cardDetailsWithDefaultAction.fillPaycardInfo(record, isDefault);
               return analyticsInteractor.walletAnalyticsPipe()
                     .createObservableResult(new WalletAnalyticsCommand(cardDetailsWithDefaultAction));
            })
            .map(Command::getResult)
            .subscribe(callback::onSuccess, callback::onFail);
   }
}
