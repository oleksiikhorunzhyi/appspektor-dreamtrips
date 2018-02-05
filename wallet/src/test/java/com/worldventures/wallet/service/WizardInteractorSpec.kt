package com.worldventures.wallet.service

import android.content.Context
import android.test.mock.MockContext
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.spy
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import com.worldventures.core.janet.SessionActionPipeCreator
import com.worldventures.core.janet.cache.CacheResultWrapper
import com.worldventures.core.janet.cache.storage.ActionStorage
import com.worldventures.core.modules.legal.LegalInteractor
import com.worldventures.core.modules.settings.service.SettingsInteractor
import com.worldventures.core.test.AssertUtil
import com.worldventures.dreamtrips.api.smart_card.terms_and_condition.model.TermsAndConditions
import com.worldventures.dreamtrips.api.smart_card.user_info.model.UpdateCardUserData
import com.worldventures.dreamtrips.api.terms_and_conditions.model.BaseDocumentBody
import com.worldventures.dreamtrips.api.terms_and_conditions.model.DocumentBodyWithUrl
import com.worldventures.wallet.BaseSpec
import com.worldventures.wallet.domain.converter.SmartCardRecordToWalletRecordConverter
import com.worldventures.wallet.domain.converter.WalletRecordToSmartCardRecordConverter
import com.worldventures.wallet.domain.entity.ConnectionStatus
import com.worldventures.wallet.domain.entity.SmartCardAgreement
import com.worldventures.wallet.domain.entity.SmartCardStatus
import com.worldventures.wallet.domain.entity.SmartCardUser
import com.worldventures.wallet.domain.entity.record.Record
import com.worldventures.wallet.domain.storage.WalletStorage
import com.worldventures.wallet.domain.storage.action.DefaultRecordIdStorage
import com.worldventures.wallet.domain.storage.action.DeviceStateActionStorage
import com.worldventures.wallet.domain.storage.action.SmartCardActionStorage
import com.worldventures.wallet.domain.storage.action.WalletRecordsActionStorage
import com.worldventures.wallet.domain.storage.disk.RecordsStorage
import com.worldventures.wallet.model.TestApiSmartCardDetails
import com.worldventures.wallet.model.TestApiUpdateCardUserData
import com.worldventures.wallet.model.createTestSmartCard
import com.worldventures.wallet.service.command.http.AssociateCardUserCommand
import com.worldventures.wallet.service.command.http.FetchSmartCardAgreementsCommand
import com.worldventures.wallet.service.command.reset.ResetSmartCardCommand
import com.worldventures.wallet.service.lostcard.LostCardRepository
import com.worldventures.wallet.util.CachedPhotoUtil
import com.worldventures.wallet.util.WalletFeatureHelper
import com.worldventures.wallet.util.WalletFeatureHelperFull
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
import org.jetbrains.spek.api.dsl.context
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import rx.observers.TestSubscriber

