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
import com.worldventures.dreamtrips.wallet.domain.converter.RecordToBankCardConverter
import com.worldventures.dreamtrips.wallet.domain.entity.AddressInfo
import com.worldventures.dreamtrips.wallet.domain.entity.RecordIssuerInfo
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCard
import com.worldventures.dreamtrips.wallet.domain.entity.card.BankCard
import com.worldventures.dreamtrips.wallet.domain.storage.DefaultBankCardStorage
import com.worldventures.dreamtrips.wallet.domain.storage.SmartCardStorage
import com.worldventures.dreamtrips.wallet.domain.storage.WalletCardsDiskStorage
import com.worldventures.dreamtrips.wallet.domain.storage.disk.CardListStorage
import com.worldventures.dreamtrips.wallet.model.TestAddressInfo
import com.worldventures.dreamtrips.wallet.model.TestBankCard
import com.worldventures.dreamtrips.wallet.model.TestRecordIssuerInfo
import com.worldventures.dreamtrips.wallet.service.FirmwareInteractor
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
import rx.observers.TestSubscriber

class SmartCardInteractorSpec : BaseSpec({
   describe("SmartCard SDK actions") {
      beforeEach {
         staticMockTextUtils()

         mockDb = createMockDb()
         cardStorage = mock()
         mappery = createMappery()
         janet = createJanet()
         smartCardInteractor = createSmartCardInteractor(janet)

         janet.connectToSmartCardSdk()
      }

      context("Smart Card connection status should be changed") {

         it("Connect to smart card") {
            val smartcard: SmartCard = mockSmartCard("4")
            whenever(smartcard.cardStatus()).thenReturn(SmartCard.CardStatus.DRAFT)

            val testSubscriber: TestSubscriber<ActionState<ConnectSmartCardCommand>> = TestSubscriber()
            janet.createPipe(ConnectSmartCardCommand::class.java)
                  .createObservable(ConnectSmartCardCommand(smartcard, false))
                  .subscribe(testSubscriber)

            assertActionSuccess(testSubscriber, { it.result.connectionStatus() === SmartCard.ConnectionStatus.CONNECTED })
            verify(mockDb, times(1)).saveSmartCard(any())
         }

         it("set disconnect status if the event is thrown") {
            val activeSmartCardId = "4"
            val smartcard: SmartCard = mockSmartCard(activeSmartCardId)
            whenever(mockDb.activeSmartCardId).thenReturn(activeSmartCardId)
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
            fetchCardStackListOfCard { it.result.size == 1 }
         }


         it("should fetch from device, even if cache is present") {
            smartCardInteractor.addRecordPipe()
                  .send(AttachCardCommand(debitCard, false))

            fetchCardStackListOfCard(true) {
               it.result.size == 1 &&
                     it.result[0].bankCards.size == 1 &&
                     it.result[0].stackStackType === StackType.PAYMENT
            }
         }

         it("Add several card to smartCard, check size after one by one options add") {
            smartCardInteractor.addRecordPipe()
                  .send(AttachCardCommand(debitCard, true))
            smartCardInteractor.addRecordPipe()
                  .send(AttachCardCommand(creditCard, false))

            fetchCardStackListOfCard(true) {
               it.result.size == 1 &&
                     it.result[0].bankCards.size == 2
            }
         }
      }

      context("Delete card") {
         val removedCardId = "51"
         val debitCard = TestBankCard(removedCardId, TestRecordIssuerInfo(cardType = BankCard.CardType.DEBIT))
         val creditCard = TestBankCard("52", TestRecordIssuerInfo(cardType = BankCard.CardType.CREDIT))

         beforeEach {
            // mock active smart card
            val smartCardId = "111"
            val smartCard = mockSmartCard(smartCardId)
            whenever(mockDb.activeSmartCardId).thenReturn(smartCardId)
            whenever(mockDb.getSmartCard(smartCardId)).thenReturn(smartCard)

            // mock saving result after delete
            var list = listOf<BankCard>(debitCard, creditCard)
            whenever(cardStorage.readWalletCardsList()).thenAnswer { return@thenAnswer list }
            whenever(cardStorage.saveWalletCardsList(anyList())).thenAnswer { invocation ->
               list = invocation.arguments[0] as List<BankCard>
               return@thenAnswer null
            }
         }

         it("should delete item from cache of card list") {
            val testSubscriber: TestSubscriber<ActionState<CardStacksCommand>> = TestSubscriber()

            smartCardInteractor.cardStacksPipe()
                  .observe()
                  .subscribe(testSubscriber)

            smartCardInteractor.deleteCardPipe()
                  .createObservable(DeleteRecordAction(Integer.parseInt(removedCardId)))
                  .subscribe()

            assertActionSuccess(testSubscriber, {
               it.result.flatMap { it.bankCards }.find { it.id() == removedCardId } == null
            })
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
            whenever(mockDb.activeSmartCardId).thenReturn(smartCardId)
            whenever(mockDb.getSmartCard(smartCardId)).thenReturn(smartCard)
         }

         it("Card with valid data should be stored with default address and marked as default") {
            whenever(mockDb.readWalletDefaultCardId()).thenReturn(null)
            val subscriber = saveBankCardData(bankCard, setAsDefaultCard = true, setAsDefaultAddress = true)
            assertActionSuccess(subscriber, { true })

            verify(mockDb, times(1)).saveDefaultAddress(any())
            verify(mockDb, times(1)).saveWalletDefaultCardId(any())
         }

         it("Card with valid data should be stored without default address and not marked as default") {
            val defaultCardId = "9"
            whenever(mockDb.readWalletDefaultCardId()).thenReturn(defaultCardId)

            val subscriber = saveBankCardData(bankCard, setAsDefaultCard = false, setAsDefaultAddress = false)
            assertActionSuccess(subscriber, { true })
            verify(mockDb, times(0)).saveDefaultAddress(any())
            verify(mockDb, times(0)).saveWalletDefaultCardId(any())
            verify(mockDb, times(0)).saveWalletDefaultCardId(defaultCardId)
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

      val setOfMultiplyStorage: () -> Set<MultipleActionStorage<*>> = {
         setOf(DefaultBankCardStorage(mockDb), SmartCardStorage(mockDb))
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

      fun createSmartCardInteractor(janet: Janet) = SmartCardInteractor(janet, SessionActionPipeCreator(janet), createFirmwareInteractor(janet))

      fun createFirmwareInteractor(janet: Janet) = FirmwareInteractor(janet)

      fun createJanet(): Janet {
         val daggerCommandActionService = CommandActionService()
               .wrapCache()
               .bindMultiplyStorageSet(setOfMultiplyStorage())
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
                           nickName: String = "Card1",
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
         this.createPipe(ConnectAction::class.java).createObservableResult(ConnectAction(ImmutableConnectionParams.of(1))).subscribe()
      }

      fun mockSmartCard(cardId: String): SmartCard {
         val mockedSmartCard: SmartCard = mock()
         whenever(mockedSmartCard.smartCardId()).thenReturn(cardId)
         whenever(mockedSmartCard.cardStatus()).thenReturn(SmartCard.CardStatus.ACTIVE)
         whenever(mockedSmartCard.connectionStatus()).thenReturn(SmartCard.ConnectionStatus.DISCONNECTED)
         whenever(mockedSmartCard.cardName()).thenReturn("device name")
         whenever(mockedSmartCard.deviceAddress()).thenReturn("device address")
         whenever(mockedSmartCard.cardName()).thenReturn("card name")
         whenever(mockedSmartCard.sdkVersion()).thenReturn("1.0.0")
         whenever(mockedSmartCard.firmWareVersion()).thenReturn("1.0.0")
         whenever(mockedSmartCard.serialNumber()).thenReturn("")

         return mockedSmartCard
      }
   }
}


