package com.worldventures.wallet.ui.records.swiping.impl

import android.graphics.PointF
import android.support.v7.widget.Toolbar
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.afollestad.materialdialogs.MaterialDialog
import com.facebook.drawee.view.SimpleDraweeView
import com.worldventures.wallet.R
import com.worldventures.wallet.domain.entity.SmartCardUserPhoto
import com.worldventures.wallet.service.command.http.CreateRecordCommand
import com.worldventures.wallet.ui.common.base.WalletBaseController
import com.worldventures.wallet.ui.common.helper2.error.ErrorViewFactory
import com.worldventures.wallet.ui.common.helper2.error.SCConnectionErrorViewProvider
import com.worldventures.wallet.ui.common.helper2.error.SmartCardErrorViewProvider
import com.worldventures.wallet.ui.records.swiping.WizardChargingPresenter
import com.worldventures.wallet.ui.records.swiping.WizardChargingScreen
import com.worldventures.wallet.ui.records.swiping.anim.ChargingSwipingAnimations
import com.worldventures.wallet.util.SmartCardAvatarHelper
import io.techery.janet.operationsubscriber.view.ComposableOperationView
import io.techery.janet.operationsubscriber.view.OperationView
import io.techery.janet.smartcard.action.charger.StartCardRecordingAction
import javax.inject.Inject

class WizardChargingScreenImpl : WalletBaseController<WizardChargingScreen, WizardChargingPresenter>(), WizardChargingScreen {

   @Inject lateinit var screenPresenter: WizardChargingPresenter

   private val swipingAnimations = ChargingSwipingAnimations()

   private lateinit var smartCard: View
   private lateinit var creditCard: View
   private lateinit var userPhoto: SimpleDraweeView

   private var dialog: MaterialDialog? = null

   override fun onFinishInflate(view: View) {
      super.onFinishInflate(view)
      val toolbar = view.findViewById<Toolbar>(R.id.toolbar)
      toolbar.setNavigationOnClickListener { navigateClick() }
      userPhoto = view.findViewById(R.id.user_photo)
      userPhoto.hierarchy.setActualImageFocusPoint(PointF(0f, .5f))
      smartCard = view.findViewById(R.id.smart_card)
      creditCard = view.findViewById(R.id.credit_card)
      SmartCardAvatarHelper.applyGrayScaleColorFilter(userPhoto)
   }

   override fun inflateView(layoutInflater: LayoutInflater, viewGroup: ViewGroup): View {
      return layoutInflater.inflate(R.layout.screen_wallet_wizard_charging, viewGroup, false)
   }

   override fun supportConnectionStatusLabel(): Boolean {
      return false
   }

   override fun supportHttpConnectionStatusLabel(): Boolean {
      return false
   }

   override fun onAttach(view: View) {
      super.onAttach(view)
      swipingAnimations.animateSmartCard(smartCard)
      swipingAnimations.animateBankCard(creditCard)
   }

   override fun getPresenter(): WizardChargingPresenter? {
      return screenPresenter
   }

   private fun navigateClick() {
      presenter!!.goBack()
   }

   override fun showSwipeError() {
      showDialog(getString(R.string.wallet_wizard_charging_swipe_error), false)
   }

   override fun trySwipeAgain() {
      showDialog(getString(R.string.wallet_receive_data_error), false)
   }

   override fun showSwipeSuccess() {
      showDialog(getString(R.string.wallet_add_card_swipe_success), true)
   }

   private fun showDialog(message: String, withProgress: Boolean) {
      val builder = MaterialDialog.Builder(context)
            .content(message)

      if (withProgress) {
         builder.progress(true, 0)
      } else {
         builder.positiveText(R.string.wallet_ok)
         builder.onPositive { dialog, _ ->
            dialog.dismiss()
            presenter!!.goBack()
         }
      }

      if (dialog != null) {
         dialog!!.dismiss()
      }
      dialog = builder.build()
      dialog!!.show()
   }

   override fun userPhoto(photo: SmartCardUserPhoto?) {
      if (photo != null) {
         userPhoto.setImageURI(photo.uri)
      }
   }

   override fun provideOperationCreateRecord(): OperationView<CreateRecordCommand> {
      return ComposableOperationView(
            ErrorViewFactory.builder<CreateRecordCommand>()
                  .addProvider(SCConnectionErrorViewProvider(context))
                  .addProvider(SmartCardErrorViewProvider(context))
                  .build()
      )
   }

   override fun provideOperationStartCardRecording(): OperationView<StartCardRecordingAction> {
      return ComposableOperationView(
            ErrorViewFactory.builder<StartCardRecordingAction>()
                  .addProvider(SCConnectionErrorViewProvider(context,
                        { trySwipeAgain() }) { presenter!!.goBack() })
                  .addProvider(SmartCardErrorViewProvider(context,
                        { trySwipeAgain() }) { presenter!!.goBack() })
                  .build()
      )
   }

   override fun onDetach(view: View) {
      if (dialog != null) {
         dialog!!.dismiss()
      }
      swipingAnimations.stopAnimations()
      super.onDetach(view)
   }
}
