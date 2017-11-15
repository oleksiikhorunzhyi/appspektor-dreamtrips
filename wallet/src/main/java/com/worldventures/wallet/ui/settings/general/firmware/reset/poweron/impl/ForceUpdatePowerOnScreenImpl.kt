package com.worldventures.wallet.ui.settings.general.firmware.reset.poweron.impl

import android.animation.AnimatorSet
import android.animation.ObjectAnimator.ofFloat
import android.support.v7.widget.Toolbar
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.afollestad.materialdialogs.MaterialDialog
import com.worldventures.wallet.R
import com.worldventures.wallet.ui.common.base.WalletBaseController
import com.worldventures.wallet.ui.settings.general.firmware.reset.poweron.ForceUpdatePowerOnPresenter
import com.worldventures.wallet.ui.settings.general.firmware.reset.poweron.ForceUpdatePowerOnScreen
import com.worldventures.wallet.ui.widget.WizardVideoView
import javax.inject.Inject

class ForceUpdatePowerOnScreenImpl : WalletBaseController<ForceUpdatePowerOnScreen, ForceUpdatePowerOnPresenter>(), ForceUpdatePowerOnScreen {

   private lateinit var walletWizardSplashTitle: TextView
   private lateinit var nexrButton: Button
   private lateinit var wizardVideoView: WizardVideoView

   @Inject lateinit var screenPresenter: ForceUpdatePowerOnPresenter

   override fun onFinishInflate(view: View) {
      super.onFinishInflate(view)
      val toolbar = view.findViewById<Toolbar>(R.id.toolbar)
      toolbar.setNavigationOnClickListener { presenter.onBack() }
      walletWizardSplashTitle = view.findViewById(R.id.wallet_wizard_power_on_title)
      nexrButton = view.findViewById(R.id.wallet_wizard_power_on_btn)
      wizardVideoView = view.findViewById(R.id.wizard_video_view)
      hideAllView()
      wizardVideoView.setVideoSource(R.raw.wallet_anim_power_on_sc)

      nexrButton.setOnClickListener { presenter.goNext() }
   }

   override fun inflateView(layoutInflater: LayoutInflater, viewGroup: ViewGroup) =
         layoutInflater.inflate(R.layout.screen_wallet_force_fw_update_power_on, viewGroup, false)!!

   override fun supportConnectionStatusLabel() = false

   override fun supportHttpConnectionStatusLabel() = false

   override fun onAttach(view: View) {
      super.onAttach(view)
      view.postDelayed({ this.startSoarAnimation() }, SHOW_SOAR_TITLE_DELAY)
   }

   override fun getPresenter() = screenPresenter

   private fun hideAllView() {
      listOf(nexrButton, walletWizardSplashTitle, wizardVideoView).forEach { it.alpha = 0f }
   }

   private fun startSoarAnimation() {
      val mainAnimation = AnimatorSet()
      mainAnimation
            .play(ofFloat<View>(nexrButton, View.ALPHA, 1f).setDuration(COMMON_FADE_IN_DELAY))
            .with(ofFloat<View>(walletWizardSplashTitle, View.ALPHA, 1f).setDuration(COMMON_FADE_IN_DELAY))
            .after(ofFloat<View>(wizardVideoView, View.ALPHA, 1f).setDuration(CARD_FADE_IN_DELAY))

      mainAnimation.start()
   }

   override fun showDialogEnableBleAndInternet() {
      MaterialDialog.Builder(context)
            .content(R.string.wallet_firmware_pre_installation_bluetooth)
            .positiveText(R.string.wallet_ok)
            .show()
   }

   companion object {

      private val SHOW_SOAR_TITLE_DELAY = 1000L
      private val CARD_FADE_IN_DELAY = 300L
      private val COMMON_FADE_IN_DELAY = 250L
   }
}
