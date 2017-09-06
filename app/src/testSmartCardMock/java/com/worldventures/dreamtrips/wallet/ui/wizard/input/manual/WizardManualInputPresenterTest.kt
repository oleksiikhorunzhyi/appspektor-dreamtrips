package com.worldventures.dreamtrips.wallet.ui.wizard.input.manual

import com.nhaarman.mockito_kotlin.*
import com.worldventures.dreamtrips.wallet.analytics.WalletAnalyticsCommand
import com.worldventures.dreamtrips.wallet.domain.entity.ImmutableSmartCardStatus
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor
import com.worldventures.dreamtrips.wallet.service.WalletAnalyticsInteractor
import com.worldventures.dreamtrips.wallet.service.command.device.DeviceStateCommand
import com.worldventures.dreamtrips.wallet.ui.common.BasePresenterTest
import com.worldventures.dreamtrips.wallet.ui.common.InteractorBuilder
import com.worldventures.dreamtrips.wallet.ui.common.ViewPresenterBinder
import com.worldventures.dreamtrips.wallet.ui.wizard.input.helper.InputBarcodeDelegate
import com.worldventures.dreamtrips.wallet.ui.wizard.input.manual.impl.WizardManualInputPresenterImpl
import io.techery.janet.command.test.Contract
import org.junit.Test
import rx.lang.kotlin.PublishSubject

class WizardManualInputPresenterTest : BasePresenterTest<WizardManualInputScreen, WizardManualInputPresenter>() {

   lateinit var screen : WizardManualInputScreen
   lateinit var presenter : WizardManualInputPresenter
   lateinit var inputBarcodeDelegate: InputBarcodeDelegate

   private val inputSubject = PublishSubject<CharSequence>()

   private val interactorBuilder = InteractorBuilder.configJanet {
      addMockAnalyticsService()
      addMockCommandActionService {
         addContract(Contract.of(DeviceStateCommand::class.java).result(ImmutableSmartCardStatus.builder().build()))
         addContract(Contract.of(WalletAnalyticsCommand::class.java).result(null))
      }
   }

   override fun createViewPresenterBinder(): ViewPresenterBinder<WizardManualInputScreen, WizardManualInputPresenter> =
         ViewPresenterBinder(screen, presenter)

   override fun setup() {
      val smartCardInteractor = interactorBuilder.createInteractor(SmartCardInteractor::class)
      val analyticsInteractor = interactorBuilder.createInteractor(WalletAnalyticsInteractor::class)

      screen = mock()
      whenever(screen.scidInput()).thenReturn(inputSubject)
      whenever(screen.scIdLength).thenReturn(5)

      inputBarcodeDelegate = mock()
      presenter = WizardManualInputPresenterImpl(navigator, smartCardInteractor, walletNetworkService, analyticsInteractor, inputBarcodeDelegate)
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