package com.worldventures.wallet.ui.wizard.input.scanner

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import com.worldventures.core.ui.util.permission.PermissionDispatcher
import com.worldventures.core.ui.util.permission.PermissionsResult
import com.worldventures.wallet.domain.entity.ImmutableSmartCardStatus
import com.worldventures.wallet.service.command.device.DeviceStateCommand
import com.worldventures.wallet.ui.common.BasePresenterTest
import com.worldventures.wallet.ui.common.InteractorBuilder
import com.worldventures.wallet.ui.common.MockDeviceConnectionDelegate
import com.worldventures.wallet.ui.common.ViewPresenterBinder
import com.worldventures.wallet.ui.common.base.WalletDeviceConnectionDelegate
import com.worldventures.wallet.ui.wizard.input.helper.InputBarcodeDelegate
import com.worldventures.wallet.ui.wizard.input.scanner.impl.WizardScanBarcodePresenterImpl
import io.techery.janet.command.test.Contract
import org.junit.Test
import rx.Observable
import kotlin.test.todo

class WizardScanBarcodePresenterTest : BasePresenterTest<WizardScanBarcodeScreen, WizardScanBarcodePresenter>() {

   lateinit var screen: WizardScanBarcodeScreen
   lateinit var presenter: WizardScanBarcodePresenter
   lateinit var inputBarcodeDelegate: InputBarcodeDelegate
   lateinit var permissionDispatcher: PermissionDispatcher

   private val interactorBuilder = InteractorBuilder.configJanet {
      addMockAnalyticsService()
      addMockCommandActionService {
         addContract(Contract.of(DeviceStateCommand::class.java).result(ImmutableSmartCardStatus.builder().build()))
      }
   }

   override fun createViewPresenterBinder(): ViewPresenterBinder<WizardScanBarcodeScreen, WizardScanBarcodePresenter> = ViewPresenterBinder(screen, presenter)

   override fun setup() {
      screen = mockScreen(WizardScanBarcodeScreen::class.java)
      val deviceConnectionDelegate: WalletDeviceConnectionDelegate = MockDeviceConnectionDelegate()
      permissionDispatcher = mock()
      inputBarcodeDelegate = mock()

      presenter = WizardScanBarcodePresenterImpl(navigator, deviceConnectionDelegate, permissionDispatcher, inputBarcodeDelegate)
   }

   @Test
   fun testRequestCameraPermissionGranted() {
      todo {
         // create factory for PermissionsResult or mock PermissionDispatcher
      }
      whenever(permissionDispatcher.requestPermission(any())).thenReturn(Observable.just(PermissionsResult(0, arrayOf<String>(), 0)))

      presenter.requestCamera()
      verify(screen, times(1)).startCamera()
   }

   @Test
   fun testRequestCameraPermissionDenied() {
      whenever(permissionDispatcher.requestPermission(any())).thenReturn(Observable.just(PermissionsResult(0, arrayOf<String>(), -1)))

      presenter.requestCamera()
      verify(screen, times(1)).showDeniedForCamera()
   }

   @Test
   fun testRequestCameraPermissionRationale() {
      whenever(permissionDispatcher.requestPermission(any())).thenReturn(Observable.just(PermissionsResult(0, arrayOf<String>(), true)))

      presenter.requestCamera()
      verify(screen, times(1)).showRationaleForCamera()
   }

   @Test
   fun testGoBack() {
      presenter.goBack()

      verify(navigator, times(1)).goBack()
   }

   @Test
   fun testCheckBarcode() {
      presenter.barcodeScanned("")

      verify(inputBarcodeDelegate, times(1)).barcodeEntered(any())
   }
}
