package com.worldventures.dreamtrips.wallet

import android.content.Context
import android.test.mock.MockContext
import com.nhaarman.mockito_kotlin.*
import com.techery.spares.session.NxtSessionHolder
import com.worldventures.dreamtrips.AssertUtil.assertActionFail
import com.worldventures.dreamtrips.AssertUtil.assertActionSuccess
import com.worldventures.dreamtrips.BaseSpec
import com.worldventures.dreamtrips.core.janet.SessionActionPipeCreator
import com.worldventures.dreamtrips.core.janet.cache.CacheResultWrapper
import com.worldventures.dreamtrips.core.janet.cache.storage.ActionStorage
import com.worldventures.dreamtrips.core.repository.SnappyRepository
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsInteractor
import com.worldventures.dreamtrips.wallet.domain.converter.*
import com.worldventures.dreamtrips.wallet.domain.entity.AddressInfo
import com.worldventures.dreamtrips.wallet.domain.entity.record.FinancialService
import com.worldventures.dreamtrips.wallet.domain.entity.record.Record
import com.worldventures.dreamtrips.wallet.domain.entity.record.RecordType
import com.worldventures.dreamtrips.wallet.domain.storage.DefaultRecordIdStorage
import com.worldventures.dreamtrips.wallet.domain.storage.SmartCardActionStorage
import com.worldventures.dreamtrips.wallet.domain.storage.WalletRecordsDiskStorage
import com.worldventures.dreamtrips.wallet.domain.storage.disk.RecordsStorage
import com.worldventures.dreamtrips.wallet.domain.storage.disk.TestRecordsStorage
import com.worldventures.dreamtrips.wallet.model.TestAddressInfo
import com.worldventures.dreamtrips.wallet.model.TestMultiResponseBody
import com.worldventures.dreamtrips.wallet.model.TestRecord
import com.worldventures.dreamtrips.wallet.service.*
import com.worldventures.dreamtrips.wallet.service.command.GetDefaultAddressCommand
import com.worldventures.dreamtrips.wallet.service.command.RecordListCommand
import com.worldventures.dreamtrips.wallet.service.command.SetDefaultCardOnDeviceCommand
import com.worldventures.dreamtrips.wallet.service.command.SetLockStateCommand
import com.worldventures.dreamtrips.wallet.service.command.offline_mode.OfflineModeStatusCommand
import com.worldventures.dreamtrips.wallet.service.command.offline_mode.RestoreOfflineModeDefaultStateCommand
import com.worldventures.dreamtrips.wallet.service.command.offline_mode.SwitchOfflineModeCommand
import com.worldventures.dreamtrips.wallet.service.command.record.AddRecordCommand
import com.worldventures.dreamtrips.wallet.service.command.record.DefaultRecordIdCommand
import com.worldventures.dreamtrips.wallet.service.command.record.DeleteRecordCommand
import com.worldventures.dreamtrips.wallet.service.command.record.UpdateRecordCommand
import com.worldventures.dreamtrips.wallet.service.nxt.NxtInteractor
import com.worldventures.dreamtrips.wallet.service.nxt.TokenizeMultipleRecordsCommand
import com.worldventures.dreamtrips.wallet.util.FormatException
import com.worldventures.dreamtrips.wallet.util.NetworkUnavailableException
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
            val testSmartCardId: String = "4"

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

         val debitCard = TestRecord(null, cardType = RecordType.DEBIT)
         val creditCard = TestRecord(null, cardType = RecordType.CREDIT)

         it("creates record with ID") {
            val testSubscriber = addRecord(debitCard)
            assertActionSuccess(testSubscriber, { it.result.id() != null })
         }

         it("creates record with saving address as default") {
            val testSubscriber = addRecord(debitCard, setAsDefaultCard = true, setAsDefaultAddress = true)
            assertActionSuccess(testSubscriber, { it.result.id() != null })
            verify(mockDb, times(1)).saveDefaultAddress(any())
         }

         it("creates record and saves default record id") {
            val testSubscriber = addRecord(debitCard, setAsDefaultCard = true, setAsDefaultAddress = true)

            var savedRecord: Record? = null
            assertActionSuccess(testSubscriber, {
               savedRecord = it.result
               it.result.id() != null
            })

            val defaultIdSubscriber = loadDefaultCardId()
            assertActionSuccess(defaultIdSubscriber, { it.result == savedRecord?.id() })
         }

         it("throws FormatException when creating record with invalid data") {
            val subscriber = addRecord(debitCard, cvv = "pp", setAsDefaultCard = true, setAsDefaultAddress = true)
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
               it.result.id() != null
            })

            val updateRecordSubscriber = TestSubscriber<ActionState<UpdateRecordCommand>>()
            recordInteractor.updateRecordPipe()
                  .createObservable(UpdateRecordCommand.updateNickName(savedRecord, newRecordName))
                  .subscribe(updateRecordSubscriber)

            assertActionSuccess(updateRecordSubscriber, { true })
            fetchCardListOfCard { it.result.size == 1 }
            fetchCardListOfCard { it.result[0].nickName() == newRecordName }
         }

         it("deletes record") {
            var recordToDelete: Record? = null

            val testSubscriber = addRecord(debitCard)
            assertActionSuccess(testSubscriber, {
               recordToDelete = it.result
               it.result.id() != null
            })

            fetchCardListOfCard { it.result.find { it.id() == recordToDelete?.id() } != null }

            recordInteractor.deleteRecordPipe()
                  .createObservable(DeleteRecordCommand(recordToDelete?.id()))
                  .subscribe()

            fetchCardListOfCard { it.result.find { it.id() == recordToDelete?.id() } == null }
         }
      }

      context("Tokenization and Offline mode") {

         val defaultOfflineModeState = false
         val testRecord = TestRecord(null)

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
                  .flatMap { v ->
                     smartCardInteractor.offlineModeStatusPipe().createObservable(OfflineModeStatusCommand.fetch())
                  }
                  .subscribe(stateDefaultSubscriber)

            assertActionSuccess(stateNotDefaultSubscriber, { it.result == !defaultOfflineModeState })
            assertActionSuccess(stateDefaultSubscriber, { it.result == defaultOfflineModeState })
         }

         it("record is tokenized properly") {

            val testSubscriber = TestSubscriber<ActionState<TokenizeMultipleRecordsCommand>>()
            nxtInteractor.tokenizeMultipleRecordsPipe().createObservable(TokenizeMultipleRecordsCommand(listOf(
                  TestRecord("0", cvv = "123"),
                  TestRecord("1", number = "0000111122223333")),
                  true))
                  .subscribe(testSubscriber)

            assertActionSuccess(testSubscriber, { it.result[0].cvv() == TestMultiResponseBody.TEST_CVV })
            assertActionSuccess(testSubscriber, { it.result[1].number() == TestMultiResponseBody.TEST_NUMBER })
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
            fetchCardListOfCard { it.result[0].cvv() == TestMultiResponseBody.TEST_CVV }
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

      context("Fetch default address") {
         val addressInfo = TestAddressInfo()
         beforeEachTest {
            whenever(mockDb.readDefaultAddress()).thenReturn(addressInfo)
         }

         it("should fetch only from cache") {
            assertActionSuccess(loadDefaultAddress(), { addressInfo.address1() == it.result.address1() })
         }
      }
   }
}) {
   private companion object {
      lateinit var mockDb: SnappyRepository
      lateinit var janet: Janet
      lateinit var mappery: MapperyContext
      lateinit var smartCardInteractor: SmartCardInteractor
      lateinit var firmwareInteractor: FirmwareInteractor
      lateinit var recordInteractor: RecordInteractor
      lateinit var cardStorage: RecordsStorage
      lateinit var smartCardSyncManager: SmartCardSyncManager
      lateinit var nxtInteractor: NxtInteractor
      lateinit var nxtSessionHolder: NxtSessionHolder
      lateinit var analyticsInteractor: AnalyticsInteractor
      lateinit var networkService: WalletNetworkService

      val setOfMultiplyStorage: () -> Set<ActionStorage<*>> = {
         setOf(DefaultRecordIdStorage(cardStorage), SmartCardActionStorage(mockDb))
      }

      fun createSmartCardInteractor(janet: Janet) = SmartCardInteractor(SessionActionPipeCreator(janet), { Schedulers.immediate() })

      fun createFirmwareInteractor(janet: Janet) = FirmwareInteractor(janet)

      fun createRecordInteractor(janet: Janet) = RecordInteractor(SessionActionPipeCreator(janet), { Schedulers.immediate() })

      fun createNxtInteractor(janet: Janet) = NxtInteractor(janet)

      fun createAnalyticsInteractor(janet: Janet) = AnalyticsInteractor(SessionActionPipeCreator(janet))

      fun createSmartCardSyncManager(janet: Janet, smartCardInteractor: SmartCardInteractor, firmwareInteractor: FirmwareInteractor, recordInteractor: RecordInteractor) = SmartCardSyncManager(janet, smartCardInteractor, firmwareInteractor, recordInteractor)

      fun createJanet(): Janet {
         val daggerCommandActionService = CommandActionService()
               .wrapCache()
               .bindStorageSet(setOfMultiplyStorage())
               .bindStorageSet(setOf(WalletRecordsDiskStorage(cardStorage)))
               .wrapDagger()

         janet = Janet.Builder()
               .addService(daggerCommandActionService)
               .addService(mockHttpService().wrapStub().wrapCache())
               .addService(SmartCardActionService.createDefault(MockSmartCardClient()))
               .build()

         daggerCommandActionService.registerProvider(Janet::class.java) { janet }
         daggerCommandActionService.registerProvider(SnappyRepository::class.java) { mockDb }
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

         return janet
      }

      fun createMockDb(): SnappyRepository = spy()

      fun createMappery(): MapperyContext = Mappery.Builder()
            .map(Record::class.java).to(io.techery.janet.smartcard.model.Record::class.java, WalletRecordToSmartCardRecordConverter())
            .map(io.techery.janet.smartcard.model.Record::class.java).to(Record::class.java, SmartCardRecordToWalletRecordConverter())

            .map(RecordType::class.java).to(io.techery.janet.smartcard.model.Record.CardType::class.java, WalletRecordTypeToSmartCardRecordTypeConverter())
            .map(io.techery.janet.smartcard.model.Record.CardType::class.java).to(RecordType::class.java, SmartCardRecordTypeToWalletRecordTypeConverter())

            .map(FinancialService::class.java).to(io.techery.janet.smartcard.model.Record.FinancialService::class.java, WalletFinancialServiceToSmartCardFinancialServiceConverter())
            .map(io.techery.janet.smartcard.model.Record.FinancialService::class.java).to(FinancialService::class.java, SmartCardFinancialServiceToWalletFinancialServiceConverter())
            .build()

      fun loadDefaultCardId(): TestSubscriber<ActionState<DefaultRecordIdCommand>> {
         val testSubscriber = TestSubscriber<ActionState<DefaultRecordIdCommand>>()

         recordInteractor.defaultRecordIdPipe()
               .createObservable(DefaultRecordIdCommand.fetch())
               .subscribe(testSubscriber)
         return testSubscriber
      }

      fun fetchCardListOfCard(predicate: (command: RecordListCommand) -> Boolean): Unit {
         val testSubscriber: TestSubscriber<ActionState<RecordListCommand>> = TestSubscriber()

         recordInteractor.cardsListPipe()
               .createObservable(RecordListCommand.fetch())
               .subscribe(testSubscriber)
         assertActionSuccess(testSubscriber, { predicate(it) })
      }

      fun loadDefaultAddress(): TestSubscriber<ActionState<GetDefaultAddressCommand>> {
         val testSubscriber = TestSubscriber<ActionState<GetDefaultAddressCommand>>()

         recordInteractor.defaultAddressCommandPipe
               .createObservable(GetDefaultAddressCommand())
               .subscribe(testSubscriber)
         return testSubscriber
      }

      fun addRecord(record: Record,
                    setAsDefaultAddress: Boolean = false,
                    setAsDefaultCard: Boolean = false,
                    useDefaultAddress: Boolean = false,
                    manualAddressInfo: AddressInfo = TestAddressInfo(),
                    nickName: String = "Test Card",
                    cvv: String = "000"): TestSubscriber<ActionState<AddRecordCommand>> {
         // by default, mock payment card number is 123456789. It mean then cvv should be contain only 3 digit.
         val testSubscriber = TestSubscriber<ActionState<AddRecordCommand>>()
         val cmd = AddRecordCommand.Builder()
               .setRecord(record)
               .setManualAddressInfo(manualAddressInfo)
               .setRecordName(nickName)
               .setCvv(cvv)
               .setUseDefaultAddress(useDefaultAddress)
               .setSetAsDefaultAddress(setAsDefaultAddress)
               .setSetAsDefaultRecord(setAsDefaultCard)
               .create()

         recordInteractor.addRecordPipe()
               .createObservable(cmd)
               .subscribe(testSubscriber)
         return testSubscriber
      }

      fun mockHttpService(): MockHttpActionService {
         return MockHttpActionService.Builder()
               .bind(MockHttpActionService.Response(200).body(TestMultiResponseBody(listOf(null, "0", "1", "2"))), { request ->
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