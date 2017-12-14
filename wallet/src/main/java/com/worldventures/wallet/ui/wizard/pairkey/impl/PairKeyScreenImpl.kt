package com.worldventures.wallet.ui.wizard.pairkey.impl

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button

import com.worldventures.wallet.R
import com.worldventures.wallet.service.command.CreateAndConnectToCardCommand
import com.worldventures.wallet.service.provisioning.ProvisioningMode
import com.worldventures.wallet.ui.common.base.WalletBaseController
import com.worldventures.wallet.ui.common.helper2.error.ErrorViewFactory
import com.worldventures.wallet.ui.common.helper2.error.SimpleDialogErrorViewProvider
import com.worldventures.wallet.ui.common.helper2.progress.SimpleDialogProgressView
import com.worldventures.wallet.ui.wizard.pairkey.PairKeyPresenter
import com.worldventures.wallet.ui.wizard.pairkey.PairKeyScreen
import com.worldventures.wallet.util.SmartCardConnectException

import javax.inject.Inject

import io.techery.janet.operationsubscriber.view.ComposableOperationView
import io.techery.janet.operationsubscriber.view.OperationView

class PairKeyScreenImpl private constructor(args: Bundle?) : WalletBaseController<PairKeyScreen, PairKeyPresenter>(args), PairKeyScreen {

   private lateinit var toolbar: Toolbar
   private lateinit var btnNext: Button

   @Inject internal lateinit var screenPresenter: PairKeyPresenter

   private lateinit var operationView: OperationView<CreateAndConnectToCardCommand>

   constructor() : this(null)

   override fun onFinishInflate(view: View) {
      super.onFinishInflate(view)
      toolbar = view.findViewById(R.id.toolbar)
      btnNext = view.findViewById(R.id.button_next)
      btnNext.setOnClickListener { presenter.tryToPairAndConnectSmartCard() }

      operationView = ComposableOperationView(
            SimpleDialogProgressView(context, R.string.wallet_loading, false),
            ErrorViewFactory.builder<CreateAndConnectToCardCommand>()
                  .addProvider(SimpleDialogErrorViewProvider(
                        context,
                        SmartCardConnectException::class.java,
                        R.string.wallet_smartcard_connection_error
                  ) { presenter.goBack() }
                  ).build())
   }

   override fun provideOperationCreateAndConnect(): OperationView<CreateAndConnectToCardCommand> = operationView

   @Suppress("UnsafeCast")
   override val provisionMode: ProvisioningMode
      get() = args.getSerializable(KEY_PROVISION_MODE) as ProvisioningMode

   override val barcode: String
      get() = args.getString(KEY_BARCODE)

   override fun onDetach(view: View) {
      super.onDetach(view)
      operationView.hideError()
      operationView.hideProgress()
   }

   override fun nextButtonEnable(enable: Boolean) {
      btnNext.isEnabled = enable
   }

   override fun showBackButton() {
      toolbar.setNavigationIcon(R.drawable.ic_wallet_vector_arrow_back)
      toolbar.setNavigationOnClickListener { presenter.goBack() }
   }

   override fun hideBackButton() {
      toolbar.navigationIcon = ColorDrawable(Color.TRANSPARENT)
      toolbar.setNavigationOnClickListener(null)
   }

   override fun getPresenter(): PairKeyPresenter = screenPresenter

   override fun inflateView(layoutInflater: LayoutInflater, viewGroup: ViewGroup): View =
         layoutInflater.inflate(R.layout.screen_wallet_wizard_pairkey, viewGroup, false)

   override fun supportConnectionStatusLabel() = false

   override fun supportHttpConnectionStatusLabel() = false

   companion object {

      private val KEY_PROVISION_MODE = "key_provision_mode"
      private val KEY_BARCODE = "key_barcode"

      fun create(mode: ProvisioningMode, barcode: String): PairKeyScreenImpl {
         val args = Bundle()
         args.putSerializable(KEY_PROVISION_MODE, mode)
         args.putString(KEY_BARCODE, barcode)
         return PairKeyScreenImpl(args)
      }
   }
}
