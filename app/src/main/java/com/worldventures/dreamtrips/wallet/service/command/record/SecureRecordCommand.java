package com.worldventures.dreamtrips.wallet.service.command.record;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsInteractor;
import com.worldventures.dreamtrips.wallet.analytics.tokenization.ActionType;
import com.worldventures.dreamtrips.wallet.analytics.tokenization.TokenizationAnalyticsLocationCommand;
import com.worldventures.dreamtrips.wallet.analytics.tokenization.TokenizationCardAction;
import com.worldventures.dreamtrips.wallet.domain.entity.record.Record;
import com.worldventures.dreamtrips.wallet.domain.storage.disk.RecordsStorage;
import com.worldventures.dreamtrips.wallet.service.nxt.DetokenizeRecordCommand;
import com.worldventures.dreamtrips.wallet.service.nxt.NxtInteractor;
import com.worldventures.dreamtrips.wallet.service.nxt.TokenizeRecordCommand;

import javax.inject.Inject;

import io.techery.janet.Command;
import io.techery.janet.command.annotations.CommandAction;
import rx.Observable;
import rx.functions.Action1;

@CommandAction
public class SecureRecordCommand extends Command<Record> implements InjectableAction {

   @Inject NxtInteractor nxtInteractor;
   @Inject AnalyticsInteractor analyticsInteractor;
   @Inject RecordsStorage recordsStorage;

   private final Record record;
   private final boolean secureForLocalStorage;

   @Nullable
   private final ActionType actionType;

   private SecureRecordCommand(Record record, boolean secureForLocalStorage, @Nullable ActionType actionType) {

      this.record = record;
      this.secureForLocalStorage = secureForLocalStorage;
      this.actionType = actionType;
   }

   @Override
   protected void run(CommandCallback<Record> callback) throws Throwable {
      boolean offlineModeEnabled = recordsStorage.readOfflineModeState();

      if (offlineModeEnabled) {
         callback.onSuccess(record);
      } else {
         if (secureForLocalStorage) {
            tokenizeRecord(record).subscribe(handleSuccess(callback), handleFailure(callback));
         } else {
            detokenizeRecord(record).subscribe(handleSuccess(callback), handleFailure(callback));
         }
      }
   }

   private Observable<Record> tokenizeRecord(Record record) {
      return nxtInteractor.tokenizeRecordPipe()
            .createObservableResult(new TokenizeRecordCommand(record))
            .map(Command::getResult);
   }

   private Observable<Record> detokenizeRecord(Record record) {
      return nxtInteractor.detokenizeRecordPipe()
            .createObservableResult(new DetokenizeRecordCommand(record))
            .map(Command::getResult);
   }

   @NonNull
   private Action1<Record> handleSuccess(CommandCallback<Record> callback) {
      return (result) -> {
         sendTokenizationAnalytics(this.record, true);
         callback.onSuccess(result);
      };
   }

   @NonNull
   private Action1<Throwable> handleFailure(CommandCallback<Record> callback) {
      return (throwable) -> {
         sendTokenizationAnalytics(this.record, false);
         callback.onFail(throwable);
      };
   }

   private void sendTokenizationAnalytics(Record record, boolean success) {
      if (actionType == null) return;

      analyticsInteractor.walletAnalyticsCommandPipe().send(new TokenizationAnalyticsLocationCommand(
            TokenizationCardAction.from(record, success, actionType, secureForLocalStorage)
      ));
   }

   public static class Builder {

      private final Record record;
      private final boolean secureForLocalStorage;

      @Nullable
      private ActionType actionType;

      private Builder(Record record, boolean secureForLocalStorage) {
         this.record = record;
         this.secureForLocalStorage = secureForLocalStorage;
      }

      public static Builder prepareRecordForLocalStorage(Record record) {
         return new Builder(record, true);
      }

      public static Builder prepareRecordForSmartCard(Record record) {
         return new Builder(record, false);
      }

      public Builder withAnalyticsActionType(ActionType actionType) {
         this.actionType = actionType;
         return this;
      }

      public SecureRecordCommand create() {
         return new SecureRecordCommand(record, secureForLocalStorage, actionType);
      }

   }

}
