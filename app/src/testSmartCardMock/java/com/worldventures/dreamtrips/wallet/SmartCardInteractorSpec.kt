package com.worldventures.dreamtrips.wallet

import android.content.Context
import android.test.mock.MockContext
import android.text.TextUtils
import com.nhaarman.mockito_kotlin.*
import com.worldventures.dreamtrips.AssertUtil.assertActionFail
import com.worldventures.dreamtrips.AssertUtil.assertActionSuccess
import com.worldventures.dreamtrips.BaseSpec
import com.worldventures.dreamtrips.core.janet.SessionActionPipeCreator
import com.worldventures.dreamtrips.core.janet.cache.CacheResultWrapper
import com.worldventures.dreamtrips.core.janet.cache.storage.ActionStorage
import com.worldventures.dreamtrips.core.janet.cache.storage.MultipleActionStorage
import com.worldventures.dreamtrips.core.repository.SnappyRepository
import com.worldventures.dreamtrips.wallet.domain.converter.BankCardToRecordConverter
import com.worldventures.dreamtrips.wallet.domain.converter.FinancialServiceToRecordFinancialServiceConverter
import com.worldventures.dreamtrips.wallet.domain.converter.RecordFinancialServiceToFinancialServiceConverter
import com.worldventures.dreamtrips.wallet.domain.converter.RecordToBankCardConverter
import com.worldventures.dreamtrips.wallet.domain.entity.*
import com.worldventures.dreamtrips.wallet.domain.entity.card.BankCard
import com.worldventures.dreamtrips.wallet.domain.storage.DefaultBankCardStorage
import com.worldventures.dreamtrips.wallet.domain.storage.SmartCardActionStorage
import com.worldventures.dreamtrips.wallet.domain.storage.WalletCardsDiskStorage
import com.worldventures.dreamtrips.wallet.domain.storage.disk.CardListStorage
import com.worldventures.dreamtrips.wallet.model.TestAddressInfo
import com.worldventures.dreamtrips.wallet.model.TestBankCard
import com.worldventures.dreamtrips.wallet.model.TestFirmware
import com.worldventures.dreamtrips.wallet.model.TestRecordIssuerInfo
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor
import com.worldventures.dreamtrips.wallet.service.SmartCardSyncManager
import com.worldventures.dreamtrips.wallet.service.command.*
import com.worldventures.dreamtrips.wallet.util.FormatException
import io.techery.janet.ActionState
import io.techery.janet.CommandActionService
import io.techery.janet.Janet
import io.techery.janet.SmartCardActionService
import io.techery.janet.http.test.MockHttpActionService
import io.techery.janet.smartcard.action.lock.GetLockDeviceStatusAction
import io.techery.janet.smartcard.action.records.DeleteRecordAction
import io.techery.janet.smartcard.action.support.ConnectAction
import io.techery.janet.smartcard.mock.client.MockSmartCardClient
import io.techery.janet.smartcard.model.ImmutableConnectionParams
import io.techery.janet.smartcard.model.Record
import io.techery.mappery.Mappery
import io.techery.mappery.MapperyContext
import org.junit.Assert
import org.powermock.api.mockito.PowerMockito
import rx.observers.TestSubscriber
import rx.schedulers.Schedulers

