package com.worldventures.wallet.service.lostcard

import com.nhaarman.mockito_kotlin.mock
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

   private fun createLocationTrackingManager(lostCardManager: LostCardManager) = LocationTrackingManager(
         smartCardInteractor, locationInteractor, MockDetectLocationService(), authInteractor, lostCardManager,
         sessionHolder)

   private fun createUserSession() {
      val userSession: UserSession = mock()
      whenever(sessionHolder.get()).thenReturn(Optional.of(userSession))
   }

   @Before
   fun createInteractors() {
      smartCardInteractor = interactorBuilder.createInteractor(SmartCardInteractor::class)
      locationInteractor = interactorBuilder.createInteractor(SmartCardLocationInteractor::class)
      authInteractor = interactorBuilder.createInteractor(AuthInteractor::class)
      whenever(sessionHolder.get()).thenReturn(Optional.absent())
   }

   @Test
   fun testLaunchAppWithTurnedFeature() {
      fetchTrackingStatusContract.result(true)
      createUserSession()

      val lostCardManager: LostCardManager = mock()
      val trackManager = createLocationTrackingManager(lostCardManager)
      trackManager.track()

      verify(lostCardManager, times(1)).connect()
      verify(lostCardManager, times(0)).disconnect()
   }

   @Test
   fun testLaunchAppWithTurnedOffFeature() {
      fetchTrackingStatusContract.result(false)
      createUserSession()

      val lostCardManager: LostCardManager = mock()
      val trackManager = createLocationTrackingManager(lostCardManager)
      trackManager.track()

      verify(lostCardManager, times(0)).connect()
      verify(lostCardManager, times(1)).disconnect()
   }

   @Test
   fun testLogin() {
      fetchTrackingStatusContract.result(true)

      val lostCardManager: LostCardManager = mock()
      val trackManager = createLocationTrackingManager(lostCardManager)
      trackManager.track()

      authInteractor.loginActionPipe().send(LoginCommand("", ""))

      verify(lostCardManager, times(1)).connect()
      verify(lostCardManager, times(1)).disconnect()
   }

   @Test
   fun testLoginWithDisableFeature() {
      fetchTrackingStatusContract.result(false)

      val lostCardManager: LostCardManager = mock()
      val trackManager = createLocationTrackingManager(lostCardManager)
      trackManager.track()

      authInteractor.loginActionPipe().send(LoginCommand("", ""))

      verify(lostCardManager, times(0)).connect()
      verify(lostCardManager, times(1)).disconnect()
   }

   @Test
   fun testLogout() {
      fetchTrackingStatusContract.result(true)
      createUserSession()

      val lostCardManager: LostCardManager = mock()
      val trackManager = createLocationTrackingManager(lostCardManager)
      trackManager.track()

      authInteractor.logoutPipe().send(LogoutCommand())

      verify(lostCardManager, times(1)).connect()
      verify(lostCardManager, times(1)).disconnect()
   }

   @Test
   fun testLogoutWithDisableFeature() {
      fetchTrackingStatusContract.result(false)
      createUserSession()

      val lostCardManager: LostCardManager = mock()
      val trackManager = createLocationTrackingManager(lostCardManager)
      trackManager.track()

      authInteractor.logoutPipe().send(LogoutCommand())

      verify(lostCardManager, times(0)).connect()
      verify(lostCardManager, times(2)).disconnect()
   }

   @Test
   fun testToggleEnableFeature() {
      fetchTrackingStatusContract.result(false)
      createUserSession()

      val lostCardManager: LostCardManager = mock()
      val trackManager = createLocationTrackingManager(lostCardManager)
      trackManager.track()

      verify(lostCardManager, times(0)).connect()
      verify(lostCardManager, times(1)).disconnect()

      updateTrackingStatusContract.result(true)
      locationInteractor.updateTrackingStatusPipe().send(UpdateTrackingStatusCommand(true))

      verify(lostCardManager, times(1)).connect()
      verify(lostCardManager, times(1)).disconnect()

      updateTrackingStatusContract.result(false)
      locationInteractor.updateTrackingStatusPipe().send(UpdateTrackingStatusCommand(false))

      verify(lostCardManager, times(1)).connect()
      verify(lostCardManager, times(2)).disconnect()
   }
}
