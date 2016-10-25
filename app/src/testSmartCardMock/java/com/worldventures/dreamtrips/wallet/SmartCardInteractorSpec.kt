package com.worldventures.dreamtrips.wallet

import android.content.Context
import android.test.mock.MockContext
import android.text.TextUtils
import com.nhaarman.mockito_kotlin.*
import com.worldventures.dreamtrips.AssertUtil
import com.worldventures.dreamtrips.AssertUtil.assertActionSuccess
import com.worldventures.dreamtrips.BaseSpec
import com.worldventures.dreamtrips.core.janet.SessionActionPipeCreator
import com.worldventures.dreamtrips.core.janet.cache.CacheResultWrapper
import com.worldventures.dreamtrips.core.janet.cache.storage.ActionStorage
import com.worldventures.dreamtrips.core.janet.cache.storage.MultipleActionStorage
import com.worldventures.dreamtrips.core.repository.SnappyRepository
import com.worldventures.dreamtrips.wallet.domain.converter.BankCardToRecordConverter
import com.worldventures.dreamtrips.wallet.domain.converter.RecordToBankCardConverter
import com.worldventures.dreamtrips.wallet.domain.entity.AddressInfo
import com.worldventures.dreamtrips.wallet.domain.entity.RecordIssuerInfo
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCard
import com.worldventures.dreamtrips.wallet.domain.entity.card.BankCard
import com.worldventures.dreamtrips.wallet.domain.entity.card.BankCard.CardType
import com.worldventures.dreamtrips.wallet.domain.entity.card.Card
import com.worldventures.dreamtrips.wallet.domain.storage.DefaultBankCardStorage
import com.worldventures.dreamtrips.wallet.domain.storage.SmartCardStorage
import com.worldventures.dreamtrips.wallet.domain.storage.WalletCardsDiskStorage
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor
import com.worldventures.dreamtrips.wallet.service.command.*
import com.worldventures.dreamtrips.wallet.service.command.CardStacksCommand.CardStackModel.StackType
import com.worldventures.dreamtrips.wallet.util.FormatException
import io.techery.janet.ActionState
import io.techery.janet.CommandActionService
import io.techery.janet.Janet
import io.techery.janet.SmartCardActionService
import io.techery.janet.http.test.MockHttpActionService
import io.techery.janet.smartcard.action.lock.GetLockDeviceStatusAction
import io.techery.janet.smartcard.action.records.DeleteRecordAction
import io.techery.janet.smartcard.action.records.SetRecordAsDefaultAction
import io.techery.janet.smartcard.action.support.ConnectAction
import io.techery.janet.smartcard.mock.client.MockSmartCardClient
import io.techery.janet.smartcard.model.ImmutableConnectionParams
import io.techery.janet.smartcard.model.Record
import io.techery.mappery.Mappery
import io.techery.mappery.MapperyContext
import org.powermock.api.mockito.PowerMockito
import rx.functions.Func1
import rx.observers.TestSubscriber

