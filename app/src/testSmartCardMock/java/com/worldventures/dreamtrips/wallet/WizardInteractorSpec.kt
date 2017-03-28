package com.worldventures.dreamtrips.wallet

import android.content.Context
import android.test.mock.MockContext
import android.text.TextUtils
import com.nhaarman.mockito_kotlin.*
import com.worldventures.dreamtrips.AssertUtil
import com.worldventures.dreamtrips.BaseSpec
import com.worldventures.dreamtrips.api.profile.model.ProfileAddress
import com.worldventures.dreamtrips.api.smart_card.terms_and_condition.model.TermsAndConditions
import com.worldventures.dreamtrips.api.smart_card.user_info.model.UpdateCardUserData
import com.worldventures.dreamtrips.core.janet.SessionActionPipeCreator
import com.worldventures.dreamtrips.core.janet.cache.CacheResultWrapper
import com.worldventures.dreamtrips.core.janet.cache.storage.ActionStorage
import com.worldventures.dreamtrips.core.janet.cache.storage.MultipleActionStorage
import com.worldventures.dreamtrips.core.repository.SnappyRepository
import com.worldventures.dreamtrips.wallet.domain.converter.SmartCardDetailsConverter
import com.worldventures.dreamtrips.wallet.domain.converter.SmartCardRecordToWalletRecordConverter
import com.worldventures.dreamtrips.wallet.domain.converter.WalletRecordToSmartCardRecordConverter
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardDetails
import com.worldventures.dreamtrips.wallet.domain.entity.record.Record
import com.worldventures.dreamtrips.wallet.domain.storage.DefaultRecordIdStorage
import com.worldventures.dreamtrips.wallet.domain.storage.SmartCardActionStorage
import com.worldventures.dreamtrips.wallet.domain.storage.WalletRecordsDiskStorage
import com.worldventures.dreamtrips.wallet.domain.storage.disk.RecordsStorage
import com.worldventures.dreamtrips.wallet.model.TestSmartCard
import com.worldventures.dreamtrips.wallet.model.TestSmartCardDetails
import com.worldventures.dreamtrips.wallet.model.TestTermsAndConditions
import com.worldventures.dreamtrips.wallet.model.TestUpdateCardUserData
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor
import com.worldventures.dreamtrips.wallet.service.SystemPropertiesProvider
import com.worldventures.dreamtrips.wallet.service.WizardInteractor
import com.worldventures.dreamtrips.wallet.service.command.CreateAndConnectToCardCommand
import com.worldventures.dreamtrips.wallet.service.command.http.AssociateCardUserCommand
import com.worldventures.dreamtrips.wallet.service.command.http.FetchAndStoreDefaultAddressInfoCommand
import com.worldventures.dreamtrips.wallet.service.command.http.FetchTermsAndConditionsCommand
import com.worldventures.dreamtrips.wallet.service.command.reset.ResetSmartCardCommand
import com.worldventures.dreamtrips.wallet.service.lostcard.LostCardRepository
import io.techery.janet.ActionState
import io.techery.janet.CommandActionService
import io.techery.janet.Janet
import io.techery.janet.SmartCardActionService
import io.techery.janet.http.test.MockHttpActionService
import io.techery.janet.smartcard.action.support.ConnectAction
import io.techery.janet.smartcard.mock.client.MockSmartCardClient
import io.techery.janet.smartcard.model.ImmutableConnectionParams
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
         recordsStorage = mock()
         mappery = createMappery()
         janet = createJanet()
         wizardInteractor = createWizardInteractor(janet)
         smartCardInteractor = createSmartCardInteractor(janet)

         propertiesProvider = createPropertiesProvider()

         janet.connectToSmartCardSdk()

         mockedDebitCard = mock()
      }

      context("SmartCard wizard flow") {

         beforeEach {
            lostCardStorage = mock()
         }

         it("should be to save of SmartCard") {
            val testSubscriber: TestSubscriber<ActionState<CreateAndConnectToCardCommand>> = TestSubscriber()
            janet.createPipe(CreateAndConnectToCardCommand::class.java)
                  .createObservable(CreateAndConnectToCardCommand(MOCK_BARCODE, false))
                  .subscribe(testSubscriber)

            AssertUtil.assertActionSuccess(testSubscriber, { true })
            verify(mockDb, times(1)).saveSmartCard(any())
            verify(propertiesProvider, times(1)).deviceId()
         }

         it("should be associate SmartCard and save SmartCardDetails to db") {
            val cardUserData: UpdateCardUserData = TestUpdateCardUserData()
            val testSubscriber: TestSubscriber<ActionState<AssociateCardUserCommand>> = TestSubscriber()
            janet.createPipe(AssociateCardUserCommand::class.java)
                  .createObservable(AssociateCardUserCommand(MOCK_BARCODE, cardUserData))
                  .subscribe(testSubscriber)

            AssertUtil.assertActionSuccess(testSubscriber, { true })
            verify(mockDb, times(0)).saveSmartCardDetails(any())
            verify(propertiesProvider, times(1)).deviceId()
            verify(propertiesProvider, times(1)).deviceName()
            verify(propertiesProvider, times(1)).osVersion()
         }

         it("should be save TermsAndConditions to db") {
            val testSubscriber: TestSubscriber<ActionState<FetchTermsAndConditionsCommand>> = TestSubscriber()
            janet.createPipe(FetchTermsAndConditionsCommand::class.java)
                  .createObservable(FetchTermsAndConditionsCommand())
                  .subscribe(testSubscriber)

            AssertUtil.assertActionSuccess(testSubscriber, { true })
            verify(mockDb, times(0)).saveWalletTermsAndConditions(any())
         }

         it("should be save DefaultAddress to db") {
            val testSubscriber: TestSubscriber<ActionState<FetchAndStoreDefaultAddressInfoCommand>> = TestSubscriber()
            janet.createPipe(FetchAndStoreDefaultAddressInfoCommand::class.java)
                  .createObservable(FetchAndStoreDefaultAddressInfoCommand())
                  .subscribe(testSubscriber)

            AssertUtil.assertActionSuccess(testSubscriber, { true })
            verify(mockDb, times(1)).saveDefaultAddress(any())
         }

         it("disassociates SmartCard") {
            val smartCard = TestSmartCard(MOCK_BARCODE)
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
      lateinit var recordsStorage: RecordsStorage

      lateinit var janet: Janet
      lateinit var mappery: MapperyContext
      lateinit var wizardInteractor: WizardInteractor
      lateinit var smartCardInteractor: SmartCardInteractor

      lateinit var propertiesProvider: SystemPropertiesProvider
      lateinit var lostCardStorage: LostCardRepository

      lateinit var mockedDebitCard: Record

      val setOfMultiplyStorage: () -> Set<ActionStorage<*>> = {
         setOf(DefaultRecordIdStorage(recordsStorage), SmartCardActionStorage(mockDb), WalletRecordsDiskStorage(recordsStorage))
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

      fun createWizardInteractor(janet: Janet) = WizardInteractor(SessionActionPipeCreator(janet))

      fun createSmartCardInteractor(janet: Janet) = SmartCardInteractor(SessionActionPipeCreator(janet), { Schedulers.immediate() })

      fun createJanet(): Janet {
         val daggerCommandActionService = CommandActionService()
               .wrapCache()
               .bindStorageSet(setOfMultiplyStorage())
               .wrapDagger()

         janet = Janet.Builder()
               .addService(daggerCommandActionService)
               .addService(mockHttpService().wrapStub().wrapCache())
               .addService(SmartCardActionService.createDefault(MockSmartCardClient()))
               .build()

         daggerCommandActionService.registerProvider(Janet::class.java) { janet }
         daggerCommandActionService.registerProvider(SnappyRepository::class.java) { mockDb }
         daggerCommandActionService.registerProvider(MapperyContext::class.java) { mappery }
         daggerCommandActionService.registerProvider(RecordsStorage::class.java) { recordsStorage }
         daggerCommandActionService.registerProvider(Context::class.java, { MockContext() })
         daggerCommandActionService.registerProvider(WizardInteractor::class.java, { wizardInteractor })
         daggerCommandActionService.registerProvider(SmartCardInteractor::class.java, { smartCardInteractor })
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
            .map(Record::class.java).to(io.techery.janet.smartcard.model.Record::class.java, WalletRecordToSmartCardRecordConverter())
            .map(io.techery.janet.smartcard.model.Record::class.java).to(Record::class.java, SmartCardRecordToWalletRecordConverter())
            .map(com.worldventures.dreamtrips.api.smart_card.user_association.model.SmartCardDetails::class.java).to(SmartCardDetails::class.java, SmartCardDetailsConverter())
            .build()

      fun mockHttpService(): MockHttpActionService {
         val termsAndConditionsResponse = mockTermsAndConditionsResponse()
         val emptyAddressResponse: List<ProfileAddress> = emptyList()
         return MockHttpActionService.Builder()
               .bind(MockHttpActionService.Response(204), { request ->
                  request.url.contains("api/smartcard/provisioning/card_user") && request.method.equals("delete", true)
               })
               .bind(MockHttpActionService.Response(204).body(TestSmartCardDetails(MOCK_SMART_CARD_ID)), { request ->
                  request.url.contains("api/smartcard/provisioning/card_user") && request.method.equals("post", true)
               })
               .bind(MockHttpActionService.Response(200).body(emptyAddressResponse)) { request ->
                  request.url.contains("api/profile/addresses")
               }
               .bind(MockHttpActionService.Response(200).body(termsAndConditionsResponse)) { request ->
                  request.url.contains("api/smartcard/provisioning/terms_and_conditions")
               }
               .build()
      }

      private fun mockTermsAndConditionsResponse(): TermsAndConditions {
         val termsAndConditions: TermsAndConditions = mock()
         whenever(termsAndConditions.version()).thenReturn(1)
         whenever(termsAndConditions.url()).thenReturn("http://www.termsandconditions.test.com")
         return termsAndConditions
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

      fun createPropertiesProvider(): SystemPropertiesProvider {
         val propertiesProvider: SystemPropertiesProvider = mock()
         whenever(propertiesProvider.deviceId()).thenReturn("Android1234567890device")
         whenever(propertiesProvider.deviceName()).thenReturn("Android")
         whenever(propertiesProvider.osVersion()).thenReturn("7.1.1")
         return propertiesProvider
      }
   }
}
