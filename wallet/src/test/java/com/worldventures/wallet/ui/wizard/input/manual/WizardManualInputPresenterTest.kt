package com.worldventures.wallet.ui.wizard.input.manual

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.eq
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.never
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import com.worldventures.wallet.analytics.WalletAnalyticsCommand
import com.worldventures.wallet.domain.entity.SmartCardStatus
import com.worldventures.wallet.service.WalletAnalyticsInteractor
import com.worldventures.wallet.service.command.device.DeviceStateCommand
import com.worldventures.wallet.ui.common.BasePresenterTest
import com.worldventures.wallet.ui.common.InteractorBuilder
import com.worldventures.wallet.ui.common.MockDeviceConnectionDelegate
import com.worldventures.wallet.ui.common.ViewPresenterBinder
import com.worldventures.wallet.ui.common.base.WalletDeviceConnectionDelegate
import com.worldventures.wallet.ui.wizard.input.helper.InputBarcodeDelegate
import com.worldventures.wallet.ui.wizard.input.manual.impl.WizardManualInputPresenterImpl
import io.techery.janet.command.test.Contract
import org.junit.Test
import rx.lang.kotlin.PublishSubject

class WizardManualInputPresenterTest : BasePresenterTest<WizardManualInputScreen, WizardManualInputPresenter>() {

   private lateinit var screen: WizardManualInputScreen
   private lateinit var presenter: WizardManualInputPresenter
   private lateinit var inputBarcodeDelegate: InputBarcodeDelegate

   private val inputSubject = PublishSubject<CharSequence>()

   private val interactorBuilder = InteractorBuilder.configJanet {
      addMockAnalyticsService()
      addMockCommandActionService {
         addContract(Contract.of(DeviceStateCommand::class.java).result(SmartCardStatus()))
         addContract(Contract.of(WalletAnalyticsCommand::class.java).result(null))
      }
   }

   override fun createViewPresenterBinder(): ViewPresenterBinder<WizardManualInputScreen, WizardManualInputPresenter> =
         ViewPresenterBinder(screen, presenter)

   override fun setup() {
      val deviceConnectionDelegate: WalletDeviceConnectionDelegate = MockDeviceConnectionDelegate()
      val analyticsInteractor = interactorBuilder.createInteractor(WalletAnalyticsInteractor::class)

      screen = mockScreen(WizardManualInputScreen::class.java)
      whenever(screen.scidInput()).thenReturn(inputSubject)
      whenever(screen.getScIdLength()).thenReturn(5)

      inputBarcodeDelegate = mock()
      presenter = WizardManualInputPresenterImpl(navigator, deviceConnectionDelegate, analyticsInteractor, inputBarcodeDelegate)
   }

   @Test
   fun testDisablingButton() {
      for (value in listOf("1", "22", "333", "4444")) inputSubject.onNext(value)
      verify(screen, times(4)).buttonEnable(eq(false))
      verify(screen, never()).buttonEnable(eq(true))
   }

   @Test
   fun testEnablingButton() {
      for (value in listOf("00000", "11111")) inputSubject.onNext(value)
      verify(screen, times(2)).buttonEnable(eq(true))
      verify(screen, never()).buttonEnable(eq(false))
   }

   @Test
   fun testGoBack() {
      presenter.goBack()

      verify(navigator, times(1)).goBack()
   }

   @Test
   fun testCheckBarcode() {
      presenter.checkBarcode("")
      verify(inputBarcodeDelegate, times(1)).barcodeEntered(any())
   }
}
