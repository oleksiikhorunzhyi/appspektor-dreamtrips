package com.worldventures.dreamtrips.wallet.analytics;


import android.text.TextUtils;

import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsInteractor;
import com.worldventures.dreamtrips.wallet.domain.entity.record.Record;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;

import javax.inject.Inject;

import io.techery.janet.Command;
import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class PaycardAnalyticsCommand extends Command<Void> implements InjectableAction {

   @Inject SmartCardInteractor smartCardInteractor;
   @Inject AnalyticsInteractor analyticsInteractor;

   private final BaseCardDetailsWithDefaultAction cardDetailsWithDefaultAction;
   private final Record record;

   public PaycardAnalyticsCommand(BaseCardDetailsWithDefaultAction cardDetailsWithDefaultAction, Record record) {
      this.cardDetailsWithDefaultAction = cardDetailsWithDefaultAction;
      this.record = record;
   }

   @Override
   protected void run(CommandCallback<Void> callback) throws Throwable {
      smartCardInteractor.defaultRecordIdPipe()
            .observeSuccessWithReplay()
            .take(1)
            .flatMap(command -> {
               boolean isDefault = TextUtils.equals(record.id(), command.getResult());
               cardDetailsWithDefaultAction.fillPaycardInfo(record, isDefault);
               return analyticsInteractor.walletAnalyticsCommandPipe()
                     .createObservableResult(new WalletAnalyticsCommand(cardDetailsWithDefaultAction));
            })
            .map(Command::getResult)
            .subscribe(callback::onSuccess, callback::onFail);
   }
}