class WizardInteractorSpec : BaseSpec({

   describe("SmartCard SDK actions") {
      beforeEachTest {

         mockDb = createMockDb()
         whenever(mockDb.smartCardUser).thenReturn(SmartCardUser("First Name"))
         whenever(mockDb.smartCard).thenReturn(createTestSmartCard("1"))
         recordsStorage = mock()
         mappery = createMappery()
         janet = createJanet()
         wizardInteractor = createWizardInteractor(janet)
         smartCardInteractor = createSmartCardInteractor(janet)
         propertiesProvider = createPropertiesProvider()
         smartCardLocationInteractor = createSmartCardLocationInteractor(janet)
         settingsInteractor = createSettingsInteractor(janet)
         legalInteractor = createLegalInteractor(janet)

         janet.connectToSmartCardSdk()
         mockedDebitCard = Record(id = null, number = "7777 7777 7777 7777", numberLastFourDigits = "7777", expDate = "00/00")
      }

      context("SmartCard wizard flow") {
         beforeEachTest {
            lostCardStorage = mock()
         }

         it("should be associate SmartCard and save SmartCardDetails to mediaModelStorage") {
            val cardUserData: UpdateCardUserData = TestApiUpdateCardUserData()
            val testSubscriber: TestSubscriber<ActionState<AssociateCardUserCommand>> = TestSubscriber()
            janet.createPipe(AssociateCardUserCommand::class.java)
                  .createObservable(AssociateCardUserCommand(MOCK_BARCODE, cardUserData))
                  .subscribe(testSubscriber)

            AssertUtil.assertActionSuccess(testSubscriber, { true })
            verify(mockDb, times(1)).saveSmartCard(any())
            verify(propertiesProvider, times(1)).deviceId()
            verify(propertiesProvider, times(1)).deviceName()
            verify(propertiesProvider, times(1)).osVersion()
         }

         it("should be save TermsAndConditions to mediaModelStorage") {
            val testSubscriber: TestSubscriber<ActionState<FetchSmartCardAgreementsCommand>> = TestSubscriber()
            janet.createPipe(FetchSmartCardAgreementsCommand::class.java)
                  .createObservable(FetchSmartCardAgreementsCommand.termsAndConditions())
                  .subscribe(testSubscriber)

            AssertUtil.assertActionSuccess(testSubscriber, { true })
            verify(mockDb, times(1)).saveWalletTermsAndConditions(any())
         }

         it("disassociates SmartCard") {
            val testSubscriber: TestSubscriber<ActionState<ResetSmartCardCommand>> = TestSubscriber()
            janet.createPipe(ResetSmartCardCommand::class.java)
                  .createObservable(ResetSmartCardCommand())
                  .subscribe(testSubscriber)

            AssertUtil.assertActionSuccess(testSubscriber, { true })
            verify(lostCardStorage, times(1)).clear()
         }
      }
   }
}) {
   private companion object {

      const val MOCK_SMART_CARD_ID: Long = 13371340
      const val MOCK_BARCODE = MOCK_SMART_CARD_ID.toString()

      lateinit var mockDb: WalletStorage
      lateinit var recordsStorage: RecordsStorage

      lateinit var janet: Janet
      lateinit var mappery: MapperyContext
      lateinit var wizardInteractor: WizardInteractor
      lateinit var smartCardInteractor: SmartCardInteractor

      lateinit var propertiesProvider: SystemPropertiesProvider
      lateinit var lostCardStorage: LostCardRepository
      lateinit var mockedDebitCard: Record
      lateinit var smartCardLocationInteractor: SmartCardLocationInteractor
      lateinit var settingsInteractor: SettingsInteractor
      lateinit var legalInteractor: LegalInteractor
      private val deviceStateActionStorage = DeviceStateActionStorage()

      init {
         deviceStateActionStorage.save(null, SmartCardStatus(connectionStatus = ConnectionStatus.CONNECTED))
      }

      val setOfMultiplyStorage: () -> Set<ActionStorage<*>> = {
         setOf(DefaultRecordIdStorage(recordsStorage), SmartCardActionStorage(mockDb), WalletRecordsActionStorage(recordsStorage), deviceStateActionStorage)
      }

      fun createWizardInteractor(janet: Janet) = WizardInteractor(SessionActionPipeCreator(janet))

      fun createSmartCardInteractor(janet: Janet) = SmartCardInteractor(SessionActionPipeCreator(janet), TestSchedulerProvider())

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
         daggerCommandActionService.registerProvider(WalletStorage::class.java) { mockDb }
         daggerCommandActionService.registerProvider(MapperyContext::class.java) { mappery }
         daggerCommandActionService.registerProvider(RecordsStorage::class.java) { recordsStorage }
         daggerCommandActionService.registerProvider(Context::class.java, { MockContext() })
         daggerCommandActionService.registerProvider(WizardInteractor::class.java, { wizardInteractor })
         daggerCommandActionService.registerProvider(SmartCardInteractor::class.java, { smartCardInteractor })
         daggerCommandActionService.registerProvider(SystemPropertiesProvider::class.java, { propertiesProvider })
         daggerCommandActionService.registerProvider(LostCardRepository::class.java, { lostCardStorage })
         daggerCommandActionService.registerProvider(CachedPhotoUtil::class.java, { mock() })
         daggerCommandActionService.registerProvider(SmartCardLocationInteractor::class.java) { smartCardLocationInteractor }
         daggerCommandActionService.registerProvider(SettingsInteractor::class.java, { settingsInteractor })
         daggerCommandActionService.registerProvider(WalletFeatureHelper::class.java) { WalletFeatureHelperFull() }
         daggerCommandActionService.registerProvider(LegalInteractor::class.java) { legalInteractor }

         return janet
      }

      fun createMockDb(): WalletStorage {
         val repository: WalletStorage = spy()
         whenever(repository.walletTermsAndConditions).thenReturn(SmartCardAgreement("", "1"))
         return repository
      }

      fun createMappery(): MapperyContext = Mappery.Builder()
            .map(Record::class.java).to(io.techery.janet.smartcard.model.Record::class.java, WalletRecordToSmartCardRecordConverter())
            .map(io.techery.janet.smartcard.model.Record::class.java).to(Record::class.java, SmartCardRecordToWalletRecordConverter())
            .build()

      fun mockHttpService(): MockHttpActionService {
         val termsAndConditionsResponse = mockTermsAndConditionsResponse()
         val fetchAgreementsResponse = mockAgreementsResponse()
         return MockHttpActionService.Builder()
               .bind(MockHttpActionService.Response(204)) { request ->
                  request.url.contains("api/smartcard/provisioning/card_user") && request.method.equals("delete", true)
               }
               .bind(MockHttpActionService.Response(204).body(TestApiSmartCardDetails(MOCK_SMART_CARD_ID))) { request ->
                  request.url.contains("api/smartcard/provisioning/card_user") && request.method.equals("post", true)
               }
               .bind(MockHttpActionService.Response(200).body(termsAndConditionsResponse)) { request ->
                  request.url.contains("api/smartcard/provisioning/terms_and_conditions")
               }
               .bind(MockHttpActionService.Response(204)) { request ->
                  request.url.endsWith("api/user/settings")
               }
               .bind(MockHttpActionService.Response(204).body(fetchAgreementsResponse)) { request ->
                  request.url.endsWith("/api/documents/" + BaseDocumentBody.SC_BETA_AFFIDAVIT)
                        || request.url.endsWith("/api/documents/" + BaseDocumentBody.SC_TERMS)
               }
               .build()
      }

      private fun mockTermsAndConditionsResponse(): TermsAndConditions {
         val termsAndConditions: TermsAndConditions = mock()
         whenever(termsAndConditions.version()).thenReturn(1)
         whenever(termsAndConditions.url()).thenReturn("http://www.termsandconditions.test.com")
         return termsAndConditions
      }

      private fun mockAgreementsResponse(): DocumentBodyWithUrl {
         val agreementDocument: DocumentBodyWithUrl = mock()
         whenever(agreementDocument.url()).thenReturn("http://www.termsandconditions.test.com")
         whenever(agreementDocument.version()).thenReturn("1.0")
         return agreementDocument
      }

      fun CacheResultWrapper.bindStorageSet(storageSet: Set<ActionStorage<*>>): CacheResultWrapper {
         storageSet.forEach { bindStorage(it.actionClass, it) }
         return this
      }

      fun Janet.connectToSmartCardSdk() {
         this.createPipe(ConnectAction::class.java).createObservableResult(ConnectAction(ImmutableConnectionParams.of(1))).subscribe()
      }

      fun createSmartCardLocationInteractor(janet: Janet) = SmartCardLocationInteractor(SessionActionPipeCreator(janet))

      fun createSettingsInteractor(janet: Janet) = SettingsInteractor(SessionActionPipeCreator(janet))

      fun createPropertiesProvider(): SystemPropertiesProvider {
         val propertiesProvider: SystemPropertiesProvider = mock()
         whenever(propertiesProvider.deviceId()).thenReturn("Android1234567890device")
         whenever(propertiesProvider.deviceName()).thenReturn("Android")
         whenever(propertiesProvider.osVersion()).thenReturn("7.1.1")
         return propertiesProvider
      }

      fun createLegalInteractor(janet: Janet) = LegalInteractor(SessionActionPipeCreator(janet))
   }
}
