package com.worldventures.dreamtrips.wallet

import android.content.Context
import android.test.mock.MockContext
import android.text.TextUtils
import com.nhaarman.mockito_kotlin.*
import com.worldventures.dreamtrips.AssertUtil
import com.worldventures.dreamtrips.BaseSpec
import com.worldventures.dreamtrips.api.smart_card.user_info.model.UpdateCardUserData
import com.worldventures.dreamtrips.core.janet.SessionActionPipeCreator
import com.worldventures.dreamtrips.core.janet.cache.CacheResultWrapper
import com.worldventures.dreamtrips.core.janet.cache.storage.ActionStorage
import com.worldventures.dreamtrips.core.janet.cache.storage.MultipleActionStorage
import com.worldventures.dreamtrips.core.repository.SnappyRepository
import com.worldventures.dreamtrips.wallet.domain.converter.BankCardToRecordConverter
import com.worldventures.dreamtrips.wallet.domain.converter.RecordToBankCardConverter
import com.worldventures.dreamtrips.wallet.domain.converter.SmartCardDetailsConverter
import com.worldventures.dreamtrips.wallet.domain.entity.ConnectionStatus
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCard
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardDetails
import com.worldventures.dreamtrips.wallet.domain.entity.card.BankCard
import com.worldventures.dreamtrips.wallet.domain.storage.DefaultBankCardStorage
import com.worldventures.dreamtrips.wallet.domain.storage.SmartCardStorage
import com.worldventures.dreamtrips.wallet.domain.storage.WalletCardsDiskStorage
import com.worldventures.dreamtrips.wallet.domain.storage.disk.CardListStorage
import com.worldventures.dreamtrips.wallet.model.TestFirmware
import com.worldventures.dreamtrips.wallet.model.TestSmartCardDetails
import com.worldventures.dreamtrips.wallet.model.TestTermsAndConditions
import com.worldventures.dreamtrips.wallet.model.TestUpdateCardUserData
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor
import com.worldventures.dreamtrips.wallet.service.SystemPropertiesProvider
import com.worldventures.dreamtrips.wallet.service.WizardInteractor
import com.worldventures.dreamtrips.wallet.service.command.CreateAndConnectToCardCommand
import com.worldventures.dreamtrips.wallet.service.command.http.AssociateCardUserCommand
import com.worldventures.dreamtrips.wallet.service.command.reset.ResetSmartCardCommand
import com.worldventures.dreamtrips.wallet.service.impl.TestSystemPropertiesProvider
import com.worldventures.dreamtrips.wallet.service.lostcard.LostCardRepository
import com.worldventures.dreamtrips.wallet.service.storage.WizardMemoryStorage
import io.techery.janet.ActionState
import io.techery.janet.CommandActionService
import io.techery.janet.Janet
import io.techery.janet.SmartCardActionService
import io.techery.janet.http.test.MockHttpActionService
import io.techery.janet.smartcard.action.support.ConnectAction
import io.techery.janet.smartcard.mock.client.MockSmartCardClient
import io.techery.janet.smartcard.model.ImmutableConnectionParams
import io.techery.janet.smartcard.model.Record
import io.techery.mappery.Mappery
import io.techery.mappery.MapperyContext
import org.powermock.api.mockito.PowerMockito
import rx.observers.TestSubscriber
import rx.schedulers.Schedulers