class SmartCardInteractorSpec : BaseSpec({
   describe("SmartCard SDK actions") {
      beforeEach {
         staticMockTextUtils()

         mockDb = createMockDb()
         cardStorage = mock()
         mappery = createMappery()
         janet = createJanet()
         smartCardInteractor = createSmartCardInteractor(janet)
         smartCardSyncManager = createSmartCardSyncManager(janet, smartCardInteractor)

         janet.connectToSmartCardSdk()
         smartCardSyncManager.connect()
      }

      context("SmartCard connection status should be changed") {

         it("Connect to smart card") {
            val smartCard: SmartCard = mockSmartCard("4")
            whenever(smartCard.cardStatus()).thenReturn(SmartCard.CardStatus.DRAFT)

            val testSubscriber: TestSubscriber<ActionState<ConnectSmartCardCommand>> = TestSubscriber()
            janet.createPipe(ConnectSmartCardCommand::class.java)
                  .createObservable(ConnectSmartCardCommand("4", false))
                  .subscribe(testSubscriber)

//            assertActionSuccess(testSubscriber, { it.result.connectionStatus() === ConnectionStatus.CONNECTED })
            verify(mockDb, times(1)).saveSmartCard(any())
         }

         it("Update SmartCard connection status") {
            val activeSmartCardId = "4"
            val smartCard: SmartCard = mockSmartCard(activeSmartCardId)
            whenever(mockDb.smartCard).thenReturn(smartCard)

            val connectionStatus = ConnectionStatus.DISCONNECTED
            val testSubscriber: TestSubscriber<ActionState<ActiveSmartCardCommand>> = TestSubscriber()
            janet.createPipe(ActiveSmartCardCommand::class.java)
                  .createObservable(ActiveSmartCardCommand({
                     ImmutableSmartCard.copyOf(it)
//                           .withConnectionStatus(connectionStatus)
                  }))
                  .subscribe(testSubscriber)

//            assertActionSuccess(testSubscriber, { it.result.connectionStatus() === ConnectionStatus.DISCONNECTED })
            verify(mockDb, atLeast(1)).saveSmartCard(any())
         }

      }

      context("Default card id is fetching") {

         it("should fetch default card id from storage") {
            val testSmartCardId: String = "4"

            whenever(mockDb.readWalletDefaultCardId()).thenReturn(testSmartCardId)
            assertActionSuccess(loadDefaultCardId(), { testSmartCardId == it.result })
         }

         it("should fetch default card id from sdk") {
            val testSmartCardId: String = "4"

            val testSubscriber = TestSubscriber<ActionState<DefaultCardIdCommand>>()
            janet.createPipe(DefaultCardIdCommand::class.java)
                  .observe()
                  .subscribe(testSubscriber)

            janet.createPipe(SetDefaultCardOnDeviceCommand::class.java)
                  .send(SetDefaultCardOnDeviceCommand.setAsDefault(testSmartCardId))

            assertActionSuccess(testSubscriber, { testSmartCardId == it.result })
         }
      }

      context("Add card") {

         it("should assigned ID after adding") {
            val debitCard = TestBankCard(null, TestRecordIssuerInfo(cardType = BankCard.CardType.DEBIT))

            val testSubscriber = TestSubscriber<ActionState<AttachCardCommand>>()
            smartCardInteractor.addRecordPipe()
                  .createObservable(AttachCardCommand(debitCard, false))
                  .subscribe(testSubscriber)

            assertActionSuccess(testSubscriber, { it.result.id() != null })
         }
      }

      context("Fetch list of cards") {
         val defaultCardId = "101"
         val debitCard = TestBankCard(null, TestRecordIssuerInfo(cardType = BankCard.CardType.DEBIT))
         val creditCard = TestBankCard(defaultCardId, TestRecordIssuerInfo(cardType = BankCard.CardType.CREDIT))

         beforeEach {
            val cardList = listOf(debitCard, creditCard)
            whenever(cardStorage.readWalletCardsList()).thenReturn(cardList)
            whenever(mockDb.readWalletDefaultCardId()).thenReturn(defaultCardId)
         }

         it("should fetch from cache") {
            fetchCardListOfCard { it.result.size == 2 }
         }


         it("should fetch from device, even if cache is present") {

            val testSubscriber = TestSubscriber<CardListCommand>()
            smartCardInteractor.cardsListPipe()
                  .observeSuccess()
                  .filter { it.hasOperationFunc() }
                  .subscribe(testSubscriber)

            smartCardInteractor.addRecordPipe()
                  .createObservableResult(AttachCardCommand(debitCard, false))
                  .subscribe()


            testSubscriber.unsubscribe()
            testSubscriber.assertNoErrors()
            testSubscriber.assertUnsubscribed()
            Assert.assertTrue(testSubscriber.onNextEvents.last().result.size == 3)
         }

         it("Add several card to SmartCard, check size after one by one options add") {
            smartCardInteractor.addRecordPipe()
                  .send(AttachCardCommand(debitCard, true))
            smartCardInteractor.addRecordPipe()
                  .send(AttachCardCommand(creditCard, false))

            fetchCardListOfCard {
               it.result.size == 2
            }
         }
      }

      context("Delete card") {
         val removedCardId = "51"
         val debitCard = TestBankCard(removedCardId, TestRecordIssuerInfo(cardType = BankCard.CardType.DEBIT))
         val creditCard = TestBankCard("52", TestRecordIssuerInfo(cardType = BankCard.CardType.CREDIT))

         beforeEach {
            // mock active SmartCard
            val smartCardId = "111"
            val smartCard = mockSmartCard(smartCardId)
            whenever(mockDb.smartCard).thenReturn(smartCard)

            // mock saving result after delete
            var list = listOf<BankCard>(debitCard, creditCard)
            whenever(cardStorage.readWalletCardsList()).thenAnswer { return@thenAnswer list }
            whenever(cardStorage.saveWalletCardsList(anyList())).thenAnswer { invocation ->
               list = invocation.arguments[0] as List<BankCard>
               return@thenAnswer null
            }
         }

         it("should delete item from cache of card list") {
            fetchCardListOfCard {
               it.result.find { it.id() == removedCardId } != null
            }

            smartCardInteractor.deleteCardPipe()
                  .createObservable(DeleteRecordAction(Integer.parseInt(removedCardId)))
                  .subscribe()

            fetchCardListOfCard {
               it.result.find { it.id() == removedCardId } == null
            }
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

         beforeEach {
            whenever(mockDb.readDefaultAddress()).thenReturn(addressInfo)
         }

         it("should fetch only from cache") {
            assertActionSuccess(loadDefaultAddress(), { addressInfo.address1() == it.result.address1() })
         }
      }

      context("Save bank card details data") {
         val bankCard = TestBankCard("11", TestRecordIssuerInfo())

         beforeEach {
            val smartCardId = "111"
            val smartCard = mockSmartCard(smartCardId)
            whenever(mockDb.smartCard).thenReturn(smartCard)
         }

         it("Card with valid data should be stored with default address and marked as default") {
            whenever(mockDb.readWalletDefaultCardId()).thenReturn(null)
            val subscriber = saveBankCardData(bankCard, setAsDefaultCard = true, setAsDefaultAddress = true)
            assertActionSuccess(subscriber, { true })

            verify(mockDb, times(1)).saveDefaultAddress(any())
            verify(mockDb, atLeast(1)).saveWalletDefaultCardId(any())
         }

         it("Card with valid data should be stored without default address and not marked as default") {
            val defaultCardId = "9"
            whenever(mockDb.readWalletDefaultCardId()).thenReturn(defaultCardId)

            val subscriber = saveBankCardData(bankCard, setAsDefaultCard = false, setAsDefaultAddress = false)
            assertActionSuccess(subscriber, { true })
            verify(mockDb, times(0)).saveDefaultAddress(any())
         }

         it("Card with invalid data shouldn't be stored") {
            val subscriber = saveBankCardData(bankCard, cvv = "pp", setAsDefaultCard = true, setAsDefaultAddress = true)
            assertActionFail(subscriber, { it.cause is FormatException })

            verify(mockDb, times(0)).saveDefaultAddress(any())
            verify(mockDb, times(0)).saveWalletDefaultCardId(any())
         }
      }
   }

}) {
   private companion object {
      lateinit var mockDb: SnappyRepository
      lateinit var janet: Janet
      lateinit var mappery: MapperyContext
      lateinit var smartCardInteractor: SmartCardInteractor
      lateinit var cardStorage: CardListStorage
      lateinit var smartCardSyncManager: SmartCardSyncManager

      val setOfMultiplyStorage: () -> Set<ActionStorage<*>> = {
         setOf(DefaultBankCardStorage(mockDb), SmartCardActionStorage(mockDb))
      }

      fun staticMockTextUtils() {
         PowerMockito.`mockStatic`(TextUtils::class.java)
         PowerMockito.`doAnswer`({ invocation ->
            val arg1: String = invocation.getArgumentAt(0, String::class.java)
            val arg2: String = invocation.getArgumentAt(1, String::class.java)
            arg1 == arg2
         }).`when`(TextUtils::class.java)
         TextUtils.`equals`(anyString(), anyString())
      }

      fun createSmartCardInteractor(janet: Janet) = SmartCardInteractor(SessionActionPipeCreator(janet), { Schedulers.immediate() })

      fun createSmartCardSyncManager(janet: Janet, smartCardInteractor: SmartCardInteractor) = SmartCardSyncManager(janet, smartCardInteractor)

      fun createJanet(): Janet {
         val daggerCommandActionService = CommandActionService()
               .wrapCache()
               .bindStorageSet(setOfMultiplyStorage())
               .bindStorageSet(setOf(WalletCardsDiskStorage(cardStorage)))
               .wrapDagger()

         janet = Janet.Builder()
               .addService(daggerCommandActionService)
               .addService(mockHttpService().wrapStub().wrapCache())
               .addService(SmartCardActionService.createDefault(MockSmartCardClient()))
               .build()

         daggerCommandActionService.registerProvider(Janet::class.java) { janet }
         daggerCommandActionService.registerProvider(SnappyRepository::class.java) { mockDb }
         daggerCommandActionService.registerProvider(CardListStorage::class.java) { cardStorage }
         daggerCommandActionService.registerProvider(MapperyContext::class.java) { mappery }
         daggerCommandActionService.registerProvider(Context::class.java, { MockContext() })
         daggerCommandActionService.registerProvider(SmartCardInteractor::class.java, { smartCardInteractor })

         return janet
      }

      fun createMockDb(): SnappyRepository = spy()

      fun createMappery(): MapperyContext = Mappery.Builder()
            .map(BankCard::class.java).to(Record::class.java, BankCardToRecordConverter())
            .map(Record::class.java).to(BankCard::class.java, RecordToBankCardConverter())
            .map(FinancialService::class.java).to(Record.FinancialService::class.java, FinancialServiceToRecordFinancialServiceConverter())
            .map(Record.FinancialService::class.java).to(FinancialService::class.java, RecordFinancialServiceToFinancialServiceConverter())
            .build()

      fun loadDefaultCardId(): TestSubscriber<ActionState<DefaultCardIdCommand>> {
         val testSubscriber = TestSubscriber<ActionState<DefaultCardIdCommand>>()

         smartCardInteractor.defaultCardIdPipe()
               .createObservable(DefaultCardIdCommand())
               .subscribe(testSubscriber)
         return testSubscriber
      }

      fun fetchCardListOfCard(predicate: (command: CardListCommand) -> Boolean): Unit {
         val testSubscriber: TestSubscriber<ActionState<CardListCommand>> = TestSubscriber()

         smartCardInteractor.cardsListPipe()
               .createObservable(CardListCommand.fetch())
               .subscribe(testSubscriber)
         assertActionSuccess(testSubscriber, { predicate(it) })
      }

      fun loadDefaultAddress(): TestSubscriber<ActionState<GetDefaultAddressCommand>> {
         val testSubscriber = TestSubscriber<ActionState<GetDefaultAddressCommand>>()

         smartCardInteractor.defaultAddressCommandPipe
               .createObservable(GetDefaultAddressCommand())
               .subscribe(testSubscriber)
         return testSubscriber
      }

      fun saveBankCardData(bankCard: BankCard,
                           setAsDefaultAddress: Boolean,
                           setAsDefaultCard: Boolean,
                           useDefaultAddress: Boolean = false,
                           issuerInfo: RecordIssuerInfo = TestRecordIssuerInfo(),
                           manualAddressInfo: AddressInfo = TestAddressInfo(),
                           nickName: String = "Test Card",
                           cvv: String = "000"): TestSubscriber<ActionState<AddBankCardCommand>> {
         // by default, mock payment card number is 123456789. It mean then cvv should be contain only 3 digit.
         val testSubscriber = TestSubscriber<ActionState<AddBankCardCommand>>()
         val cmd = AddBankCardCommand.Builder()
               .setBankCard(bankCard)
               .setManualAddressInfo(manualAddressInfo)
               .setCardName(nickName)
               .setCvv(cvv)
               .setIssuerInfo(issuerInfo)
               .setUseDefaultAddress(useDefaultAddress)
               .setSetAsDefaultAddress(setAsDefaultAddress)
               .setSetAsDefaultCard(setAsDefaultCard).create();

         smartCardInteractor.saveCardDetailsDataPipe()
               .createObservable(cmd)
               .subscribe(testSubscriber)
         return testSubscriber
      }

      fun mockHttpService(): MockHttpActionService {
         return MockHttpActionService.Builder().build()
      }

      fun CacheResultWrapper.bindStorageSet(storageSet: Set<ActionStorage<*>>): CacheResultWrapper {
         storageSet.forEach { bindStorage(it.actionClass, it) }
         return this
      }

      fun CacheResultWrapper.bindMultiplyStorageSet(storageSet: Set<MultipleActionStorage<*>>): CacheResultWrapper {
         storageSet.flatMap { it.actionClasses.map { actionClass -> actionClass to it } }.forEach { bindStorage(it.first, it.second) }
         return this
      }

      fun Janet.connectToSmartCardSdk() {
         this.createPipe(ConnectAction::class.java).createObservableResult(ConnectAction(ImmutableConnectionParams.of(1)))
               .toBlocking()
               .subscribe()
      }

      fun mockSmartCard(cardId: String): SmartCard {
         val mockedSmartCard: SmartCard = mock()
         whenever(mockedSmartCard.smartCardId()).thenReturn(cardId)
         whenever(mockedSmartCard.cardStatus()).thenReturn(SmartCard.CardStatus.ACTIVE)
//         whenever(mockedSmartCard.connectionStatus()).thenReturn(ConnectionStatus.DISCONNECTED)
         whenever(mockedSmartCard.deviceAddress()).thenReturn("device address")
         whenever(mockedSmartCard.sdkVersion()).thenReturn("1.0.0")
         whenever(mockedSmartCard.firmwareVersion()).thenReturn(TestFirmware())
         whenever(mockedSmartCard.serialNumber()).thenReturn("")
         whenever(mockedSmartCard.user()).thenReturn(mock())

         return mockedSmartCard
      }
   }
}

