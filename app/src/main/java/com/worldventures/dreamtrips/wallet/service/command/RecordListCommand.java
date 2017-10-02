package com.worldventures.dreamtrips.wallet.service.command;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.wallet.domain.entity.record.Record;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.service.command.wizard.DummyRecordCreator;
import com.worldventures.dreamtrips.wallet.util.WalletFeatureHelper;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.techery.janet.command.annotations.CommandAction;
import rx.functions.Func1;

@CommandAction
public class RecordListCommand extends CachedValueCommand<List<Record>> implements InjectableAction {

   @Inject WalletFeatureHelper featureHelper;
   @Inject SmartCardInteractor smartCardInteractor;

   public static RecordListCommand fetch() {
      return new RecordListCommand();
   }

   public static RecordListCommand remove(String recordId) {
      return new RecordListCommand(new RemoveOperationFunc(recordId));
   }

   public static RecordListCommand add(Record record) {
      return new RecordListCommand(new AddOperationFunc(record));
   }

   public static RecordListCommand addAll(List<Record> records) {
      return new RecordListCommand(new AddOperationFunc(records));
   }

   public static RecordListCommand replace(List<Record> records) {
      return new RecordListCommand(new ReplaceCardsOperationFunc(records));
   }

   public static RecordListCommand edit(Record record) {
      return new RecordListCommand(new EditOperationFunc(record));
   }

   public RecordListCommand() {
   }

   public RecordListCommand(Func1<List<Record>, List<Record>> operationFunc) {
      super(cards -> operationFunc.call(new ArrayList<>(cards)));
   }

   private static final class RemoveOperationFunc implements Func1<List<Record>, List<Record>> {

      private final String recordId;

      RemoveOperationFunc(String recordId) {
         this.recordId = recordId;
      }

      @Override
      public List<Record> call(List<Record> records) {
         records.remove(Queryable.from(records).first(element -> recordId.equals(element.id())));
         return records;
      }
   }

   private static final class AddOperationFunc implements Func1<List<Record>, List<Record>> {

      private final List<Record> recordsToAdd = new ArrayList<>();

      AddOperationFunc(Record record) {
         recordsToAdd.add(record);
      }

      AddOperationFunc(List<Record> records) {
         recordsToAdd.addAll(records);
      }

      @Override
      public List<Record> call(List<Record> records) {
         records.addAll(recordsToAdd);
         return records;
      }
   }

   private static final class ReplaceCardsOperationFunc implements Func1<List<Record>, List<Record>> {

      private final List<Record> newRecords = new ArrayList<>();

      ReplaceCardsOperationFunc(List<Record> records) {
         newRecords.addAll(records);
      }

      @Override
      public List<Record> call(List<Record> records) {
         records.clear();
         records.addAll(newRecords);
         return records;
      }
   }

   private static final class EditOperationFunc implements Func1<List<Record>, List<Record>> {

      private final Record editedRecord;

      EditOperationFunc(Record record) {
         editedRecord = record;
      }

      @Override
      public List<Record> call(List<Record> records) {
         return !records.isEmpty() ? Queryable.from(records).map(this::remapRecord).toList() : records;
      }

      private Record remapRecord(Record record) {
         return record.id().equals(editedRecord.id()) ? editedRecord : record;
      }
   }

   @Override
   protected void run(CommandCallback<List<Record>> callback) throws Throwable {
      if (featureHelper.isSampleCardMode()) {
         smartCardInteractor.smartCardUserPipe()
               .createObservableResult(SmartCardUserCommand.fetch())
               .map(command -> DummyRecordCreator.createRecords(command.getResult(), "0"))
               .subscribe(callback::onSuccess, callback::onFail);
      } else {
         super.run(callback);
      }
   }
}