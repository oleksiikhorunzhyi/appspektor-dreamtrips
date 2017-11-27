package com.worldventures.wallet.service.command

import com.worldventures.janet.injection.InjectableAction
import com.worldventures.wallet.domain.entity.record.Record
import com.worldventures.wallet.service.SmartCardInteractor
import com.worldventures.wallet.service.command.wizard.DummyRecordCreator
import com.worldventures.wallet.util.WalletFeatureHelper
import io.techery.janet.command.annotations.CommandAction
import rx.functions.Func1
import java.util.ArrayList
import javax.inject.Inject

@CommandAction
class RecordListCommand : CachedValueCommand<List<Record>>, InjectableAction {

   @Inject lateinit var featureHelper: WalletFeatureHelper
   @Inject lateinit var smartCardInteractor: SmartCardInteractor

   private constructor()

   private constructor(func: Func1<MutableList<Record>, List<Record>>)
         : super(Func1<List<Record>, List<Record>> { func.call(it.toMutableList()) })

   private class RemoveOperationFunc internal constructor(private val recordId: String) : Func1<MutableList<Record>, List<Record>> {

      override fun call(records: MutableList<Record>): MutableList<Record> {
         records.remove(records.first { (id) -> recordId == id })
         return records
      }
   }

   private class AddOperationFunc : Func1<MutableList<Record>, List<Record>> {

      private val recordsToAdd = ArrayList<Record>()

      internal constructor(record: Record) {
         recordsToAdd.add(record)
      }

      internal constructor(records: MutableList<Record>) {
         recordsToAdd.addAll(records)
      }

      override fun call(records: MutableList<Record>): List<Record> {
         records.addAll(recordsToAdd)
         return records
      }
   }

   private class ReplaceCardsOperationFunc internal constructor(records: MutableList<Record>) : Func1<MutableList<Record>, List<Record>> {

      private val newRecords = ArrayList<Record>()

      init {
         newRecords.addAll(records)
      }

      override fun call(records: MutableList<Record>): List<Record> {
         records.clear()
         records.addAll(newRecords)
         return records
      }
   }

   private class EditOperationFunc internal constructor(private val editedRecord: Record) : Func1<MutableList<Record>, List<Record>> {

      override fun call(records: MutableList<Record>): List<Record> {

         return if (!records.isEmpty()) records.map { this.remapRecord(it) } else records
      }

      private fun remapRecord(record: Record): Record {
         return if (record.id.equals(editedRecord.id)) editedRecord else record
      }
   }

   @Throws(Throwable::class)
   override fun run(callback: CommandCallback<List<Record>>) {
      if (featureHelper.isSampleCardMode) {
         smartCardInteractor.smartCardUserPipe()
               .createObservableResult(SmartCardUserCommand.fetch())
               .map { command -> DummyRecordCreator.createRecords(command.result, "0") }
               .subscribe({ callback.onSuccess(it) }, { callback.onFail(it) })
      } else {
         super.run(callback)
      }
   }

   companion object {

      fun fetch(): RecordListCommand {
         return RecordListCommand()
      }

      fun remove(recordId: String): RecordListCommand {
         return RecordListCommand(RemoveOperationFunc(recordId))
      }

      fun add(record: Record): RecordListCommand {
         return RecordListCommand(AddOperationFunc(record))
      }

      fun addAll(records: MutableList<Record>): RecordListCommand {
         return RecordListCommand(AddOperationFunc(records))
      }

      fun replace(records: MutableList<Record>): RecordListCommand {
         return RecordListCommand(ReplaceCardsOperationFunc(records))
      }

      fun edit(record: Record): RecordListCommand {
         return RecordListCommand(EditOperationFunc(record))
      }
   }
}
