package com.worldventures.dreamtrips.wallet.ui.settings.security

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import com.nxtid.mobile.NxtMobileResp
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsInteractor
import com.worldventures.dreamtrips.wallet.domain.entity.ImmutableSmartCardStatus
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor
import com.worldventures.dreamtrips.wallet.service.command.SetPinEnabledCommand
import com.worldventures.dreamtrips.wallet.service.command.device.DeviceStateCommand
import com.worldventures.dreamtrips.wallet.ui.common.BasePresenterTest
import com.worldventures.dreamtrips.wallet.ui.common.InteractorBuilder
import com.worldventures.dreamtrips.wallet.ui.common.ViewPresenterBinder
import com.worldventures.dreamtrips.wallet.ui.common.connectToSmartCard
import com.worldventures.dreamtrips.wallet.ui.settings.security.impl.WalletSecuritySettingsPresenterImpl
import com.worldventures.dreamtrips.wallet.util.WalletFeatureHelper
import com.worldventures.dreamtrips.wallet.util.WalletFeatureHelperFull
import io.techery.janet.command.test.BaseContract
import io.techery.janet.smartcard.client.ImmutableEvent
import io.techery.janet.smartcard.device.SmartCardDevice
import io.techery.janet.smartcard.mock.client.MockSmartCardClient
import org.junit.Test
import org.mockito.Mockito
import rx.lang.kotlin.PublishSubject


class WalletSecuritySettingsPresenterTest : BasePresenterTest<WalletSecuritySettingsScreen, WalletSecuritySettingsPresenter>() {

   lateinit var smartCardInteractor : SmartCardInteractor
   lateinit var walletFeatureHelper : WalletFeatureHelper
   lateinit var screen : WalletSecuritySettingsScreen
   lateinit var presenter : WalletSecuritySettingsPresenter

   private val smartCardClient : MockSmartCardClient = MockSmartCardClient()
   private val deviceStateCommandContract : BaseContract = BaseContract.of(DeviceStateCommand::class.java)
   private val setPinEnabledCommandContract : BaseContract = BaseContract.of(SetPinEnabledCommand::class.java)
   private val lockToggleSubject = PublishSubject<Boolean>()
   private val stealthToggleSubject = PublishSubject<Boolean>()

   private val interactorBuilder = InteractorBuilder.configJanet {
      addMockAnalyticsService()
      addMockSmartCardActionService(smartCardClient)
      addMockCommandActionService {
         addContract(deviceStateCommandContract)
         addContract(setPinEnabledCommandContract)
      }
   }
   override fun createViewPresenterBinder(): ViewPresenterBinder<WalletSecuritySettingsScreen, WalletSecuritySettingsPresenter> =
         ViewPresenterBinder(screen, presenter)

   override fun setup() {
      smartCardInteractor = interactorBuilder.createInteractor(SmartCardInteractor::class)
      val analyticsInteractor = interactorBuilder.createInteractor(AnalyticsInteractor::class)
      deviceStateCommandContract.result(ImmutableSmartCardStatus.builder().build())
      walletFeatureHelper = WalletFeatureHelperFull()
      screen = mock()
      whenever(screen.stealthModeStatus()).thenReturn(stealthToggleSubject.asObservable())
      whenever(screen.lockStatus()).thenReturn(lockToggleSubject.asObservable())
      presenter = WalletSecuritySettingsPresenterImpl(navigator, smartCardInteractor, walletNetworkService,
            analyticsInteractor, walletFeatureHelper)
      interactorBuilder.janet.connectToSmartCard()
   }

   @Test
   fun testfetchSmartCardChanges() {
      verify(screen, times(1)).lockStatus(Mockito.anyBoolean())
      verify(screen, times(1)).stealthModeStatus(Mockito.anyBoolean())
      verify(screen, times(1)).disableDefaultPaymentValue(Mockito.anyLong())
      verify(screen, times(1)).autoClearSmartCardValue(Mockito.anyLong())
   }

   @Test
   fun testPinStatusEventAndCommand() {
      verify(screen, times(1)).setLockToggleEnable(true)
      verify(screen, times(1)).setAddRemovePinState(true)

      smartCardClient.onEvent(ImmutableEvent
            .of(SmartCardDevice.EVENT_PIN_STATUS).withData(NxtMobileResp.RESP_PIN_DISABLED))
      verify(screen, times(1)).setLockToggleEnable(false)
      verify(screen, times(1)).setAddRemovePinState(false)
      Mockito.clearInvocations(screen)

      smartCardClient.onEvent(ImmutableEvent
            .of(SmartCardDevice.EVENT_PIN_STATUS).withData(NxtMobileResp.RESP_PIN_AUTHENTICATED))
      setPinEnabledCommandContract.result(true)
      smartCardInteractor.setPinEnabledCommandActionPipe().send(SetPinEnabledCommand(true))
      verify(screen, times(2)).setLockToggleEnable(true)
      verify(screen, times(2)).setAddRemovePinState(true)
      Mockito.clearInvocations(screen)


      smartCardClient.onEvent(ImmutableEvent
            .of(SmartCardDevice.EVENT_PIN_STATUS).withData(NxtMobileResp.RESP_PIN_USER_INTERFACING))
      setPinEnabledCommandContract.result(false)
      smartCardInteractor.setPinEnabledCommandActionPipe().send(SetPinEnabledCommand(false))
      verify(screen, times(1)).setLockToggleEnable(true)
      verify(screen, times(1)).setAddRemovePinState(true)
      verify(screen, times(1)).setLockToggleEnable(false)
      verify(screen, times(1)).setAddRemovePinState(false)
   }

}