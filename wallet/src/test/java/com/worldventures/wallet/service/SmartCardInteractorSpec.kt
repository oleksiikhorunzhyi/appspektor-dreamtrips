package com.worldventures.wallet.service

import android.content.Context
import android.test.mock.MockContext
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.spy
import com.nhaarman.mockito_kotlin.whenever
import com.worldventures.core.janet.SessionActionPipeCreator
import com.worldventures.core.janet.cache.CacheResultWrapper
import com.worldventures.core.janet.cache.storage.ActionStorage
import com.worldventures.core.modules.auth.service.AuthInteractor
import com.worldventures.core.service.analytics.AnalyticsInteractor
import com.worldventures.core.test.AssertUtil.assertActionFail
import com.worldventures.core.test.AssertUtil.assertActionSuccess
import com.worldventures.wallet.BaseSpec
import com.worldventures.wallet.domain.converter.SmartCardRecordToWalletRecordConverter
import com.worldventures.wallet.domain.converter.WalletRecordToSmartCardRecordConverter
import com.worldventures.wallet.domain.entity.record.Record
import com.worldventures.wallet.domain.entity.record.RecordType
import com.worldventures.wallet.domain.session.NxtSessionHolder
import com.worldventures.wallet.domain.storage.WalletStorage
import com.worldventures.wallet.domain.storage.action.DefaultRecordIdStorage
import com.worldventures.wallet.domain.storage.action.SmartCardActionStorage
import com.worldventures.wallet.domain.storage.action.WalletRecordsActionStorage
import com.worldventures.wallet.domain.storage.disk.RecordsStorage
import com.worldventures.wallet.domain.storage.disk.TestRecordsStorage
import com.worldventures.wallet.model.createTestMultiResponseBody
import com.worldventures.wallet.service.command.RecordListCommand
import com.worldventures.wallet.service.command.SetDefaultCardOnDeviceCommand
import com.worldventures.wallet.service.command.SetLockStateCommand
import com.worldventures.wallet.service.command.offline_mode.OfflineModeStatusCommand
import com.worldventures.wallet.service.command.offline_mode.RestoreOfflineModeDefaultStateCommand
import com.worldventures.wallet.service.command.offline_mode.SwitchOfflineModeCommand
import com.worldventures.wallet.service.command.record.AddRecordCommand
import com.worldventures.wallet.service.command.record.DefaultRecordIdCommand
import com.worldventures.wallet.service.command.record.DeleteRecordCommand
import com.worldventures.wallet.service.command.record.UpdateRecordCommand
import com.worldventures.wallet.service.nxt.NxtInteractor
import com.worldventures.wallet.service.nxt.TokenizeMultipleRecordsCommand
import com.worldventures.wallet.util.FormatException
import com.worldventures.wallet.util.NetworkUnavailableException
import com.worldventures.wallet.util.WalletFeatureHelper
import com.worldventures.wallet.util.WalletFeatureHelperFull
import io.techery.janet.ActionState
import io.techery.janet.CommandActionService
import io.techery.janet.Janet
import io.techery.janet.SmartCardActionService
import io.techery.janet.http.test.MockHttpActionService
import io.techery.janet.smartcard.action.lock.GetLockDeviceStatusAction
import io.techery.janet.smartcard.action.support.ConnectAction
import io.techery.janet.smartcard.mock.client.MockSmartCardClient
import io.techery.janet.smartcard.model.ImmutableConnectionParams
import io.techery.mappery.Mappery
import io.techery.mappery.MapperyContext
import org.jetbrains.spek.api.dsl.context
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.junit.Assert
import rx.observers.TestSubscriber
import rx.schedulers.Schedulers

