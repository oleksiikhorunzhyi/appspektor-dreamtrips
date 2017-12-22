package com.worldventures.wallet.ui.wizard.assign.impl

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.worldventures.core.utils.HttpErrorHandlingUtil
import com.worldventures.wallet.R
import com.worldventures.wallet.service.command.wizard.AddDummyRecordCommand
import com.worldventures.wallet.service.command.wizard.WizardCompleteCommand
import com.worldventures.wallet.service.provisioning.ProvisioningMode
import com.worldventures.wallet.ui.common.base.WalletBaseController
import com.worldventures.wallet.ui.common.helper2.error.ErrorViewFactory
import com.worldventures.wallet.ui.common.helper2.error.RetryDialogErrorView
import com.worldventures.wallet.ui.common.helper2.error.SCConnectionErrorViewProvider
import com.worldventures.wallet.ui.common.helper2.error.SmartCardErrorViewProvider
import com.worldventures.wallet.ui.common.helper2.error.http.HttpErrorViewProvider
import com.worldventures.wallet.ui.common.helper2.progress.WalletProgressView
import com.worldventures.wallet.ui.widget.WalletProgressWidget
import com.worldventures.wallet.ui.wizard.assign.WizardAssignUserPresenter
import com.worldventures.wallet.ui.wizard.assign.WizardAssignUserScreen

import javax.inject.Inject

import io.techery.janet.operationsubscriber.view.ComposableOperationView
import io.techery.janet.operationsubscriber.view.OperationView

class WizardAssignUserScreenImpl : WalletBaseController<WizardAssignUserScreen, WizardAssignUserPresenter>, WizardAssignUserScreen {

   private lateinit var assignProgress: WalletProgressWidget

   @Inject lateinit var screenPresenter: WizardAssignUserPresenter
   @Inject lateinit var httpErrorHandlingUtil: HttpErrorHandlingUtil

   @Suppress("UnsafeCast")
   override val provisionMode: ProvisioningMode
      get() = args.getSerializable(KEY_PROVISION_MODE) as ProvisioningMode

   constructor() : super()

   constructor(args: Bundle) : super(args)

   override fun onFinishInflate(view: View) {
      super.onFinishInflate(view)
      val toolbar = view.findViewById<Toolbar>(R.id.toolbar)
      toolbar.navigationIcon = ColorDrawable(Color.TRANSPARENT)
      assignProgress = view.findViewById(R.id.assign_progress)
   }

   override fun inflateView(layoutInflater: LayoutInflater, viewGroup: ViewGroup): View {
      return layoutInflater.inflate(R.layout.screen_wallet_wizard_assign_smartcard, viewGroup, false)
   }

   override fun supportConnectionStatusLabel(): Boolean {
      return false
   }

   override fun supportHttpConnectionStatusLabel(): Boolean {
      return false
   }

   override fun provideOperationView(): OperationView<WizardCompleteCommand> {
      return ComposableOperationView(WalletProgressView(assignProgress),
            ErrorViewFactory.builder<WizardCompleteCommand>()
                  .addProvider(HttpErrorViewProvider(context, httpErrorHandlingUtil,
                        { presenter.onWizardComplete() }
                  ) { presenter.onWizardCancel() })
                  .build())
   }

   override fun provideDummyRecordOperationView(): OperationView<AddDummyRecordCommand> {
      return ComposableOperationView(WalletProgressView(assignProgress),
            ErrorViewFactory.builder<AddDummyRecordCommand>()
                  .addProvider(SmartCardErrorViewProvider(context,
                        { presenter.retryUploadDummyCards() }, { presenter.cancelUploadDummyCards() }))
                  .addProvider(SCConnectionErrorViewProvider(context,
                        { presenter.retryUploadDummyCards() }, { presenter.cancelUploadDummyCards() }))
                  .defaultErrorView(RetryDialogErrorView(context, R.string.wallet_wizard_finish_error_sample_cards,
                        { presenter.retryUploadDummyCards() }, { presenter.cancelUploadDummyCards() }))
                  .build())
   }

   override fun getPresenter(): WizardAssignUserPresenter = screenPresenter

   override fun screenModule() = WizardAssignUserScreenModule()

   companion object {

      private val KEY_PROVISION_MODE = "key_provision_mode"

      fun create(provisioningMode: ProvisioningMode): WizardAssignUserScreenImpl {
         val args = Bundle()
         args.putSerializable(KEY_PROVISION_MODE, provisioningMode)
         return WizardAssignUserScreenImpl(args)
      }
   }
}
