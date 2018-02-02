package com.worldventures.wallet.service.lostcard

import com.nhaarman.mockito_kotlin.eq
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.never
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import com.worldventures.core.model.session.SessionHolder
import com.worldventures.core.model.session.UserSession
import com.worldventures.core.modules.auth.api.command.LoginCommand
import com.worldventures.core.modules.auth.api.command.LogoutCommand
import com.worldventures.core.modules.auth.service.AuthInteractor
import com.worldventures.core.storage.complex_objects.Optional
import com.worldventures.wallet.service.SmartCardInteractor
import com.worldventures.wallet.service.SmartCardLocationInteractor
import com.worldventures.wallet.service.command.reset.WipeSmartCardDataCommand
import com.worldventures.core.service.location.MockDetectLocationService
import com.worldventures.wallet.service.lostcard.command.FetchTrackingStatusCommand
import com.worldventures.wallet.service.lostcard.command.UpdateTrackingStatusCommand
import com.worldventures.wallet.ui.common.BaseTest
import com.worldventures.wallet.ui.common.InteractorBuilder
import io.techery.janet.command.test.Contract
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers.anyString

private const val TEST_SMART_CARD_ID = "my_favorite_smart_card"

class LocationTrackingManagerTest : BaseTest() {

   private val fetchTrackingStatusContract = Contract.of(FetchTrackingStatusCommand::class.java)
   private val updateTrackingStatusContract = Contract.of(UpdateTrackingStatusCommand::class.java)

   private val interactorBuilder = InteractorBuilder.configJanet {
      addMockCommandActionService {
         addContract(Contract.of(WipeSmartCardDataCommand::class.java).result(null))
         addContract(fetchTrackingStatusContract)
         addContract(updateTrackingStatusContract)
         val userSession: UserSession = mock()
         addContract(Contract.of(LoginCommand::class.java).result(userSession))
         addContract(Contract.of(LogoutCommand::class.java).result(null))
      }
   }

   private lateinit var smartCardInteractor: SmartCardInteractor
   private lateinit var locationInteractor: SmartCardLocationInteractor
   private lateinit var authInteractor: AuthInteractor

   private val sessionHolder: SessionHolder = mock()

   private lateinit var lostCardManager: LostCardManager
   private lateinit var trackManager: LocationTrackingManager
   private lateinit var locationManager: MockDetectLocationService
   private lateinit var mockSmartCardIdHelper: MockSmartCardIdHelper

   private fun setupManager(trackingStatus: Boolean = false, withUserSession: Boolean = false,
                            smartCardId: String? = null) {
      fetchTrackingStatusContract.result(trackingStatus)
      mockSmartCardIdHelper.initValue = smartCardId
      whenever(sessionHolder.get()).thenReturn(
            if (withUserSession) {
               Optional.of(mock())
            } else {
               Optional.absent()
            }
      )
   }

   @Before
   fun createInteractors() {
      smartCardInteractor = interactorBuilder.createInteractor(SmartCardInteractor::class)
      locationInteractor = interactorBuilder.createInteractor(SmartCardLocationInteractor::class)
      authInteractor = interactorBuilder.createInteractor(AuthInteractor::class)

      locationManager = MockDetectLocationService()
      lostCardManager = mock()
      mockSmartCardIdHelper = MockSmartCardIdHelper(locationInteractor)
      trackManager = LocationTrackingManager(locationInteractor, mockSmartCardIdHelper, locationManager, authInteractor, lostCardManager, sessionHolder)
   }

   @Test
   fun testLaunchAppWithoutLogin() {
      setupManager()
      trackManager.track()

      verify(lostCardManager, never()).connect(anyString())
      verify(lostCardManager, never()).disconnect()
   }

   @Test
   fun testLaunchAppWithTurnedFeatureWithoutSmartCard() {
      setupManager(trackingStatus = true, withUserSession = true)
      trackManager.track()

      verify(lostCardManager, never()).connect(anyString())
      verify(lostCardManager, never()).disconnect()
   }

   @Test
   fun testLaunchAppWithTurnedFeature() {
      setupManager(trackingStatus = true, withUserSession = true, smartCardId = TEST_SMART_CARD_ID)
      trackManager.track()
      verify(lostCardManager, times(1)).connect(eq(TEST_SMART_CARD_ID))
      verify(lostCardManager, never()).disconnect()
   }

   @Test
   fun testLaunchAppWithTurnedOffFeature() {
      setupManager(withUserSession = true, smartCardId = TEST_SMART_CARD_ID)
      trackManager.track()

      verify(lostCardManager, never()).connect(anyString())
      verify(lostCardManager, never()).disconnect()
   }

