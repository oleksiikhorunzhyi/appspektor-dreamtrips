package com.worldventures.wallet.ui.wizard.power_on.impl

import android.support.v7.widget.Toolbar
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.worldventures.wallet.R
import com.worldventures.wallet.ui.common.base.WalletBaseController
import com.worldventures.wallet.ui.widget.WizardVideoView
import com.worldventures.wallet.ui.wizard.power_on.WizardPowerOnPresenter
import com.worldventures.wallet.ui.wizard.power_on.WizardPowerOnScreen
import com.worldventures.wallet.ui.wizard.welcome.WizardWelcomeScreen

import javax.inject.Inject

@Suppress("UnsafeCallOnNullableType")
class WizardPowerOnScreenImpl : WalletBaseController<WizardWelcomeScreen, WizardPowerOnPresenter>(), WizardPowerOnScreen {

   @Inject lateinit var screenPresenter: WizardPowerOnPresenter

   override fun onFinishInflate(view: View) {
      super.onFinishInflate(view)
      view.findViewById<Toolbar>(R.id.toolbar).setNavigationOnClickListener { presenter.onBack() }
      view.findViewById<WizardVideoView>(R.id.wizard_video_view).setVideoSource(R.raw.wallet_anim_power_on_sc)
      view.findViewById<View>(R.id.wallet_wizard_power_on_btn).setOnClickListener { presenter.onNext() }
   }

   override fun inflateView(layoutInflater: LayoutInflater, viewGroup: ViewGroup) =
         layoutInflater.inflate(R.layout.screen_wallet_wizard_power_on, viewGroup, false)!!

   override fun supportConnectionStatusLabel() = false

   override fun supportHttpConnectionStatusLabel() = false

   override fun getPresenter() = screenPresenter

   override fun screenModule(): Any? = WizardPowerOnScreenModule()
}
