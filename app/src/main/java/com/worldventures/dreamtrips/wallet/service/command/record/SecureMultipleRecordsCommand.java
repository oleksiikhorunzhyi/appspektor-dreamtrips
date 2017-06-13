package com.worldventures.dreamtrips.wallet.service.command.record;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsInteractor;
import com.worldventures.dreamtrips.wallet.analytics.tokenization.ActionType;
import com.worldventures.dreamtrips.wallet.analytics.tokenization.TokenizationAnalyticsLocationCommand;
import com.worldventures.dreamtrips.wallet.analytics.tokenization.TokenizationCardAction;
import com.worldventures.dreamtrips.wallet.domain.entity.record.Record;
import com.worldventures.dreamtrips.wallet.domain.storage.disk.RecordsStorage;
import com.worldventures.dreamtrips.wallet.service.nxt.BaseMultipleRecordsCommand;
import com.worldventures.dreamtrips.wallet.service.nxt.DetokenizeMultipleRecordsCommand;
import com.worldventures.dreamtrips.wallet.service.nxt.NxtInteractor;
import com.worldventures.dreamtrips.wallet.service.nxt.TokenizeMultipleRecordsCommand;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.techery.janet.ActionState;
import io.techery.janet.Command;
import io.techery.janet.command.annotations.CommandAction;
import io.techery.janet.helper.ActionStateToActionTransformer;
import rx.Observable;
import rx.functions.Action1;

@CommandAction
public class SecureMultipleRecordsCommand extends Command<List<Record>> implements InjectableAction {

   @Inject NxtInteractor nxtInteractor;
   @Inject AnalyticsInteractor analyticsInteractor;
   @Inject RecordsStorage recordsStorage;

   private final List<Record> records;
   private final boolean secureForLocalStorage;
   private final boolean skipTokenizationErrors;

   @Nullable
   private final ActionType actionType;

   private SecureMultipleRecordsCommand(List<Record> records, boolean secureForLocalStorage,
         boolean skipTokenizationErrors, @Nullable ActionType actionType) {

      this.records = records;
      this.secureForLocalStorage = secureForLocalStorage;
      this.skipTokenizationErrors = skipTokenizationErrors;
      this.actionType = actionType;
   }

   @Override
   protected void run(CommandCallback<List<Record>> callback) throws Throwable {
      boolean offlineModeEnabled = recordsStorage.readOfflineModeState();

      if (offlineModeEnabled) {
         callback.onSuccess(records);
      } else {
         if (secureForLocalStorage) {
            tokenizeRecords(records).subscribe(callback::onSuccess, callback::onFail);
         } else {
            detokenizeRecords(records).subscribe(callback::onSuccess, callback::onFail);
         }
      }
   }

   private Observable<List<Record>> tokenizeRecords(List<? extends Record> records) {
      return nxtInteractor.tokenizeMultipleRecordsPipe()
            .createObservable(new TokenizeMultipleRecordsCommand(records, skipTokenizationErrors))
            .doOnNext(processResultForAnalytics())
            .compose(new ActionStateToActionTransformer<>())
            .map(Command::getResult);
   }

   private Observable<List<Record>> detokenizeRecords(List<? extends Record> records) {
      return nxtInteractor.detokenizeMultipleRecordsPipe()
            .createObservable(new DetokenizeMultipleRecordsCommand(records, skipTokenizationErrors))
            .doOnNext(processResultForAnalytics())
            .compose(new ActionStateToActionTransformer<>())
            .map(Command::getResult);
   }

   @NonNull
   private Action1<ActionState<? extends BaseMultipleRecordsCommand>> processResultForAnalytics() {
      return actionState -> {
         if (actionState.status == ActionState.Status.SUCCESS) {
            sendTokenizationAnalytics(actionState.action.getResult(), true);
         }
         if (actionState.status == ActionState.Status.FAIL || actionState.status == ActionState.Status.SUCCESS) {
            sendTokenizationAnalytics(actionState.action.getRecordsProcessedWithErrors(), false);
         }
      };
   }

   private void sendTokenizationAnalytics(List<Record> records, boolean success) {
      if (actionType == null || records.isEmpty()) return;

      Queryable.from(records).forEachR(recordWithError -> analyticsInteractor.walletAnalyticsCommandPipe()
            .send(new TokenizationAnalyticsLocationCommand(
                  TokenizationCardAction.from(recordWithError, success, actionType, secureForLocalStorage)
            )));
   }

   public static class Builder {

      private final List<Record> records = new ArrayList<>();
      private final boolean secureForLocalStorage;

      private boolean skipTokenizationErrors;
      @Nullable
      private ActionType actionType;

      private Builder(List<? extends Record> records, boolean secureForLocalStorage) {
         this.records.addAll(records);
         this.secureForLocalStorage = secureForLocalStorage;
      }

      public static Builder prepareRecordForLocalStorage(List<? extends Record> records) {
         return new Builder(records, true);
      }

      public static Builder prepareRecordForSmartCard(List<? extends Record> records) {
         return new Builder(records, false);
      }

      public Builder withAnalyticsActionType(ActionType actionType) {
         this.actionType = actionType;
         return this;
      }

      public Builder skipTokenizationErrors(boolean skipTokenizationErrors) {
         this.skipTokenizationErrors = skipTokenizationErrors;
         return this;
      }

      public SecureMultipleRecordsCommand create() {
         return new SecureMultipleRecordsCommand(records, secureForLocalStorage, skipTokenizationErrors, actionType);
      }

   }

}