   @Test
   fun testLaunchAppWithTurnedOffFeatureWithoutSmartCard() {
      setupManager(withUserSession = true)
      trackManager.track()

      verify(lostCardManager, never()).connect(anyString())
      verify(lostCardManager, never()).disconnect()
   }

   @Test
   fun testLoginEnabledFeature() {
      setupManager(trackingStatus = true, smartCardId = TEST_SMART_CARD_ID)
      trackManager.track()

      authInteractor.loginActionPipe().send(LoginCommand("", ""))

      verify(lostCardManager, times(1)).connect(eq(TEST_SMART_CARD_ID))
      verify(lostCardManager, never()).disconnect()
   }

   @Test
   fun testLoginWithDisableFeature() {
      setupManager(smartCardId = TEST_SMART_CARD_ID)
      trackManager.track()

      authInteractor.loginActionPipe().send(LoginCommand("", ""))

      verify(lostCardManager, never()).connect(anyString())
      verify(lostCardManager, never()).disconnect()
   }

   @Test
   fun testLoginWithoutSmartCard() {
      setupManager()
      trackManager.track()

      authInteractor.loginActionPipe().send(LoginCommand("", ""))

      verify(lostCardManager, never()).connect(anyString())
      verify(lostCardManager, never()).disconnect()
   }

   @Test
   fun testAssociateCardAfterLogin() {
      setupManager()
      trackManager.track()

      authInteractor.loginActionPipe().send(LoginCommand("", ""))
      mockSmartCardIdHelper.pushNewSmartCardId(TEST_SMART_CARD_ID)

      verify(lostCardManager, never()).connect(anyString())
      verify(lostCardManager, never()).disconnect()
   }

   @Test
   fun testAssociateCardAndTurnOnTrackingAfterLogin() {
      setupManager()
      trackManager.track()

      authInteractor.loginActionPipe().send(LoginCommand("", ""))
      mockSmartCardIdHelper.pushNewSmartCardId(TEST_SMART_CARD_ID)

      fetchTrackingStatusContract.result(true)
      locationInteractor.fetchTrackingStatusPipe().send(FetchTrackingStatusCommand())

      verify(lostCardManager, times(1)).connect(eq(TEST_SMART_CARD_ID))
      verify(lostCardManager, never()).disconnect()
   }

   @Test
   fun testLogout() {
      setupManager(trackingStatus = true, withUserSession = true, smartCardId = TEST_SMART_CARD_ID)
      trackManager.track()

      authInteractor.logoutPipe().send(LogoutCommand())

      verify(lostCardManager, times(1)).connect(eq(TEST_SMART_CARD_ID))
      verify(lostCardManager, times(1)).disconnect()
   }

   @Test
   fun testReLogin() {
      setupManager(trackingStatus = true, withUserSession = true, smartCardId = TEST_SMART_CARD_ID)
      trackManager.track()

      authInteractor.logoutPipe().send(LogoutCommand())
      authInteractor.loginActionPipe().send(LoginCommand())

      verify(lostCardManager, times(2)).connect(eq(TEST_SMART_CARD_ID))
      verify(lostCardManager, times(1)).disconnect()
   }

   @Test
   fun testChangeLocationSettingsAfterLogout() {
      setupManager(trackingStatus = true, withUserSession = true, smartCardId = TEST_SMART_CARD_ID)
      trackManager.track()

      authInteractor.logoutPipe().send(LogoutCommand())
      locationManager.pushNewLocationSettingsState(false)
      locationManager.pushNewLocationSettingsState(true)

      verify(lostCardManager, times(1)).connect(eq(TEST_SMART_CARD_ID))
      verify(lostCardManager, times(1)).disconnect()
   }

   @Test
   fun testLogoutWithDisableFeature() {
      setupManager(withUserSession = true, smartCardId = TEST_SMART_CARD_ID)
      trackManager.track()

      authInteractor.logoutPipe().send(LogoutCommand())

      verify(lostCardManager, never()).connect(anyString())
      verify(lostCardManager, never()).disconnect()
   }

   @Test
   fun testToggleEnableFeature() {
      setupManager(withUserSession = true, smartCardId = TEST_SMART_CARD_ID)
      trackManager.track()

      verify(lostCardManager, never()).connect(anyString())
      verify(lostCardManager, never()).disconnect()

      updateTrackingStatusContract.result(true)
      locationInteractor.updateTrackingStatusPipe().send(UpdateTrackingStatusCommand(true))

      verify(lostCardManager, times(1)).connect(eq(TEST_SMART_CARD_ID))
      verify(lostCardManager, never()).disconnect()

      updateTrackingStatusContract.result(false)
      locationInteractor.updateTrackingStatusPipe().send(UpdateTrackingStatusCommand(false))

      verify(lostCardManager, times(1)).connect(eq(TEST_SMART_CARD_ID))
      verify(lostCardManager, times(1)).disconnect()
   }
}
