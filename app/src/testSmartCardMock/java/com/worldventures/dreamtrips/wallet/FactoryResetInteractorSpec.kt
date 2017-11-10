package com.worldventures.dreamtrips.wallet

import android.content.Context
import android.test.mock.MockContext
import com.nhaarman.mockito_kotlin.*
import com.worldventures.dreamtrips.AssertUtil
import com.worldventures.dreamtrips.BaseSpec
import com.worldventures.core.janet.SessionActionPipeCreator
import com.worldventures.core.janet.cache.storage.ActionStorage
import com.worldventures.core.service.analytics.AnalyticsInteractor
import com.worldventures.core.modules.settings.service.SettingsInteractor
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCard
import com.worldventures.dreamtrips.wallet.domain.storage.WalletStorage
import com.worldventures.dreamtrips.wallet.domain.storage.action.DefaultRecordIdStorage
import com.worldventures.dreamtrips.wallet.domain.storage.action.SmartCardActionStorage
import com.worldventures.dreamtrips.wallet.domain.storage.action.WalletRecordsActionStorage
import com.worldventures.dreamtrips.wallet.domain.storage.disk.RecordsStorage
import com.worldventures.dreamtrips.wallet.model.TestDisassociateResponseBody
import com.worldventures.dreamtrips.wallet.model.TestSmartCardUser
import com.worldventures.dreamtrips.wallet.service.FactoryResetInteractor
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor
import com.worldventures.dreamtrips.wallet.service.SmartCardLocationInteractor
import com.worldventures.dreamtrips.wallet.service.TestSchedulerProvider
import com.worldventures.dreamtrips.wallet.service.command.FactoryResetCommand
import com.worldventures.dreamtrips.wallet.service.command.reset.ResetOptions
import com.worldventures.dreamtrips.wallet.service.command.reset.ResetSmartCardCommand
import com.worldventures.dreamtrips.wallet.service.command.reset.WipeSmartCardDataCommand
import com.worldventures.dreamtrips.wallet.service.firmware.FirmwareInteractorSpec.Companion.connectToSmartCardSdk
import com.worldventures.dreamtrips.wallet.service.lostcard.LostCardRepository
import com.worldventures.dreamtrips.wallet.service.lostcard.SmartCardLocationInteractorSpec.Companion.smartCardInteractor
import com.worldventures.dreamtrips.wallet.util.CachedPhotoUtil
import io.techery.janet.ActionState
import io.techery.janet.CommandActionService
import io.techery.janet.Janet
import io.techery.janet.SmartCardActionService
import io.techery.janet.http.test.MockHttpActionService
import io.techery.janet.smartcard.mock.client.MockSmartCardClient
import org.jetbrains.spek.api.dsl.context
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import rx.observers.TestSubscriber
import rx.schedulers.Schedulers

