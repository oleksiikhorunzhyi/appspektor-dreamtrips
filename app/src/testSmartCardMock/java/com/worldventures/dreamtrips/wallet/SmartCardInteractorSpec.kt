package com.worldventures.dreamtrips.wallet

import android.content.Context
import android.test.mock.MockContext
import android.text.TextUtils
import com.nhaarman.mockito_kotlin.*
import com.techery.spares.session.NxtSessionHolder
import com.techery.spares.storage.complex_objects.Optional
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
import com.worldventures.dreamtrips.wallet.domain.entity.AddressInfo
import com.worldventures.dreamtrips.wallet.domain.entity.FinancialService
import com.worldventures.dreamtrips.wallet.domain.entity.RecordIssuerInfo
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCard
import com.worldventures.dreamtrips.wallet.domain.entity.card.BankCard
import com.worldventures.dreamtrips.wallet.domain.storage.DefaultBankCardStorage
import com.worldventures.dreamtrips.wallet.domain.storage.SmartCardActionStorage
import com.worldventures.dreamtrips.wallet.domain.storage.WalletCardsDiskStorage
import com.worldventures.dreamtrips.wallet.domain.storage.disk.PersistentWalletCardsStorage
import com.worldventures.dreamtrips.wallet.model.*
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor
import com.worldventures.dreamtrips.wallet.service.SmartCardSyncManager
import com.worldventures.dreamtrips.wallet.service.command.*
import com.worldventures.dreamtrips.wallet.service.nxt.NxtInteractor
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
         nxtInteractor = createNxtInteractor(janet)
         nxtSessionHolder = mock()

         janet.connectToSmartCardSdk()
         smartCardSyncManager.connect()
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
            val nxtBankCard = TestNxtBankCard(TestBankCard(null, TestRecordIssuerInfo(cardType = BankCard.CardType.DEBIT)))

            val testSubscriber = TestSubscriber <ActionState <AttachCardCommand>>()
            smartCardInteractor.addRecordPipe()
                  .createObservable(AttachCardCommand(nxtBankCard, false))
                  .subscribe(testSubscriber)

            assertActionSuccess(testSubscriber, { it.result.id() != null })
         }
      }

      context("Fetch list of cards") {
         val defaultCardId = "101"

         val debitCard = TestBankCard(null, TestRecordIssuerInfo(cardType = BankCard.CardType.DEBIT))
         val creditCard = TestBankCard(defaultCardId, TestRecordIssuerInfo(cardType = BankCard.CardType.CREDIT))

         val nxtDebitCard = TestNxtBankCard(debitCard)
         val nxtCreditCard = TestNxtBankCard(creditCard)

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
                  .createObservableResult(AttachCardCommand(nxtDebitCard, false))
                  .subscribe()


            testSubscriber.unsubscribe()
            testSubscriber.assertNoErrors()
            testSubscriber.assertUnsubscribed()
            Assert.assertTrue(testSubscriber.onNextEvents.last().result.size == 3)
         }

         it("Add several card to SmartCard, check size after one by one options add") {
            smartCardInteractor.addRecordPipe()
                  .send(AttachCardCommand(nxtDebitCard, true))
            smartCardInteractor.addRecordPipe()
                  .send(AttachCardCommand(nxtCreditCard, false))

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
            whenever(nxtSessionHolder.get()).thenReturn(Optional.of(TestNxtSession("testNxtSessionToken")))
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
      lateinit var cardStorage: PersistentWalletCardsStorage
      lateinit var smartCardSyncManager: SmartCardSyncManager
      lateinit var nxtInteractor: NxtInteractor
      lateinit var nxtSessionHolder: NxtSessionHolder

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

         PowerMockito.`doAnswer`({ invocation ->
            val arg1: String? = invocation.getArgumentAt(0, String::class.java)
            arg1 == null || arg1.isEmpty()
         }).`when`(TextUtils::class.java)
         TextUtils.`isEmpty`(anyString())
      }

      fun createSmartCardInteractor(janet: Janet) = SmartCardInteractor(SessionActionPipeCreator(janet), { Schedulers.immediate() })

      fun createNxtInteractor(janet: Janet) = NxtInteractor(janet)

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
         daggerCommandActionService.registerProvider(PersistentWalletCardsStorage::class.java) { cardStorage }
         daggerCommandActionService.registerProvider(MapperyContext::class.java) { mappery }
         daggerCommandActionService.registerProvider(Context::class.java, { MockContext() })
         daggerCommandActionService.registerProvider(SmartCardInteractor::class.java, { smartCardInteractor })
         daggerCommandActionService.registerProvider(NxtInteractor::class.java, { nxtInteractor })
         daggerCommandActionService.registerProvider(NxtSessionHolder::class.java, { nxtSessionHolder })

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
               .setSetAsDefaultCard(setAsDefaultCard)
               .create()

         smartCardInteractor.saveCardDetailsDataPipe()
               .createObservable(cmd)
               .subscribe(testSubscriber)
         return testSubscriber
      }

      fun mockHttpService(): MockHttpActionService {
         return MockHttpActionService.Builder()
               .bind(MockHttpActionService.Response(200).body(TestMultiResponseBody()), { request ->
                  request.url.contains("multifunction") && request.method.equals("post", true)
               })
               .build()
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
//         whenever(mockedSmartCard.sdkVersion()).thenReturn("1.0.0")

         return mockedSmartCard
      }
   }
}

