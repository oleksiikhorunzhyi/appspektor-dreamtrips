package com.worldventures.wallet.ui.wizard.input.manual.impl

import com.worldventures.wallet.analytics.WalletAnalyticsCommand
import com.worldventures.wallet.analytics.wizard.ManualCardInputAction
import com.worldventures.wallet.service.WalletAnalyticsInteractor
import com.worldventures.wallet.ui.common.base.WalletDeviceConnectionDelegate
import com.worldventures.wallet.ui.common.base.WalletPresenterImpl
import com.worldventures.wallet.ui.common.navigation.Navigator
import com.worldventures.wallet.ui.wizard.input.helper.InputBarcodeDelegate
import com.worldventures.wallet.ui.wizard.input.manual.WizardManualInputPresenter
import com.worldventures.wallet.ui.wizard.input.manual.WizardManualInputScreen

class WizardManualInputPresenterImpl(
      navigator: Navigator,
      deviceConnectionDelegate: WalletDeviceConnectionDelegate,
      private val analyticsInteractor: WalletAnalyticsInteractor,
      private val inputBarcodeDelegate: InputBarcodeDelegate)
   : WalletPresenterImpl<WizardManualInputScreen>(navigator, deviceConnectionDelegate), WizardManualInputPresenter {

   override fun attachView(view: WizardManualInputScreen) {
      super.attachView(view)
      inputBarcodeDelegate.init(view)
      analyticsInteractor.walletAnalyticsPipe()
            .send(WalletAnalyticsCommand(ManualCardInputAction()))
      observeScidInput()
   }

   private fun observeScidInput() {

      view!!.scidInput()
            .compose(view!!.bindUntilDetach())
            .subscribe { scid -> view!!.buttonEnable(scid.length == view!!.getScIdLength()) }
   }

   override fun checkBarcode(barcode: String) {
      inputBarcodeDelegate.barcodeEntered(barcode)
   }

   override fun goBack() {
      navigator.goBack()
   }

   override fun retry(barcode: String) {
      inputBarcodeDelegate.retry(barcode)
   }

   override fun retryAssignedToCurrentDevice() {
      inputBarcodeDelegate.retryAssignedToCurrentDevice()
   }
}
