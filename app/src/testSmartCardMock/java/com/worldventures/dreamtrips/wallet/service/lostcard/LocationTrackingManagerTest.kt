package com.worldventures.dreamtrips.wallet.service.lostcard

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import com.techery.spares.session.SessionHolder
import com.techery.spares.storage.complex_objects.Optional
import com.worldventures.dreamtrips.core.session.UserSession
import com.worldventures.dreamtrips.modules.auth.api.command.LoginCommand
import com.worldventures.dreamtrips.modules.auth.api.command.LogoutCommand
import com.worldventures.dreamtrips.modules.auth.service.LoginInteractor
import com.worldventures.dreamtrips.modules.common.service.LogoutInteractor
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor
import com.worldventures.dreamtrips.wallet.service.SmartCardLocationInteractor
import com.worldventures.dreamtrips.wallet.service.command.reset.WipeSmartCardDataCommand
import com.worldventures.dreamtrips.wallet.service.impl.MockWalletLocationService
import com.worldventures.dreamtrips.wallet.service.lostcard.command.FetchTrackingStatusCommand
import com.worldventures.dreamtrips.wallet.service.lostcard.command.UpdateTrackingStatusCommand
import com.worldventures.dreamtrips.wallet.ui.common.BaseTest
import com.worldventures.dreamtrips.wallet.ui.common.InteractorBuilder
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
   private lateinit var logoutInteractor: LogoutInteractor
   private lateinit var loginInteractor: LoginInteractor

   private val sessionHolder: SessionHolder = mock()

   private fun createLocationTrackingManager(lostCardManager: LostCardManager) = LocationTrackingManager(
         smartCardInteractor, locationInteractor, logoutInteractor, MockWalletLocationService(), lostCardManager,
         loginInteractor, sessionHolder)

   private fun createUserSession() {
      val userSession: UserSession = mock()
      whenever(sessionHolder.get()).thenReturn(Optional.of(userSession))
   }

   @Before
   fun createInteractors() {
      smartCardInteractor = interactorBuilder.createInteractor(SmartCardInteractor::class)
      locationInteractor = interactorBuilder.createInteractor(SmartCardLocationInteractor::class)
      logoutInteractor = interactorBuilder.createInteractor(LogoutInteractor::class)
      loginInteractor = interactorBuilder.createInteractor(LoginInteractor::class)
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

      loginInteractor.loginActionPipe().send(LoginCommand("", ""))

      verify(lostCardManager, times(1)).connect()
      verify(lostCardManager, times(1)).disconnect()
   }

   @Test
   fun testLoginWithDisableFeature() {
      fetchTrackingStatusContract.result(false)

      val lostCardManager: LostCardManager = mock()
      val trackManager = createLocationTrackingManager(lostCardManager)
      trackManager.track()

      loginInteractor.loginActionPipe().send(LoginCommand("", ""))

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

      logoutInteractor.logoutPipe().send(LogoutCommand())

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

      logoutInteractor.logoutPipe().send(LogoutCommand())

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