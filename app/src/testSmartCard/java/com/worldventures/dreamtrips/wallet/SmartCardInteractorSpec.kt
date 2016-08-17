package com.worldventures.dreamtrips.wallet

import android.content.Context
import android.test.mock.MockContext
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.spy
import com.nhaarman.mockito_kotlin.whenever
import com.worldventures.dreamtrips.AssertUtil.assertActionSuccess
import com.worldventures.dreamtrips.BaseSpec
import com.worldventures.dreamtrips.core.janet.cache.CacheResultWrapper
import com.worldventures.dreamtrips.core.janet.cache.storage.ActionStorage
import com.worldventures.dreamtrips.core.janet.cache.storage.MultipleActionStorage
import com.worldventures.dreamtrips.core.repository.SnappyRepository
import com.worldventures.dreamtrips.wallet.domain.entity.AddressInfo
import com.worldventures.dreamtrips.wallet.domain.entity.ImmutableProvision
import com.worldventures.dreamtrips.wallet.domain.entity.card.BankCard
import com.worldventures.dreamtrips.wallet.domain.entity.card.BankCard.CardType
import com.worldventures.dreamtrips.wallet.domain.storage.DefaultBankCardStorage
import com.worldventures.dreamtrips.wallet.domain.storage.WalletCardsDiskStorage
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor
import com.worldventures.dreamtrips.wallet.service.command.AttachCardCommand
import com.worldventures.dreamtrips.wallet.service.command.CardStacksCommand
import com.worldventures.dreamtrips.wallet.service.command.CardStacksCommand.CardStackModel.StackType
import com.worldventures.dreamtrips.wallet.service.command.FetchDefaultCardCommand
import com.worldventures.dreamtrips.wallet.service.command.SetLockStateCommand
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
import rx.functions.Func1
import rx.observers.TestSubscriber

class SmartCardInteractorSpec : BaseSpec({
    describe("SmartCard SDK actions") {
        beforeEach {
            mockDb = createMockDb()
            janet = createJanet()
            smartCardInteractor = createInteractor(janet)

            janet.connectToSmartCardSdk()

            prepareCardsMock()
            mockedListOfCards = mutableListOf(mockedDebitCard, mockedCreditCard, mockedDefaultCard)
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
                fetchCardStackListOfCard { it.result.size == StackType.values().size }
            }

            it("should fetch from device, even if cache is present") {
                smartCardInteractor.addRecordPipe()
                        .send(AttachCardCommand(mockedDebitCard, false))

                fetchCardStackListOfCard(true) {
                    it.result.size == 1 &&
                            it.result[0].bankCards().size == 1 &&
                            it.result[0].type() === StackType.DEBIT
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

                assertActionSuccess(testSubscriber, { it.result.flatMap { it.bankCards() }.find { it.id() == TEST_CARD_ID.toString() } == null })
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
    }
}) {
    private companion object {
        lateinit var mockDb: SnappyRepository
        lateinit var janet: Janet
        lateinit var smartCardInteractor: SmartCardInteractor

        val TEST_CARD_ID = 1
        val TEST_DEFAULT_CARD_ID = 101

        val mockedDebitCard: BankCard = mock()
        val mockedCreditCard: BankCard = mock()
        val mockedDefaultCard: BankCard = mock()

        lateinit var mockedListOfCards: List<BankCard>

        val setOfStorage: () -> Set<ActionStorage<*>> = {
            setOf(WalletCardsDiskStorage(mockDb))
        }

        val setOfMultiplyStorage: () -> Set<MultipleActionStorage<*>> = {
            setOf(DefaultBankCardStorage(mockDb))
        }

        fun createInteractor(janet: Janet) = SmartCardInteractor(janet)

        fun createJanet(): Janet {
            val daggerCommandActionService = CommandActionService()
                    .wrapCache()
                    .bindMultiplyStorageSet(setOfMultiplyStorage())
                    .bindStorageSet(setOfStorage())
                    .wrapDagger()

            janet = Janet.Builder()
                    .addService(daggerCommandActionService)
                    .addService(mockHttpService().wrapStub().wrapCache())
                    .addService(SmartCardActionService.createDefault(MockSmartCardClient()))
                    .build()

            daggerCommandActionService.registerProvider(Janet::class.java) { janet }
            daggerCommandActionService.registerProvider(SnappyRepository::class.java) { mockDb }
            daggerCommandActionService.registerProvider(Context::class.java, { MockContext() })
            daggerCommandActionService.registerProvider(SmartCardInteractor::class.java, { smartCardInteractor })

            return janet
        }

        fun createMockDb(): SnappyRepository = spy()

        fun loadDefaultCardId(force: Boolean = false): TestSubscriber<ActionState<FetchDefaultCardCommand>> {
            val testSubscriber = TestSubscriber<ActionState<FetchDefaultCardCommand>>()

            smartCardInteractor.fetchDefaultCardCommandActionPipe()
                    .createObservable(FetchDefaultCardCommand.fetch(force))
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

        fun mockHttpService(): MockHttpActionService {
            return MockHttpActionService.Builder()
                    .bind(MockHttpActionService.Response(200).body(
                            ImmutableProvision.builder().memberId("1").userSecret("test").build())) {
                        it.url.endsWith("create_card")
                    }
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
            this.createPipe(ConnectAction::class.java).createObservableResult(ConnectAction("any", "any")).subscribe()
        }

        private fun prepareCardsMock() {
            whenever(mockedDefaultCard.id()).thenReturn(TEST_DEFAULT_CARD_ID.toString())

            val addressInfo: AddressInfo = mock()
            whenever(addressInfo.address1()).thenReturn("test address 1")
            whenever(addressInfo.address2()).thenReturn("test address 2")
            whenever(addressInfo.city()).thenReturn("test city")
            whenever(addressInfo.state()).thenReturn("test state")
            whenever(addressInfo.zip()).thenReturn("test zip")

            whenever(mockedDebitCard.id()).thenReturn(TEST_CARD_ID.toString())
            whenever(mockedDebitCard.title()).thenReturn("TEST")
            whenever(mockedDebitCard.number()).thenReturn(123456789L)
            whenever(mockedDebitCard.cvv()).thenReturn(375)
            whenever(mockedDebitCard.addressInfo()).thenReturn(addressInfo)

            whenever(mockedDebitCard.cardType()).thenReturn(CardType.DEBIT)
            whenever(mockedCreditCard.cardType()).thenReturn(CardType.CREDIT)
            whenever(mockedDefaultCard.cardType()).thenReturn(CardType.DEBIT)
        }
    }
}