class WizardInteractorSpec : BaseSpec({

   describe("SmartCard SDK actions") {

      beforeEach {
         staticMockTextUtils()

         mockDb = createMockDb()
         cardStorage = mock()
         mappery = createMappery()
         janet = createJanet()
         wizardInteractor = createWizardInteractor(janet)
         smartCardInteractor = createSmartCardInteractor(janet)

         wizardMemoryStorage = mockWizardMemoryStorage(MOCK_BARCODE)
         propertiesProvider = TestSystemPropertiesProvider()

         janet.connectToSmartCardSdk()

         mockedDebitCard = mock()
      }

      context("SmartCard wizard flow") {

         beforeEach {
            lostCardStorage = mock()
         }

         it("connects to SmartCard") {
            val testSubscriber: TestSubscriber<ActionState<CreateAndConnectToCardCommand>> = TestSubscriber()
            janet.createPipe(CreateAndConnectToCardCommand::class.java)
                  .createObservable(CreateAndConnectToCardCommand(false, false))
                  .subscribe(testSubscriber)

            AssertUtil.assertActionSuccess(testSubscriber, {
               it.result.connectionStatus() === ConnectionStatus.CONNECTED
            })
         }

         it("associates SmartCard") {
            val cardUserData: UpdateCardUserData = TestUpdateCardUserData()
            val testSubscriber: TestSubscriber<ActionState<AssociateCardUserCommand>> = TestSubscriber()
            janet.createPipe(AssociateCardUserCommand::class.java)
                  .createObservable(AssociateCardUserCommand(MOCK_BARCODE, cardUserData))
                  .subscribe(testSubscriber)

            AssertUtil.assertActionSuccess(testSubscriber, { true })
         }

         it("disassociates SmartCard") {
            val smartCard = mockSmartCard(MOCK_BARCODE)
            whenever(smartCard.connectionStatus()).thenReturn(ConnectionStatus.CONNECTED)
            whenever(mockDb.smartCard).thenReturn(smartCard)

            val testSubscriber: TestSubscriber<ActionState<ResetSmartCardCommand>> = TestSubscriber()
            janet.createPipe(ResetSmartCardCommand::class.java)
                  .createObservable(ResetSmartCardCommand(smartCard))
                  .subscribe(testSubscriber)

            AssertUtil.assertActionSuccess(testSubscriber, { true })
            verify(lostCardStorage, times(1))
         }
      }
   }

}) {
   private companion object {

      const val MOCK_SMART_CARD_ID: Long = 13371340
      const val MOCK_BARCODE = MOCK_SMART_CARD_ID.toString()

      lateinit var mockDb: SnappyRepository
      lateinit var cardStorage: CardListStorage

      lateinit var janet: Janet
      lateinit var mappery: MapperyContext
      lateinit var wizardInteractor: WizardInteractor
      lateinit var smartCardInteractor: SmartCardInteractor

      lateinit var wizardMemoryStorage: WizardMemoryStorage
      lateinit var propertiesProvider: SystemPropertiesProvider
      lateinit var lostCardStorage: LostCardRepository

      lateinit var mockedDebitCard: BankCard

      val setOfMultiplyStorage: () -> Set<ActionStorage<*>> = {
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

      fun createWizardInteractor(janet: Janet) = WizardInteractor(SessionActionPipeCreator(janet))

      fun createSmartCardInteractor(janet: Janet) = SmartCardInteractor(SessionActionPipeCreator(janet), { Schedulers.immediate() })

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
         daggerCommandActionService.registerProvider(MapperyContext::class.java) { mappery }
         daggerCommandActionService.registerProvider(CardListStorage::class.java) { cardStorage }
         daggerCommandActionService.registerProvider(Context::class.java, { MockContext() })
         daggerCommandActionService.registerProvider(WizardInteractor::class.java, { wizardInteractor })
         daggerCommandActionService.registerProvider(SmartCardInteractor::class.java, { smartCardInteractor })
         daggerCommandActionService.registerProvider(WizardMemoryStorage::class.java, { wizardMemoryStorage })
         daggerCommandActionService.registerProvider(SystemPropertiesProvider::class.java, { propertiesProvider })
         daggerCommandActionService.registerProvider(LostCardRepository::class.java, { lostCardStorage })

         return janet
      }

      fun createMockDb(): SnappyRepository {
         val repository: SnappyRepository = spy()
         whenever(repository.walletTermsAndConditions).thenReturn(TestTermsAndConditions())
         return repository
      }

      fun createMappery(): MapperyContext = Mappery.Builder()
            .map(BankCard::class.java).to(Record::class.java, BankCardToRecordConverter())
            .map(Record::class.java).to(BankCard::class.java, RecordToBankCardConverter())
            .map(com.worldventures.dreamtrips.api.smart_card.user_association.model.SmartCardDetails::class.java).to(SmartCardDetails::class.java, SmartCardDetailsConverter())
            .build()

      fun mockHttpService(): MockHttpActionService {
         return MockHttpActionService.Builder()
               .bind(MockHttpActionService.Response(204), { request ->
                  request.url.contains("api/smartcard/provisioning/card_user") && request.method.equals("delete", true)
               })
               .bind(MockHttpActionService.Response(204).body(TestSmartCardDetails(MOCK_SMART_CARD_ID)), { request ->
                  request.url.contains("api/smartcard/provisioning/card_user") && request.method.equals("post", true)
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
         this.createPipe(ConnectAction::class.java).createObservableResult(ConnectAction(ImmutableConnectionParams.of(1))).subscribe()
      }

      fun mockWizardMemoryStorage(cardId: String): WizardMemoryStorage {
         val wizardMemoryStorage: WizardMemoryStorage = mock()
         whenever(wizardMemoryStorage.barcode).thenReturn(cardId)
         return wizardMemoryStorage
      }

      fun mockSmartCard(cardId: String): SmartCard {
         val mockedSmartCard: SmartCard = mock()
         whenever(mockedSmartCard.smartCardId()).thenReturn(cardId)
         whenever(mockedSmartCard.cardStatus()).thenReturn(SmartCard.CardStatus.ACTIVE)
         whenever(mockedSmartCard.connectionStatus()).thenReturn(ConnectionStatus.DISCONNECTED)
         whenever(mockedSmartCard.deviceAddress()).thenReturn("device address")
         whenever(mockedSmartCard.sdkVersion()).thenReturn("1.0.0")
         whenever(mockedSmartCard.firmwareVersion()).thenReturn(TestFirmware())
         whenever(mockedSmartCard.serialNumber()).thenReturn("")
         whenever(mockedSmartCard.user()).thenReturn(mock())

         return mockedSmartCard
      }

   }
}