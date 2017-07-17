package com.worldventures.dreamtrips.wallet.service.command.record;

import android.support.v4.util.Pair;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsInteractor;
import com.worldventures.dreamtrips.wallet.analytics.tokenization.ActionType;
import com.worldventures.dreamtrips.wallet.domain.entity.record.ImmutableRecord;
import com.worldventures.dreamtrips.wallet.domain.entity.record.Record;
import com.worldventures.dreamtrips.wallet.service.RecordInteractor;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.service.command.RecordListCommand;
import com.worldventures.dreamtrips.wallet.service.command.SetDefaultCardOnDeviceCommand;
import com.worldventures.dreamtrips.wallet.service.nxt.NxtInteractor;
import com.worldventures.dreamtrips.wallet.util.WalletRecordUtil;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import io.techery.janet.ActionPipe;
import io.techery.janet.Command;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import io.techery.janet.smartcard.action.records.AddRecordAction;
import io.techery.janet.smartcard.action.records.GetDefaultRecordAction;
import io.techery.janet.smartcard.action.records.GetMemberRecordsAction;
import io.techery.mappery.MapperyContext;
import rx.Observable;

import static com.worldventures.dreamtrips.core.janet.JanetModule.JANET_WALLET;

@CommandAction
public class SyncRecordsCommand extends Command<Void> implements InjectableAction {

   @Inject RecordInteractor recordInteractor;
   @Inject SmartCardInteractor interactor;
   @Inject NxtInteractor nxtInteractor;
   @Inject AnalyticsInteractor analyticsInteractor;
   @Inject MapperyContext mapperyContext;
   @Inject @Named(JANET_WALLET) Janet janet;

   private int localOnlyRecordsCount = 0;

   @Override
   protected void run(CommandCallback<Void> callback) throws Throwable {
      Observable.zip(
            janet.createPipe(GetMemberRecordsAction.class)
                  .createObservableResult(new GetMemberRecordsAction())
                  .flatMap(action -> Observable.from(action.records)
                        .map(record -> mapperyContext.convert(record, Record.class))
                        .toList()),
            recordInteractor.cardsListPipe()
                  .createObservableResult(RecordListCommand.fetch())
                  .flatMap(action -> Observable.from(action.getResult()).toList()),
            janet.createPipe(GetDefaultRecordAction.class)
                  .createObservableResult(new GetDefaultRecordAction())
                  .map(getDefaultRecordAction -> getDefaultRecordAction.recordId),
            recordInteractor.defaultRecordIdPipe()
                  .createObservableResult(DefaultRecordIdCommand.fetch())
                  .map(DefaultRecordIdCommand::getResult),
            (deviceRecords, localRecords, deviceDefaultRecordId, localDefaultRecordId) -> {
               SyncBundle bundle = new SyncBundle();
               bundle.deviceRecords = deviceRecords;
               bundle.localRecords = localRecords;
               bundle.deviceDefaultRecordId = deviceDefaultRecordId >= 0 ? String.valueOf(deviceDefaultRecordId) : null;
               bundle.localDefaultRecordId = localDefaultRecordId;
               return bundle;
            })
            .flatMap(syncBundle -> sync(syncBundle, callback))
            .subscribe(aVoid -> callback.onSuccess(null), callback::onFail);
   }

   public int getLocalOnlyRecordsCount() {
      return localOnlyRecordsCount;
   }

   /**
    * Replace local data with data from the SmartCard.
    * Add local-only data to the SmartCard.
    * Sync default record Id.
    */
   private Observable<Void> sync(SyncBundle bundle, CommandCallback<Void> callback) {
      final List<Observable<Void>> operations = new ArrayList<>();

      // All SmartCard records -> prepare for local storage -> save (override) local storage
      if (!bundle.deviceRecords.isEmpty()) {
         operations.add(prepareRecordsForLocalStorage(Queryable.from(bundle.deviceRecords)
               .map(deviceOnlyRecord -> ImmutableRecord.copyOf(deviceOnlyRecord).withNumberLastFourDigits(
                     WalletRecordUtil.obtainLastCardDigits(deviceOnlyRecord.number())))
               .toList())
               .flatMap(this::saveRecords));
      }

      // Local only records -> prepare for SmartCard -> push to SmartCard
      List<Record> localOnlyRecords = Queryable.from(bundle.localRecords)
            .filter(localRecord -> !bundle.deviceRecords.contains(localRecord))
            .toList();
      if (!localOnlyRecords.isEmpty()) {
         localOnlyRecordsCount = localOnlyRecords.size();
         callback.onProgress(0);

         final ActionPipe<AddRecordAction> addRecordActionActionPipe = janet.createPipe(AddRecordAction.class);

         operations.add(prepareRecordsForSmartCard(localOnlyRecords)
               .concatMap(indexRecordPair -> addRecordActionActionPipe
                     .createObservableResult(new AddRecordAction(indexRecordPair.second))
                     .doOnSubscribe(() -> callback.onProgress(indexRecordPair.first)))
               .map(r -> null)
         );
      }

      // Sync default record id
      if (bundle.deviceDefaultRecordId != null && bundle.localDefaultRecordId == null) {
         operations.add(recordInteractor.defaultRecordIdPipe()
               .createObservableResult(DefaultRecordIdCommand.set(bundle.deviceDefaultRecordId))
               .map(command -> null)
         );
      } else if (bundle.localDefaultRecordId != null && !bundle.localDefaultRecordId.equals(bundle.deviceDefaultRecordId)) {
         operations.add(recordInteractor.setDefaultCardOnDeviceCommandPipe()
               .createObservableResult(SetDefaultCardOnDeviceCommand.setAsDefault(bundle.localDefaultRecordId))
               .map(value -> null));
      }
      return operations.isEmpty() ? Observable.just(null) : Queryable.from(operations)
            .fold(Observable::concatWith)
            .toList()
            .map(voids -> null);
   }

   private Observable<List<Record>> prepareRecordsForLocalStorage(List<? extends Record> records) {
      return recordInteractor.secureMultipleRecordsPipe()
            .createObservableResult(SecureMultipleRecordsCommand.Builder.prepareRecordForLocalStorage(records)
                  .skipTokenizationErrors(true)
                  .create())
            .map(Command::getResult);
   }

   private Observable<Pair<Integer, io.techery.janet.smartcard.model.Record>> prepareRecordsForSmartCard(List<Record> records) {
      return recordInteractor.secureMultipleRecordsPipe().createObservableResult(
            SecureMultipleRecordsCommand.Builder.prepareRecordForSmartCard(records)
                  .withAnalyticsActionType(ActionType.RESTORE)
                  .create())
            .map(Command::getResult)
            .map(detokenizedRecords -> Queryable.from(detokenizedRecords)
                  .map((detokenizedRecord, i) -> new Pair<>(i + 1, mapperyContext.convert(
                        detokenizedRecord, io.techery.janet.smartcard.model.Record.class)))
                  .toList())
            .flatMap(Observable::from);
   }

   private Observable<Void> saveRecords(List<Record> records) {
      return recordInteractor.cardsListPipe()
            .createObservableResult(RecordListCommand.replace(records))
            .map(o -> null);
   }

   private static class SyncBundle {
      private List<Record> deviceRecords;
      private List<Record> localRecords;
      private String deviceDefaultRecordId;
      private String localDefaultRecordId;
   }

}