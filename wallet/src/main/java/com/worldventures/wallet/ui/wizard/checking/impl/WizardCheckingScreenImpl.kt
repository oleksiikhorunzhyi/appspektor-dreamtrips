package com.worldventures.wallet.ui.wizard.checking.impl

import android.support.v7.widget.Toolbar
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button

import com.worldventures.wallet.R
import com.worldventures.wallet.ui.common.base.WalletBaseController
import com.worldventures.wallet.ui.widget.WalletCheckWidget
import com.worldventures.wallet.ui.wizard.checking.WizardCheckingPresenter
import com.worldventures.wallet.ui.wizard.checking.WizardCheckingScreen

import javax.inject.Inject

@Suppress("UnsafeCallOnNullableType")
class WizardCheckingScreenImpl : WalletBaseController<WizardCheckingScreen, WizardCheckingPresenter>(), WizardCheckingScreen {

   private lateinit var checkInternet: WalletCheckWidget
   private lateinit var checkBluetooth: WalletCheckWidget
   private lateinit var nextButton: Button

   @Inject lateinit var screenPresenter: WizardCheckingPresenter

   override fun onFinishInflate(view: View) {
      super.onFinishInflate(view)
      view.findViewById<Toolbar>(R.id.toolbar).setNavigationOnClickListener { presenter.goBack() }
      checkInternet = view.findViewById(R.id.check_widget_wifi)
      checkBluetooth = view.findViewById(R.id.check_widget_bluetooth)
      nextButton = view.findViewById(R.id.next_button)
      nextButton.setOnClickListener { presenter.goNext() }
   }

   override fun inflateView(layoutInflater: LayoutInflater, viewGroup: ViewGroup) =
         layoutInflater.inflate(R.layout.screen_wallet_wizard_checking, viewGroup, false)!!

   override fun supportConnectionStatusLabel() = false

   override fun supportHttpConnectionStatusLabel() = false

   override fun networkAvailable(available: Boolean) {
      checkInternet.setTitle(if (available)
         R.string.wallet_wizard_checks_network_available
      else
         R.string.wallet_wizard_checks_network_not_available
      )
      checkInternet.setChecked(available)
   }

   override fun bluetoothEnable(enable: Boolean) {
      checkBluetooth.setTitle(if (enable)
         R.string.wallet_wizard_checks_bluetooth_enable
      else
         R.string.wallet_wizard_checks_bluetooth_not_enable)
      checkBluetooth.setChecked(enable)
   }

   override fun bluetoothDoesNotSupported() {
      checkBluetooth.setTitle(R.string.wallet_wizard_checks_bluetooth_is_not_supported)
   }

   override fun buttonEnable(enable: Boolean) {
      nextButton.isEnabled = enable
   }

   override fun getPresenter() = screenPresenter

   override fun screenModule(): Any? = WizardCheckingScreenModule()
}
