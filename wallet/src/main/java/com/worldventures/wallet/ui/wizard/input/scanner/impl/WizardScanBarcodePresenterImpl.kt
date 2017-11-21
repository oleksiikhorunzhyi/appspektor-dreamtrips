package com.worldventures.wallet.ui.wizard.input.scanner.impl

import com.worldventures.core.ui.util.permission.PermissionConstants
import com.worldventures.core.ui.util.permission.PermissionDispatcher
import com.worldventures.core.ui.util.permission.PermissionSubscriber
import com.worldventures.wallet.ui.common.base.WalletDeviceConnectionDelegate
import com.worldventures.wallet.ui.common.base.WalletPresenterImpl
import com.worldventures.wallet.ui.common.navigation.Navigator
import com.worldventures.wallet.ui.wizard.input.helper.BaseBarcodeInputPresenter
import com.worldventures.wallet.ui.wizard.input.helper.InputBarcodeDelegate
import com.worldventures.wallet.ui.wizard.input.scanner.WizardScanBarcodePresenter
import com.worldventures.wallet.ui.wizard.input.scanner.WizardScanBarcodeScreen

class WizardScanBarcodePresenterImpl(
      navigator: Navigator,
      deviceConnectionDelegate: WalletDeviceConnectionDelegate,
      private val permissionDispatcher: PermissionDispatcher, // todo: remove it form presenter.
      private val inputBarcodeDelegate: InputBarcodeDelegate)
   : WalletPresenterImpl<WizardScanBarcodeScreen>(navigator, deviceConnectionDelegate), WizardScanBarcodePresenter,
      BaseBarcodeInputPresenter by inputBarcodeDelegate {

   override fun attachView(view: WizardScanBarcodeScreen) {
      super.attachView(view)
      inputBarcodeDelegate.init(view)
   }

   override fun requestCamera() {
      permissionDispatcher.requestPermission(PermissionConstants.CAMERA_PERMISSIONS, true)
            .compose(view.bindUntilDetach())
            .subscribe(PermissionSubscriber().onPermissionGrantedAction { view.startCamera() }
                  .onPermissionRationaleAction { view.showRationaleForCamera() }
                  .onPermissionDeniedAction { view.showDeniedForCamera() })
   }

   override fun startManualInput() {
      navigator.goWizardManualInput()
   }

   override fun goBack() {
      navigator.goBack()
   }

   override fun retryScan() {
      view.reset()
   }
}