class SmartCardInteractorSpec : BaseSpec({
   describe("SmartCard SDK actions") {
      beforeEach {
         staticMockTextUtils()

         mockDb = createMockDb()
         mappery = createMappery()
         janet = createJanet()
         smartCardInteractor = createInteractor(janet)

         janet.connectToSmartCardSdk()

         mockedDebitCard = mock()
         mockedCreditCard = mock()
         mockedDefaultCard = mock()
         mockedAddressInfo = mock()
         mockedIssuerInfo = mock()

         prepareCardsAndAddressMock()

         mockedListOfCards = mutableListOf(mockedDebitCard, mockedCreditCard, mockedDefaultCard)
      }

      context("Smart Card connection status should be changed") {

         it("Connect to smart card") {
            val smartcard: SmartCard = mockSmartCard("4")
            whenever(smartcard.cardStatus()).thenReturn(SmartCard.CardStatus.DRAFT)

            val testSubscriber: TestSubscriber<ActionState<ConnectSmartCardCommand>> = TestSubscriber()
            janet.createPipe(ConnectSmartCardCommand::class.java)
                  .createObservable(ConnectSmartCardCommand(smartcard))
                  .subscribe(testSubscriber)

            assertActionSuccess(testSubscriber, { it.result.connectionStatus() === SmartCard.ConnectionStatus.CONNECTED })
            verify(mockDb, times(1)).saveSmartCard(any())
         }

         it("set disconnect status if the event is thrown") {
            val activeSmartCardId = "4"
            val smartcard: SmartCard = mockSmartCard(activeSmartCardId)
            whenever(mockDb.getActiveSmartCardId()).thenReturn(activeSmartCardId)
            whenever(mockDb.getSmartCard(activeSmartCardId)).thenReturn(smartcard)

            val testSubscriber: TestSubscriber<ActionState<UpdateSmartCardConnectionStatus>> = TestSubscriber()
            janet.createPipe(UpdateSmartCardConnectionStatus::class.java)
                  .createObservable(UpdateSmartCardConnectionStatus(SmartCard.ConnectionStatus.DISCONNECTED))
                  .subscribe(testSubscriber)

            assertActionSuccess(testSubscriber, { it.result.connectionStatus() === SmartCard.ConnectionStatus.DISCONNECTED })
            verify(mockDb, times(1)).saveSmartCard(any())
         }

      }

      context("Default card id is fetching") {

         it("should fetch default card id from storage") {
            val testSmartCardId: String = "4"

            whenever(mockDb.readWalletDefaultCardId()).thenReturn(testSmartCardId)
            assertActionSuccess(loadDefaultCardId(), { testSmartCardId == it.result })
         }

         it("should get default card id from sdk") {
            val testSmartCardId: String = "4"

            janet.createPipe(SetRecordAsDefaultAction::class.java)
                  .createObservableResult(SetRecordAsDefaultAction(testSmartCardId.toInt()))
                  .subscribe()
            assertActionSuccess(loadDefaultCardId(true), { testSmartCardId == it.result })
         }
      }

      context("Fetch list of cards") {
         beforeEach {
            whenever(mockDb.readWalletCardsList()).thenReturn(mockedListOfCards)
            whenever(mockDb.readWalletDefaultCardId()).thenReturn(TEST_DEFAULT_CARD_ID.toString())
         }

         it("should fetch from cache") {
            fetchCardStackListOfCard { it.result.size == 1 }
         }

         it("should fetch from device, even if cache is present") {
            smartCardInteractor.addRecordPipe()
                  .send(AttachCardCommand(mockedDebitCard, false))

            fetchCardStackListOfCard(true) {
               it.result.size == 1 &&
                     it.result[0].bankCards.size == 1 &&
                     it.result[0].stackStackType === StackType.PAYMENT
            }
         }

         it("Add several card to smartCard, check size after one by one options add") {
            smartCardInteractor.addRecordPipe()
                  .send(AttachCardCommand(mockedDebitCard, true))
            smartCardInteractor.addRecordPipe()
                  .send(AttachCardCommand(mockedDebitCard, false))

            fetchCardStackListOfCard(true) {
               it.result.size == 1 &&
                     it.result[0].bankCards.size == 2
            }
         }
      }

      context("Delete card") {
         beforeEach {
            whenever(mockDb.readWalletCardsList()).thenReturn(mockedListOfCards)
         }

         it("should delete item from cache of card list") {
            val testSubscriber: TestSubscriber<ActionState<CardStacksCommand>> = TestSubscriber()

            smartCardInteractor.cardStacksPipe()
                  .observe()
                  .subscribe(testSubscriber)
            smartCardInteractor.deleteCardPipe()
                  .createObservable(DeleteRecordAction(TEST_CARD_ID))
                  .subscribe()

            assertActionSuccess(testSubscriber, { it.result.flatMap { it.bankCards }.find { it.id() == TEST_CARD_ID.toString() } == null })
         }
      }

      context("Lock state is fetching") {

         fun getLockState(): TestSubscriber<ActionState<GetLockDeviceStatusAction>> {
            val testSubscriber = TestSubscriber<ActionState<GetLockDeviceStatusAction>>()
            janet.createPipe(GetLockDeviceStatusAction::class.java).createObservable(GetLockDeviceStatusAction())
                  .subscribe(testSubscriber)
            return testSubscriber
         }

         it("should get lock state") {
            smartCardInteractor.lockPipe().createObservableResult(SetLockStateCommand(true)).subscribe()
            assertActionSuccess(getLockState(), { it.locked })
         }
      }

      context("Save default address") {
         it("should save default address only in cache") {
            assertActionSuccess(saveDefaultAddress(mockedAddressInfo), { true })
            verify(mockDb, times(1)).saveDefaultAddress(mockedAddressInfo)
         }
      }

      context("Fetch default address") {
         beforeEach {
            whenever(mockDb.readDefaultAddress()).thenReturn(mockedAddressInfo)
         }

         it("should fetch only from cache") {
            assertActionSuccess(loadDefaultAddress(), { mockedAddressInfo.address1() == it.result.address1() })
         }
      }

      context("Save bank card details data") {

         it("Card with valid data should be stored with default address and marked as default") {
            whenever(mockDb.readWalletDefaultCardId()).thenReturn(null)
            assertActionSuccess(saveBankCardData(bankCard = mockedDebitCard, issuerInfo = mockedIssuerInfo, setAsDefaultAddress = true,
                  setAsDefaultCard = true, cvv = "000"), { true })

            verify(mockDb, times(1)).saveDefaultAddress(any())
            verify(mockDb, times(2)).saveWalletDefaultCardId(any())
         }

         it("Card with valid data should be stored without default address and not marked as default") {
            val defaultCardId = "9"
            whenever(mockDb.readWalletDefaultCardId()).thenReturn(defaultCardId)

            AssertUtil.assertActionSuccess(saveBankCardData(bankCard = mockedDebitCard, issuerInfo = mockedIssuerInfo, setAsDefaultCard = false,
                  cvv = "000"), { true })
            verify(mockDb, times(0)).saveDefaultAddress(any())
            //method below shouldn't be called at all, but it's called because of
            // implementation CachedAction interface by FetchDefaultCardCommand
            verify(mockDb, times(1)).saveWalletDefaultCardId(any())
            verify(mockDb, times(1)).saveWalletDefaultCardId(defaultCardId)
         }

         it("Card with invalid data shouldn't be stored") {
            AssertUtil.assertActionFail(saveBankCardData(bankCard = mockedDebitCard, cvv = "pp", issuerInfo = mockedIssuerInfo), { it.cause is FormatException })

            verify(mockDb, times(0)).saveDefaultAddress(any())
            verify(mockDb, times(0)).saveWalletDefaultCardId(any())
         }
      }
   }

}) {
   private companion object {
      val TEST_CARD_ID = 1
      val TEST_DEFAULT_CARD_ID = 101

      lateinit var mockDb: SnappyRepository
      lateinit var janet: Janet
      lateinit var mappery: MapperyContext
      lateinit var smartCardInteractor: SmartCardInteractor

      lateinit var mockedDebitCard: BankCard
      lateinit var mockedCreditCard: BankCard
      lateinit var mockedDefaultCard: BankCard
      lateinit var mockedAddressInfo: AddressInfo

      lateinit var mockedIssuerInfo: RecordIssuerInfo

      lateinit var mockedListOfCards: List<BankCard>

      val setOfMultiplyStorage: () -> Set<MultipleActionStorage<*>> = {
         setOf(DefaultBankCardStorage(mockDb), SmartCardStorage(mockDb))
      }

      fun staticMockTextUtils() {
         PowerMockito.`mockStatic`(TextUtils::class.java)
         PowerMockito.`doAnswer`({ invocation ->
            val arg1: String = invocation.getArgumentAt(0, String::class.java)
            var arg2: String = invocation.getArgumentAt(1, String::class.java)
            arg1 == arg2
         }).`when`(TextUtils::class.java)
         TextUtils.`equals`(anyString(), anyString())
      }

      fun createInteractor(janet: Janet) = SmartCardInteractor(janet, SessionActionPipeCreator(janet))

      fun createJanet(): Janet {
         val daggerCommandActionService = CommandActionService()
               .wrapCache()
               .bindMultiplyStorageSet(setOfMultiplyStorage())
               .bindStorageSet(setOf(WalletCardsDiskStorage(mockDb)))
               .wrapDagger()

         janet = Janet.Builder()
               .addService(daggerCommandActionService)
               .addService(mockHttpService().wrapStub().wrapCache())
               .addService(SmartCardActionService.createDefault(MockSmartCardClient()))
               .build()

         daggerCommandActionService.registerProvider(Janet::class.java) { janet }
         daggerCommandActionService.registerProvider(SnappyRepository::class.java) { mockDb }
         daggerCommandActionService.registerProvider(MapperyContext::class.java) { mappery }
         daggerCommandActionService.registerProvider(Context::class.java, { MockContext() })
         daggerCommandActionService.registerProvider(SmartCardInteractor::class.java, { smartCardInteractor })

         return janet
      }

      fun createMockDb(): SnappyRepository = spy()

      fun createMappery(): MapperyContext = Mappery.Builder()
            .map(BankCard::class.java).to(Record::class.java, BankCardToRecordConverter())
            .map(Record::class.java).to(BankCard::class.java, RecordToBankCardConverter())
            .build()

      fun loadDefaultCardId(force: Boolean = false): TestSubscriber<ActionState<FetchDefaultCardIdCommand>> {
         val testSubscriber = TestSubscriber<ActionState<FetchDefaultCardIdCommand>>()

         smartCardInteractor.fetchDefaultCardIdCommandPipe()
               .createObservable(FetchDefaultCardIdCommand.fetch(force))
               .subscribe(testSubscriber)
         return testSubscriber
      }

      fun fetchCardStackListOfCard(force: Boolean = false, predicate: (command: CardStacksCommand) -> Boolean): Unit {
         val testSubscriber: TestSubscriber<ActionState<CardStacksCommand>> = TestSubscriber()

         janet.createPipe(CardStacksCommand::class.java)
               .createObservable(CardStacksCommand.get(force))
               .subscribe(testSubscriber)
         assertActionSuccess(testSubscriber, Func1 { predicate(it) })
      }

      fun loadDefaultAddress(): TestSubscriber<ActionState<GetDefaultAddressCommand>> {
         val testSubscriber = TestSubscriber<ActionState<GetDefaultAddressCommand>>()

         smartCardInteractor.getDefaultAddressCommandPipe()
               .createObservable(GetDefaultAddressCommand())
               .subscribe(testSubscriber)
         return testSubscriber
      }

      fun saveDefaultAddress(defaultAddress: AddressInfo): TestSubscriber<ActionState<SaveDefaultAddressCommand>> {
         val testSubscriber = TestSubscriber<ActionState<SaveDefaultAddressCommand>>()

         smartCardInteractor.saveDefaultAddressPipe()
               .createObservable(SaveDefaultAddressCommand(defaultAddress))
               .subscribe(testSubscriber)
         return testSubscriber
      }

      fun saveBankCardData(bankCard: BankCard, manualAddressInfo: AddressInfo = mockedAddressInfo,
                           nickName: String = "Card1", cvv: String = "0000", issuerInfo: RecordIssuerInfo,
                           useDefaultAddress: Boolean = false, setAsDefaultAddress: Boolean = false, setAsDefaultCard: Boolean = true): TestSubscriber<ActionState<SaveCardDetailsDataCommand>> {
         val testSubscriber = TestSubscriber<ActionState<SaveCardDetailsDataCommand>>()
         val cmd = SaveCardDetailsDataCommand.Builder()
               .setBankCard(bankCard)
               .setManualAddressInfo(manualAddressInfo)
               .setNickName(nickName)
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
         this.createPipe(ConnectAction::class.java).createObservableResult(ConnectAction(ImmutableConnectionParams.of(1))).subscribe()
      }

      fun mockSmartCard(cardId: String): SmartCard {
         var mockedSmartCard: SmartCard = mock()
         whenever(mockedSmartCard.smartCardId()).thenReturn(cardId)
         whenever(mockedSmartCard.cardStatus()).thenReturn(SmartCard.CardStatus.ACTIVE)
         whenever(mockedSmartCard.connectionStatus()).thenReturn(SmartCard.ConnectionStatus.DISCONNECTED)
         whenever(mockedSmartCard.deviceName()).thenReturn("device name")
         whenever(mockedSmartCard.deviceAddress()).thenReturn("device address")
         whenever(mockedSmartCard.cardName()).thenReturn("card name")
         whenever(mockedSmartCard.sdkVersion()).thenReturn("1.0.0")
         whenever(mockedSmartCard.firmWareVersion()).thenReturn("1.0.0")
         whenever(mockedSmartCard.serialNumber()).thenReturn("")

         return mockedSmartCard
      }

      private fun prepareCardsAndAddressMock() {
         whenever(mockedIssuerInfo.financialService()).thenReturn(Record.FinancialService.MASTERCARD)
         whenever(mockedIssuerInfo.cardType()).thenReturn(CardType.DEBIT)
         whenever(mockedIssuerInfo.bankName()).thenReturn("Bank Name")

         whenever(mockedDefaultCard.id()).thenReturn(TEST_DEFAULT_CARD_ID.toString())

         whenever(mockedAddressInfo.address1()).thenReturn("test address 1")
         whenever(mockedAddressInfo.address2()).thenReturn("test address 2")
         whenever(mockedAddressInfo.city()).thenReturn("test city")
         whenever(mockedAddressInfo.state()).thenReturn("test state")
         whenever(mockedAddressInfo.zip()).thenReturn("test zip")

         whenever(mockedDebitCard.id()).thenReturn(TEST_CARD_ID.toString())
         whenever(mockedDebitCard.title()).thenReturn("TEST")
         whenever(mockedDebitCard.number()).thenReturn(123456789L)
         whenever(mockedDebitCard.cvv()).thenReturn(375)
         whenever(mockedDebitCard.addressInfo()).thenReturn(mockedAddressInfo)
         whenever(mockedDebitCard.expiryMonth()).thenReturn(12)
         whenever(mockedDebitCard.expiryYear()).thenReturn(34)
         whenever(mockedDebitCard.category()).thenReturn(Card.Category.BANK)
         whenever(mockedDebitCard.issuerInfo()).thenReturn(mockedIssuerInfo)

      }
   }
}