class SmartCardInteractorSpec : BaseSpec({
   describe("SmartCard SDK actions") {
      beforeEachTest {
         mockDb = createMockDb()
         cardStorage = TestRecordsStorage(offlineModeEnabled = true)
         mappery = createMappery()
         janet = createJanet()
         smartCardInteractor = createSmartCardInteractor(janet)
         authInteractor = createAuthInteractor(janet)
         firmwareInteractor = createFirmwareInteractor(janet)
         recordInteractor = createRecordInteractor(janet)
         smartCardSyncManager = createSmartCardSyncManager(janet, smartCardInteractor, firmwareInteractor, recordInteractor)
         nxtInteractor = createNxtInteractor(janet)
         analyticsInteractor = createAnalyticsInteractor(janet)
         nxtSessionHolder = mock()
         networkService = mock()
         janet.connectToSmartCardSdk()
      }

      context("Default card id is fetching") {

         it("saves default card id locally after sending to device ") {
            val testSmartCardId = "4"

            val testSubscriber = TestSubscriber<ActionState<DefaultRecordIdCommand>>()
            janet.createPipe(DefaultRecordIdCommand::class.java)
                  .observe()
                  .subscribe(testSubscriber)

            janet.createPipe(SetDefaultCardOnDeviceCommand::class.java)
                  .send(SetDefaultCardOnDeviceCommand.setAsDefault(testSmartCardId))

            assertActionSuccess(testSubscriber, { testSmartCardId == it.result })
            assertActionSuccess(loadDefaultCardId(), { testSmartCardId == it.result })
         }
      }

      context("Records CRUD commands") {

         val debitCard = Record(null, number = "7777 7777 7777 7777", numberLastFourDigits = "7777", expDate = "00/00", recordType = RecordType.DEBIT)
         val creditCard = Record(null, number = "7777 7777 7777 7777", numberLastFourDigits = "7777", expDate = "00/00", recordType = RecordType.CREDIT)

         it("creates record with ID") {
            val testSubscriber = addRecord(debitCard)
            assertActionSuccess(testSubscriber, { it.result.id != null })
         }

         it("creates record and saves default record id") {
            val testSubscriber = addRecord(debitCard, setAsDefaultCard = true)

            var savedRecord: Record? = null
            assertActionSuccess(testSubscriber, {
               savedRecord = it.result
               it.result.id != null
            })

            val defaultIdSubscriber = loadDefaultCardId()
            assertActionSuccess(defaultIdSubscriber, { it.result == savedRecord?.id })
         }

         it("throws FormatException when creating record with invalid data") {
            val subscriber = addRecord(debitCard, cvv = "pp")
            assertActionFail(subscriber, { it.cause is FormatException })
         }

         it("reads proper amount of records after adding") {
            addRecord(debitCard)
            addRecord(creditCard)

            fetchCardListOfCard { it.result.size == 2 }
         }

         it("updates record data") {
            val oldRecordName = "Foo"
            val newRecordName = "Bar"

            val testSubscriber = addRecord(debitCard, nickName = oldRecordName)
            var savedRecord: Record? = null
            assertActionSuccess(testSubscriber, {
               savedRecord = it.result
               it.result.id != null
            })

            val updateRecordSubscriber = TestSubscriber<ActionState<UpdateRecordCommand>>()
            recordInteractor.updateRecordPipe()
                  .createObservable(UpdateRecordCommand.updateNickname(savedRecord!!, newRecordName))
                  .subscribe(updateRecordSubscriber)

            assertActionSuccess(updateRecordSubscriber, { true })
            fetchCardListOfCard { it.result.size == 1 }
            fetchCardListOfCard { it.result[0].nickname == newRecordName }
         }

         it("deletes record") {
            var recordToDelete: Record? = null

            val testSubscriber = addRecord(debitCard)
            assertActionSuccess(testSubscriber, {
               recordToDelete = it.result
               it.result.id != null
            })

            fetchCardListOfCard { it.result.find { it.id == recordToDelete?.id } != null }

            recordInteractor.deleteRecordPipe()
                  .createObservable(DeleteRecordCommand(recordToDelete?.id))
                  .subscribe()

            fetchCardListOfCard { it.result.find { it.id == recordToDelete?.id } == null }
         }
      }

      context("Tokenization and Offline mode") {

         val defaultOfflineModeState = false
         val testRecord = Record(null, number = "7777 7777 7777 7777", numberLastFourDigits = "7777", expDate = "00/00")

         it("switches state properly") {

            val testSubscriber = TestSubscriber<OfflineModeStatusCommand>()
            smartCardInteractor.offlineModeStatusPipe()
                  .observeSuccess()
                  .subscribe(testSubscriber)

            smartCardInteractor.offlineModeStatusPipe()
                  .send(OfflineModeStatusCommand.save(true))
            smartCardInteractor.offlineModeStatusPipe()
                  .send(OfflineModeStatusCommand.save(false))
            smartCardInteractor.offlineModeStatusPipe()
                  .send(OfflineModeStatusCommand.save(true))

            Assert.assertTrue(testSubscriber.onNextEvents.size == 3)
            Assert.assertTrue(testSubscriber.onNextEvents[0].result == true)
            Assert.assertTrue(testSubscriber.onNextEvents[1].result == false)
            Assert.assertTrue(testSubscriber.onNextEvents[2].result == true)
         }

         it("restores state to default value") {

            smartCardInteractor.offlineModeStatusPipe()
                  .send(OfflineModeStatusCommand.save(!defaultOfflineModeState))

            val stateNotDefaultSubscriber = TestSubscriber<ActionState<OfflineModeStatusCommand>>()
            smartCardInteractor.offlineModeStatusPipe()
                  .createObservable(OfflineModeStatusCommand.fetch())
                  .subscribe(stateNotDefaultSubscriber)

            val stateDefaultSubscriber = TestSubscriber<ActionState<OfflineModeStatusCommand>>()
            smartCardInteractor.restoreOfflineModeDefaultStatePipe()
                  .createObservableResult(RestoreOfflineModeDefaultStateCommand())
                  .flatMap { smartCardInteractor.offlineModeStatusPipe().createObservable(OfflineModeStatusCommand.fetch()) }
                  .subscribe(stateDefaultSubscriber)

            assertActionSuccess(stateNotDefaultSubscriber, { it.result == !defaultOfflineModeState })
            assertActionSuccess(stateDefaultSubscriber, { it.result == defaultOfflineModeState })
         }

         it("record is tokenized properly") {

            val testSubscriber = TestSubscriber<ActionState<TokenizeMultipleRecordsCommand>>()
            nxtInteractor.tokenizeMultipleRecordsPipe().createObservable(TokenizeMultipleRecordsCommand(listOf(
                  Record(id = "0", cvv = "123", number = "7777 7777 7777 7777", numberLastFourDigits = "7777", expDate = "00/00"),
                  Record(id = "1", number = "0000111122223333", numberLastFourDigits = "7777", expDate = "00/00")),
                  true))
                  .subscribe(testSubscriber)

            assertActionSuccess(testSubscriber, { it.result[0].cvv == TEST_CVV })
            assertActionSuccess(testSubscriber, { it.result[1].number == TEST_NUMBER })
         }

         it("locally stored records processed when offline mode switches") {
            whenever(networkService.isAvailable).thenReturn(true)

            // Enable offline mode
            smartCardInteractor.offlineModeStatusPipe()
                  .send(OfflineModeStatusCommand.save(!defaultOfflineModeState))

            // Add a record
            addRecord(testRecord, cvv = "777")

            // Switch offline mode
            val switchOfflineModeSubscriber = TestSubscriber<ActionState<SwitchOfflineModeCommand>>()
            smartCardInteractor.switchOfflineModePipe()
                  .createObservable(SwitchOfflineModeCommand())
                  .subscribe(switchOfflineModeSubscriber)

            // Fetch offline mode state
            val stateDefaultSubscriber = TestSubscriber<ActionState<OfflineModeStatusCommand>>()
            smartCardInteractor.offlineModeStatusPipe()
                  .createObservable(OfflineModeStatusCommand.fetch())
                  .subscribe(stateDefaultSubscriber)

            // Check if offline mode was switched successfully and it is disabled
            assertActionSuccess(switchOfflineModeSubscriber, { true })
            assertActionSuccess(stateDefaultSubscriber, { it.result == defaultOfflineModeState })

            // Check that the record has tokenized cvv value
            fetchCardListOfCard { it.result[0].cvv == TEST_CVV }
         }

         it("does not throw NetworkUnavailableException if network is unavailable and no records stored locally") {
            whenever(networkService.isAvailable).thenReturn(false)

            val switchOfflineModeNoRecordsSubscriber = TestSubscriber<ActionState<SwitchOfflineModeCommand>>()
            smartCardInteractor.switchOfflineModePipe()
                  .createObservable(SwitchOfflineModeCommand())
                  .subscribe(switchOfflineModeNoRecordsSubscriber)

            assertActionSuccess(switchOfflineModeNoRecordsSubscriber, { true })
         }

         it("throws NetworkUnavailableException if network is unavailable and some records stored locally") {
            whenever(networkService.isAvailable).thenReturn(false)

            val addRecordSubscriber = addRecord(testRecord)
            assertActionSuccess(addRecordSubscriber, { true })

            val switchOfflineModeWithRecordsSubscriber = TestSubscriber<ActionState<SwitchOfflineModeCommand>>()
            smartCardInteractor.switchOfflineModePipe()
                  .createObservable(SwitchOfflineModeCommand())
                  .subscribe(switchOfflineModeWithRecordsSubscriber)

            assertActionFail(switchOfflineModeWithRecordsSubscriber, { it.cause is NetworkUnavailableException })
         }
      }

      context("Lock state is fetching") {

         fun getLockState(): TestSubscriber<ActionState<GetLockDeviceStatusAction>> {
            val testSubscriber = TestSubscriber<ActionState<GetLockDeviceStatusAction>>()
            janet.createPipe(GetLockDeviceStatusAction::class.java).createObservable(GetLockDeviceStatusAction())
                  .subscribe(testSubscriber)
            return testSubscriber
         }

         it("should fetch lock state") {
            smartCardInteractor.lockPipe().createObservableResult(SetLockStateCommand(true)).subscribe()
            assertActionSuccess(getLockState(), { it.locked })
         }
      }
   }
}) {

   private companion object {
      val TEST_NUMBER: String = "9876987698769876"
      val TEST_CVV: String = "987"
      val TEST_MAG_STRIPE_DATA: String = "mag_stripe_data"

      lateinit var mockDb: WalletStorage
      lateinit var janet: Janet
      lateinit var mappery: MapperyContext
      lateinit var smartCardInteractor: SmartCardInteractor
      lateinit var authInteractor: AuthInteractor
      lateinit var firmwareInteractor: FirmwareInteractor
      lateinit var recordInteractor: RecordInteractor
      lateinit var cardStorage: RecordsStorage
      lateinit var smartCardSyncManager: SmartCardSyncManager
      lateinit var nxtInteractor: NxtInteractor
      lateinit var nxtSessionHolder: NxtSessionHolder
      lateinit var analyticsInteractor: AnalyticsInteractor
      lateinit var networkService: WalletNetworkService
      val featureHelper: WalletFeatureHelper = WalletFeatureHelperFull()

      val setOfMultiplyStorage: () -> Set<ActionStorage<*>> = {
         setOf(DefaultRecordIdStorage(cardStorage), SmartCardActionStorage(mockDb))
      }

      fun createSmartCardInteractor(janet: Janet) = SmartCardInteractor(SessionActionPipeCreator(janet), { Schedulers.immediate() })

      fun createAuthInteractor(janet: Janet) = AuthInteractor(SessionActionPipeCreator(janet))

      fun createFirmwareInteractor(janet: Janet) = FirmwareInteractor(SessionActionPipeCreator(janet))

      fun createFactoryResetInteractor(janet: Janet) = FactoryResetInteractor(SessionActionPipeCreator(janet))

      fun createRecordInteractor(janet: Janet) = RecordInteractor(SessionActionPipeCreator(janet), { Schedulers.immediate() })

      fun createNxtInteractor(janet: Janet) = NxtInteractor(SessionActionPipeCreator(janet))

      fun createAnalyticsInteractor(janet: Janet) = AnalyticsInteractor(SessionActionPipeCreator(janet))

      fun createSmartCardSyncManager(janet: Janet,
                                     smartCardInteractor: SmartCardInteractor,
                                     firmwareInteractor: FirmwareInteractor,
                                     recordInteractor: RecordInteractor) =
            SmartCardSyncManager(janet, smartCardInteractor, firmwareInteractor, recordInteractor,
                  createFactoryResetInteractor(janet), authInteractor, featureHelper)

      fun createJanet(): Janet {
         val daggerCommandActionService = CommandActionService()
               .wrapCache()
               .bindStorageSet(setOfMultiplyStorage())
               .bindStorageSet(setOf(WalletRecordsActionStorage(cardStorage)))
               .wrapDagger()

         janet = Janet.Builder()
               .addService(daggerCommandActionService)
               .addService(mockHttpService().wrapStub().wrapCache())
               .addService(SmartCardActionService.createDefault(MockSmartCardClient()))
               .build()

         daggerCommandActionService.registerProvider(Janet::class.java) { janet }
         daggerCommandActionService.registerProvider(WalletStorage::class.java) { mockDb }
         daggerCommandActionService.registerProvider(RecordsStorage::class.java) { cardStorage }
         daggerCommandActionService.registerProvider(MapperyContext::class.java) { mappery }
         daggerCommandActionService.registerProvider(Context::class.java, { MockContext() })
         daggerCommandActionService.registerProvider(SmartCardInteractor::class.java, { smartCardInteractor })
         daggerCommandActionService.registerProvider(FirmwareInteractor::class.java, { firmwareInteractor })
         daggerCommandActionService.registerProvider(RecordInteractor::class.java, { recordInteractor })
         daggerCommandActionService.registerProvider(NxtInteractor::class.java, { nxtInteractor })
         daggerCommandActionService.registerProvider(AnalyticsInteractor::class.java, { analyticsInteractor })
         daggerCommandActionService.registerProvider(NxtSessionHolder::class.java, { nxtSessionHolder })
         daggerCommandActionService.registerProvider(WalletNetworkService::class.java, { networkService })
         daggerCommandActionService.registerProvider(WalletFeatureHelper::class.java, { featureHelper })
         return janet
      }

      fun createMockDb(): WalletStorage = spy()

      fun createMappery(): MapperyContext = Mappery.Builder()
            .map(Record::class.java).to(io.techery.janet.smartcard.model.Record::class.java, WalletRecordToSmartCardRecordConverter())
            .map(io.techery.janet.smartcard.model.Record::class.java).to(Record::class.java, SmartCardRecordToWalletRecordConverter())
            .build()

      fun loadDefaultCardId(): TestSubscriber<ActionState<DefaultRecordIdCommand>> {
         val testSubscriber = TestSubscriber<ActionState<DefaultRecordIdCommand>>()

         recordInteractor.defaultRecordIdPipe()
               .createObservable(DefaultRecordIdCommand.fetch())
               .subscribe(testSubscriber)
         return testSubscriber
      }

      fun fetchCardListOfCard(predicate: (command: RecordListCommand) -> Boolean) {
         val testSubscriber: TestSubscriber<ActionState<RecordListCommand>> = TestSubscriber()

         recordInteractor.cardsListPipe()
               .createObservable(RecordListCommand.fetch())
               .subscribe(testSubscriber)
         assertActionSuccess(testSubscriber, { predicate(it) })
      }

      fun addRecord(record: Record, nickName: String = "Test Card", cvv: String = "000", setAsDefaultCard: Boolean = false): TestSubscriber<ActionState<AddRecordCommand>> {
         // by default, mock payment card number is 123456789. It mean then cvv should be contain only 3 digit.
         val testSubscriber = TestSubscriber<ActionState<AddRecordCommand>>()
         val cmd = AddRecordCommand.Builder()
               .setRecord(record)
               .setRecordName(nickName)
               .setCvv(cvv).setSetAsDefaultRecord(setAsDefaultCard)

               .create()

         recordInteractor.addRecordPipe()
               .createObservable(cmd)
               .subscribe(testSubscriber)
         return testSubscriber
      }

      fun mockHttpService(): MockHttpActionService {
         return MockHttpActionService.Builder()
               .bind(MockHttpActionService.Response(200)
                     .body(createTestMultiResponseBody(listOf(null, "0", "1", "2"), TEST_NUMBER, TEST_CVV, TEST_MAG_STRIPE_DATA)), { request ->
                  request.url.contains("multifunction") && request.method.equals("post", true)
               })
               .build()
      }

      fun CacheResultWrapper.bindStorageSet(storageSet: Set<ActionStorage<*>>): CacheResultWrapper {
         storageSet.forEach { bindStorage(it.actionClass, it) }
         return this
      }

      fun Janet.connectToSmartCardSdk() {
         this.createPipe(ConnectAction::class.java).createObservableResult(ConnectAction(ImmutableConnectionParams.of(1)))
               .toBlocking()
               .subscribe()
      }
   }
}
