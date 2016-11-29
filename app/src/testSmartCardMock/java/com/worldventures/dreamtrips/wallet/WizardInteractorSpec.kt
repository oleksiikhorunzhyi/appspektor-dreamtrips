package com.worldventures.dreamtrips.wallet

import android.content.Context
import android.test.mock.MockContext
import android.text.TextUtils
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.spy
import com.nhaarman.mockito_kotlin.whenever
import com.worldventures.dreamtrips.BaseSpec
import com.worldventures.dreamtrips.core.janet.SessionActionPipeCreator
import com.worldventures.dreamtrips.core.janet.cache.CacheResultWrapper
import com.worldventures.dreamtrips.core.janet.cache.storage.ActionStorage
import com.worldventures.dreamtrips.core.janet.cache.storage.MultipleActionStorage
import com.worldventures.dreamtrips.core.repository.SnappyRepository
import com.worldventures.dreamtrips.wallet.domain.converter.BankCardToRecordConverter
import com.worldventures.dreamtrips.wallet.domain.converter.RecordToBankCardConverter
import com.worldventures.dreamtrips.wallet.domain.entity.ImmutableSmartCardDetails
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCard
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardDetails
import com.worldventures.dreamtrips.wallet.domain.entity.card.BankCard
import com.worldventures.dreamtrips.wallet.domain.storage.DefaultBankCardStorage
import com.worldventures.dreamtrips.wallet.domain.storage.SmartCardStorage
import com.worldventures.dreamtrips.wallet.domain.storage.WalletCardsDiskStorage
import com.worldventures.dreamtrips.wallet.domain.storage.disk.CardListStorage
import com.worldventures.dreamtrips.wallet.service.WizardInteractor
import com.worldventures.dreamtrips.wallet.service.command.CreateAndConnectToCardCommand
import com.worldventures.dreamtrips.wallet.service.command.http.AssociateCardUserCommand
import com.worldventures.dreamtrips.wallet.service.command.http.DisassociateCardUserCommand
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
import rx.functions.Func1
import rx.observers.TestSubscriber
import java.util.*

class WizardInteractorSpec : BaseSpec({
   describe("SmartCard SDK actions") {
      beforeEach {
         staticMockTextUtils()

         mockDb = createMockDb()
         cardStorage = mock()
         mappery = createMappery()
         janet = createJanet()
         wizardInteractor = createInteractor(janet)

         janet.connectToSmartCardSdk()

         mockedDebitCard = mock()
      }

      context("Smart Card create connect, associate and disassociate") {

         it("Create connect to smart card") {
            val smartcard: SmartCard = mockSmartCard("4")
            val smartCardDetails: SmartCardDetails = mockSmartCardDetails(smartcard)

            val testSubscriber: TestSubscriber<ActionState<CreateAndConnectToCardCommand>> = TestSubscriber()
            janet.createPipe(CreateAndConnectToCardCommand::class.java)
                  .createObservable(CreateAndConnectToCardCommand(smartCardDetails))
                  .subscribe(testSubscriber)

            testSubscriber.assertCompleted()
            testSubscriber.assertNoErrors()
         }

         it("Associate smart card") {
            var barcode: String = "13371340"
            val testSubscriber: TestSubscriber<ActionState<AssociateCardUserCommand>> = TestSubscriber()
            janet.createPipe(AssociateCardUserCommand::class.java)
                  .createObservable(AssociateCardUserCommand(barcode))
                  .subscribe(testSubscriber)

            testSubscriber.assertCompleted()
            testSubscriber.assertNoErrors()
         }

         it("Disassociate smart card") {
            var barcode: String = "13371340"
            val testSubscriber: TestSubscriber<ActionState<DisassociateCardUserCommand>> = TestSubscriber()
            janet.createPipe(DisassociateCardUserCommand::class.java)
                  .createObservable(DisassociateCardUserCommand(barcode))
                  .subscribe(testSubscriber)

            testSubscriber.assertCompleted()
            testSubscriber.assertNoErrors()
         }
      }
   }

}) {
   private companion object {

      lateinit var mockDb: SnappyRepository
      lateinit var cardStorage: CardListStorage

      lateinit var janet: Janet
      lateinit var mappery: MapperyContext
      lateinit var wizardInteractor: WizardInteractor

      lateinit var mockedDebitCard: BankCard

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

      fun createInteractor(janet: Janet) = WizardInteractor(janet, SessionActionPipeCreator(janet))

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
         daggerCommandActionService.registerProvider(MapperyContext::class.java) { mappery }
         daggerCommandActionService.registerProvider(Context::class.java, { MockContext() })
         daggerCommandActionService.registerProvider(WizardInteractor::class.java, { wizardInteractor })

         return janet
      }

      fun createMockDb(): SnappyRepository = spy()

      fun createMappery(): MapperyContext = Mappery.Builder()
            .map(BankCard::class.java).to(Record::class.java, BankCardToRecordConverter())
            .map(Record::class.java).to(BankCard::class.java, RecordToBankCardConverter())
            .build()

      fun mockHttpService(): MockHttpActionService {
         return MockHttpActionService.Builder()
               .bind(MockHttpActionService.Response(204), Func1 { request ->
                  request.url.contains("api/smartcard/provisioning/card_user/") && request.method.equals("delete", true)
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

      fun mockSmartCard(cardId: String): SmartCard {
         var mockedSmartCard: SmartCard = mock()
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

      fun mockSmartCardDetails(smartCard: SmartCard): SmartCardDetails {
         var mockedSmartCardDetails: SmartCardDetails = ImmutableSmartCardDetails.builder()
               .smartCardId(smartCard.smartCardId().toLong())
               .bleAddress("DA:30:55:CF:B4:9E")
               .nxtOrderId("123")
               .serialNumber("")
               .wvOrderId("")
               .revVersion("")
               .orderDate(Date())
               .build()

         return mockedSmartCardDetails
      }
   }
}


