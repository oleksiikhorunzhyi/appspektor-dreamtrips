package com.worldventures.dreamtrips.wallet.ui.wizard.input.scanner

import com.nhaarman.mockito_kotlin.*
import com.worldventures.dreamtrips.core.permission.PermissionDispatcher
import com.worldventures.dreamtrips.core.permission.PermissionsResult
import com.worldventures.dreamtrips.wallet.domain.entity.ImmutableSmartCardStatus
import com.worldventures.dreamtrips.wallet.service.command.device.DeviceStateCommand
import com.worldventures.dreamtrips.wallet.ui.common.BasePresenterTest
import com.worldventures.dreamtrips.wallet.ui.common.InteractorBuilder
import com.worldventures.dreamtrips.wallet.ui.common.MockDeviceConnectionDelegate
import com.worldventures.dreamtrips.wallet.ui.common.ViewPresenterBinder
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletDeviceConnectionDelegate
import com.worldventures.dreamtrips.wallet.ui.wizard.input.helper.InputBarcodeDelegate
import com.worldventures.dreamtrips.wallet.ui.wizard.input.scanner.impl.WizardScanBarcodePresenterImpl
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
      val deviceConnectionDelegate : WalletDeviceConnectionDelegate = MockDeviceConnectionDelegate()
      permissionDispatcher = mock()
      inputBarcodeDelegate = mock()

      presenter = WizardScanBarcodePresenterImpl(navigator, deviceConnectionDelegate, permissionDispatcher, inputBarcodeDelegate)
   }

   @Test
   fun testRequestCameraPermissionGranted() {
      todo {
         // create factory for PermissionsResult or mock PermissionDispatcher
      }
      whenever(permissionDispatcher.requestPermission(any())).thenReturn(Observable.just(PermissionsResult(0, arrayOf<String>(), intArrayOf(0))))

      presenter.requestCamera()
      verify(screen, times(1)).startCamera()
   }

   @Test
   fun testRequestCameraPermissionDenied() {
      whenever(permissionDispatcher.requestPermission(any())).thenReturn(Observable.just(PermissionsResult(0, arrayOf<String>(), intArrayOf(-1))))

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