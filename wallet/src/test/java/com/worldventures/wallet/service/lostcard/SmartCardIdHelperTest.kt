package com.worldventures.wallet.service.lostcard

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import com.worldventures.core.model.session.Feature
import com.worldventures.core.model.session.UserSession
import com.worldventures.wallet.model.createTestSmartCard
import com.worldventures.wallet.service.SmartCardInteractor
import com.worldventures.wallet.service.SmartCardLocationInteractor
import com.worldventures.wallet.service.command.ActiveSmartCardCommand
import com.worldventures.wallet.service.command.reset.WipeSmartCardDataCommand
import com.worldventures.wallet.service.command.wizard.FetchAssociatedSmartCardCommand
import com.worldventures.wallet.service.command.wizard.FetchAssociatedSmartCardCommand.AssociatedCard
import com.worldventures.wallet.ui.common.BaseTest
import com.worldventures.wallet.ui.common.InteractorBuilder
import com.worldventures.wallet.util.NoActiveSmartCardException
import io.techery.janet.command.test.Contract
import org.junit.Before
import org.junit.Test
import rx.observers.TestSubscriber
import kotlin.test.assertEquals

private const val TEST_SMART_CARD_ID = "test_id"

class SmartCardIdHelperTest : BaseTest() {

   private val fetchTrackingStatusContract = Contract.of(FetchAssociatedSmartCardCommand::class.java)
   private val activeSmartCardContract = Contract.of(ActiveSmartCardCommand::class.java)

   private val interactorBuilder = InteractorBuilder.configJanet {
      addMockCommandActionService {
         addContract(Contract.of(WipeSmartCardDataCommand::class.java).result(null))
         addContract(fetchTrackingStatusContract)
         addContract(activeSmartCardContract)
      }
   }

   private lateinit var smartCardInteractor: SmartCardInteractor
   private lateinit var locationInteractor: SmartCardLocationInteractor

   private lateinit var smartCardIdHelper: SmartCardIdHelper

   @Before
   fun setupSmartCardIdHelper() {
      smartCardInteractor = interactorBuilder.createInteractor(SmartCardInteractor::class)
      locationInteractor = interactorBuilder.createInteractor(SmartCardLocationInteractor::class)

      smartCardIdHelper = SmartCardIdHelperImpl(smartCardInteractor, locationInteractor)
   }

   @Test
   fun testActiveSmartCardExist() {
      activeSmartCardContract.result(createTestSmartCard(TEST_SMART_CARD_ID))

      val testSubscriber = TestSubscriber<String>()
      smartCardIdHelper.smartCardIdObservable()
            .subscribe(testSubscriber)

      assertEquals(1, testSubscriber.onNextEvents.size)
      assertEquals(TEST_SMART_CARD_ID, testSubscriber.onNextEvents[0])
   }

   @Test
   fun testActiveSmartCardDoesNotExist() {
      activeSmartCardContract.exception(NoActiveSmartCardException(""))

      val testSubscriber = TestSubscriber<String>()
      smartCardIdHelper.smartCardIdObservable()
            .subscribe(testSubscriber)

      assertEquals(0, testSubscriber.onNextEvents.size)
   }

   @Test
   fun testFactoryReset() {
      activeSmartCardContract.result(createTestSmartCard(TEST_SMART_CARD_ID))

      val testSubscriber = TestSubscriber<String>()
      smartCardIdHelper.smartCardIdObservable()
            .subscribe(testSubscriber)

      smartCardInteractor.wipeSmartCardDataPipe().send(WipeSmartCardDataCommand())
      assertEquals(2, testSubscriber.onNextEvents.size)
      assertEquals(null, testSubscriber.onNextEvents[1])
   }

   @Test
   fun testRequestOnSCAfterLogin() {
      activeSmartCardContract.exception(NoActiveSmartCardException(""))

      val testSubscriber = TestSubscriber<String>()
      smartCardIdHelper.smartCardIdObservable()
            .subscribe(testSubscriber)

      val userSession: UserSession = mock()
      whenever(userSession.permissions()).thenReturn(listOf(Feature(Feature.DTL), Feature(Feature.WALLET)))
      fetchTrackingStatusContract.result(AssociatedCard(createTestSmartCard(TEST_SMART_CARD_ID)))
      smartCardIdHelper.fetchSmartCardFromServer(userSession)

      assertEquals(1, testSubscriber.onNextEvents.size)
      assertEquals(TEST_SMART_CARD_ID, testSubscriber.onNextEvents[0])
   }

   @Test
   fun testRequestOnSCAfterLoginWithoutWalletPermission() {
      activeSmartCardContract.exception(NoActiveSmartCardException(""))
      fetchTrackingStatusContract.result(AssociatedCard())

      val testSubscriber = TestSubscriber<String>()
      smartCardIdHelper.smartCardIdObservable()
            .subscribe(testSubscriber)

      val userSession: UserSession = mock()
      whenever(userSession.permissions()).thenReturn(listOf(Feature(Feature.DTL)))
      smartCardIdHelper.fetchSmartCardFromServer(userSession)

      assertEquals(0, testSubscriber.onNextEvents.size)
   }
}