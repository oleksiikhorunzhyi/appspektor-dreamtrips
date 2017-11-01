package com.worldventures.wallet.ui.wizard.input.manual.impl

import com.worldventures.wallet.analytics.WalletAnalyticsCommand
import com.worldventures.wallet.analytics.wizard.ManualCardInputAction
import com.worldventures.wallet.service.WalletAnalyticsInteractor
import com.worldventures.wallet.ui.common.base.WalletDeviceConnectionDelegate
import com.worldventures.wallet.ui.common.base.WalletPresenterImpl
import com.worldventures.wallet.ui.common.navigation.Navigator
import com.worldventures.wallet.ui.wizard.input.helper.BaseBarcodeInputPresenter
import com.worldventures.wallet.ui.wizard.input.helper.InputBarcodeDelegate
import com.worldventures.wallet.ui.wizard.input.manual.WizardManualInputPresenter
import com.worldventures.wallet.ui.wizard.input.manual.WizardManualInputScreen

class WizardManualInputPresenterImpl(
      navigator: Navigator,
      deviceConnectionDelegate: WalletDeviceConnectionDelegate,
      private val analyticsInteractor: WalletAnalyticsInteractor,
      private val inputBarcodeDelegate: InputBarcodeDelegate)
   : WalletPresenterImpl<WizardManualInputScreen>(navigator, deviceConnectionDelegate), WizardManualInputPresenter,
      BaseBarcodeInputPresenter by inputBarcodeDelegate {

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

   override fun goBack() {
      navigator.goBack()
   }

}