class FactoryResetInteractorSpec : BaseSpec({

   describe("Factory Reset Actions") {
      beforeEachTest {
         mockDb = createMockDb()
         lostCardRepository = createLostCardRepository()
         cardStorage = mock()
         janet = createJanet()

         smartCardInteractor = createSmartCardInteractor(janet)
         factoryResetInteractor = createFactoryResetInteractor(janet)
         smartCardLocationInteractor = createSmartCardLocationInteractor(janet)
         settingsInteractor = createSettingsInteractor(janet)

         janet.connectToSmartCardSdk()
      }

      context("Factory reset of SmartCard") {

         beforeEachTest {
            // mock active SmartCard
            val smartCardId = "111"
            val smartCard = mockSmartCard(smartCardId)
            whenever(mockDb.smartCard).thenReturn(smartCard)
            whenever(mockDb.smartCardUser).thenReturn(TestSmartCardUser())
         }

         it("Factory Reset without delete payment cards") {
            val testSubscriber = TestSubscriber<ActionState<ResetSmartCardCommand>>()

            janet.createPipe(ResetSmartCardCommand::class.java)
                  .createObservable(ResetSmartCardCommand(
                        ResetOptions.builder()
                              .wipePaymentCards(false)
                              .build())
                  ).subscribe(testSubscriber)

            AssertUtil.assertActionSuccess(testSubscriber, { true })
            verify(cardStorage, times(0)).deleteAllRecords()
            verify(cardStorage, times(0)).deleteDefaultRecordId()
         }

         it("Factory Reset with delete payment cards") {
            val testSubscriber = TestSubscriber<ActionState<ResetSmartCardCommand>>()

            janet.createPipe(ResetSmartCardCommand::class.java)
                  .createObservable(ResetSmartCardCommand(
                        ResetOptions.builder()
                              .wipePaymentCards(true)
                              .build())
                  ).subscribe(testSubscriber)

            AssertUtil.assertActionSuccess(testSubscriber, { true })
            verify(cardStorage, times(1)).deleteAllRecords()
            verify(cardStorage, times(1)).deleteDefaultRecordId()
         }

         it("Factory Reset with delete payment cards and without Smart Card User Data") {
            val testSubscriber = TestSubscriber<ActionState<ResetSmartCardCommand>>()

            janet.createPipe(ResetSmartCardCommand::class.java)
                  .createObservable(ResetSmartCardCommand(
                        ResetOptions.builder()
                              .wipePaymentCards(true)
                              .wipeUserSmartCardData(false)
                              .build())
                  ).subscribe(testSubscriber)

            AssertUtil.assertActionSuccess(testSubscriber, { true })
            verify(cardStorage, times(1)).deleteAllRecords()
            verify(cardStorage, times(1)).deleteDefaultRecordId()
            verify(mockDb, times(0)).deleteSmartCardUser()
         }

         it("Factory Reset without delete payment cards and without Smart Card User Data") {
            val testSubscriber = TestSubscriber<ActionState<FactoryResetCommand>>()

            factoryResetInteractor.factoryResetCommandActionPipe()
                  .createObservable(FactoryResetCommand(
                        ResetOptions.builder()
                              .wipePaymentCards(false)
                              .wipeUserSmartCardData(false)
                              .build())
                  ).subscribe(testSubscriber)

            AssertUtil.assertActionSuccess(testSubscriber, { true })
            verify(cardStorage, times(0)).deleteAllRecords()
            verify(cardStorage, times(0)).deleteDefaultRecordId()
            verify(mockDb, times(0)).deleteSmartCardUser()
         }

         it("Wipe Smart Card Data with delete payment cards") {
            val testSubscriber = TestSubscriber<ActionState<WipeSmartCardDataCommand>>()

            smartCardInteractor.wipeSmartCardDataPipe()
                  .createObservable(WipeSmartCardDataCommand())
                  .subscribe(testSubscriber)

            AssertUtil.assertActionSuccess(testSubscriber, { true })
            verify(cardStorage, times(1)).deleteAllRecords()
            verify(cardStorage, times(1)).deleteDefaultRecordId()
            verify(mockDb, times(1)).deleteSmartCardUser()
         }
      }
   }
}) {
   private companion object {
      lateinit var janet: Janet
      lateinit var analyticsInteractor: AnalyticsInteractor
      lateinit var factoryResetInteractor: FactoryResetInteractor
      lateinit var mockDb: WalletStorage
      lateinit var lostCardRepository: LostCardRepository
      lateinit var cardStorage: RecordsStorage
      lateinit var smartCardLocationInteractor: SmartCardLocationInteractor
      lateinit var settingsInteractor: SettingsInteractor

      val setOfMultiplyStorage: () -> Set<ActionStorage<*>> = {
         setOf(DefaultRecordIdStorage(cardStorage), SmartCardActionStorage(mockDb))
      }

      fun createJanet(): Janet {
         val daggerCommandActionService = CommandActionService()
               .wrapCache()
               .bindStorageSet(setOfMultiplyStorage())
               .bindStorageSet(setOf(WalletRecordsActionStorage(cardStorage)))
               .wrapDagger()

         janet = Janet.Builder()
               .addService(daggerCommandActionService)
               .addService(mockHttpService().wrapStub().wrapCache())
               .addService(SmartCardActionService.createDefault(MockSmartCardClient()))
               .build()

         daggerCommandActionService.registerProvider(Janet::class.java) { janet }
         daggerCommandActionService.registerProvider(WalletStorage::class.java) { mockDb }
         daggerCommandActionService.registerProvider(RecordsStorage::class.java) { cardStorage }
         daggerCommandActionService.registerProvider(LostCardRepository::class.java) { lostCardRepository }
         daggerCommandActionService.registerProvider(Context::class.java, { MockContext() })
         daggerCommandActionService.registerProvider(SmartCardInteractor::class.java, { smartCardInteractor })
         daggerCommandActionService.registerProvider(AnalyticsInteractor::class.java, { analyticsInteractor })
         daggerCommandActionService.registerProvider(FactoryResetInteractor::class.java, { factoryResetInteractor })
         daggerCommandActionService.registerProvider(CachedPhotoUtil::class.java, { mock() })
         daggerCommandActionService.registerProvider(SmartCardLocationInteractor::class.java) { smartCardLocationInteractor }
         daggerCommandActionService.registerProvider(SettingsInteractor::class.java, { settingsInteractor })

         return janet
      }

      fun createMockDb(): WalletStorage = spy()

      fun createLostCardRepository(): LostCardRepository = spy()

      fun mockHttpService(): MockHttpActionService {
         return MockHttpActionService.Builder()
               .bind(MockHttpActionService.Response(200).body(TestDisassociateResponseBody()), { request ->
                  request.url.contains("card_user") && request.method.equals("delete", true)
               })
               .bind(MockHttpActionService.Response(204)) { request ->
                  request.url.endsWith("api/user/settings")
               }
               .build()
      }

      fun createFactoryResetInteractor(janet: Janet) = FactoryResetInteractor(SessionActionPipeCreator(janet))

      fun createSmartCardInteractor(janet: Janet) = SmartCardInteractor(SessionActionPipeCreator(janet), TestSchedulerProvider())

      fun createSmartCardLocationInteractor(janet: Janet) = SmartCardLocationInteractor(SessionActionPipeCreator(janet))

      fun createSettingsInteractor(janet: Janet) = SettingsInteractor(SessionActionPipeCreator(janet))

      fun mockSmartCard(cardId: String): SmartCard {
         val mockedSmartCard: SmartCard = mock()
         whenever(mockedSmartCard.smartCardId()).thenReturn(cardId)
         whenever(mockedSmartCard.cardStatus()).thenReturn(SmartCard.CardStatus.ACTIVE)

         return mockedSmartCard
      }
   }
}