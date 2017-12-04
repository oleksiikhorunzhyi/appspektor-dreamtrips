package com.worldventures.wallet.ui.wizard.input.manual.impl

import android.support.v7.widget.Toolbar
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import com.afollestad.materialdialogs.MaterialDialog
import com.jakewharton.rxbinding.widget.RxTextView
import com.worldventures.core.utils.HttpErrorHandlingUtil
import com.worldventures.wallet.R
import com.worldventures.wallet.service.command.SmartCardUserCommand
import com.worldventures.wallet.service.command.http.GetSmartCardStatusCommand
import com.worldventures.wallet.ui.common.base.WalletBaseController
import com.worldventures.wallet.ui.common.helper2.error.ErrorViewFactory
import com.worldventures.wallet.ui.common.helper2.error.http.HttpErrorViewProvider
import com.worldventures.wallet.ui.common.helper2.progress.SimpleDialogProgressView
import com.worldventures.wallet.ui.wizard.input.manual.WizardManualInputPresenter
import com.worldventures.wallet.ui.wizard.input.manual.WizardManualInputScreen
import io.techery.janet.operationsubscriber.view.ComposableOperationView
import io.techery.janet.operationsubscriber.view.OperationView
import rx.Observable
import javax.inject.Inject

class WizardManualInputScreenImpl : WalletBaseController<WizardManualInputScreen, WizardManualInputPresenter>(), WizardManualInputScreen {

   private lateinit var scidNumberInput: EditText
   private lateinit var nextButton: Button

   @Inject lateinit var viewPresenter: WizardManualInputPresenter
   @Inject lateinit var httpErrorHandlingUtil: HttpErrorHandlingUtil

   override fun onFinishInflate(view: View) {
      super.onFinishInflate(view)
      val toolbar = view.findViewById<Toolbar>(R.id.toolbar)
      toolbar.setNavigationOnClickListener { presenter.goBack() }
      scidNumberInput = view.findViewById(R.id.wallet_wizard_manual_input_scid)
      scidNumberInput.setOnEditorActionListener { _, action, _ ->
         if (action == EditorInfo.IME_ACTION_NEXT) {
            presenter.checkBarcode(scidNumberInput.text.toString())
            return@setOnEditorActionListener true
         }
         false
      }
      nextButton = view.findViewById(R.id.wallet_wizard_manual_input_next_btn)
      nextButton.setOnClickListener { presenter.checkBarcode(scidNumberInput.text.toString()) }
   }

   override fun getScIdLength(): Int = resources?.getInteger(R.integer.wallet_smart_card_id_length) ?: 0

   override fun inflateView(layoutInflater: LayoutInflater, viewGroup: ViewGroup): View =
         layoutInflater.inflate(R.layout.screen_wallet_wizard_manual_input, viewGroup, false)

   override fun supportConnectionStatusLabel(): Boolean = false

   override fun supportHttpConnectionStatusLabel(): Boolean = false

   override fun buttonEnable(isEnable: Boolean) {
      nextButton.isEnabled = isEnable
   }

   override fun scidInput(): Observable<CharSequence> = RxTextView.textChanges(scidNumberInput)

   override fun provideOperationFetchCardStatus(): OperationView<GetSmartCardStatusCommand> {
      return ComposableOperationView(
            SimpleDialogProgressView(context, R.string.wallet_wizard_assigning_msg, false),
            ErrorViewFactory.builder<GetSmartCardStatusCommand>()
                  .addProvider(HttpErrorViewProvider(context, httpErrorHandlingUtil,
                        { command -> presenter.retry(command.barcode) }) { /*nothing*/ }
                  ).build()
      )
   }

   override fun provideOperationFetchSmartCardUser(): OperationView<SmartCardUserCommand> {
      return ComposableOperationView(
            ErrorViewFactory.builder<SmartCardUserCommand>()
                  .addProvider(HttpErrorViewProvider(context, httpErrorHandlingUtil,
                        { presenter.retryAssignedToCurrentDevice() }) { /*nothing*/ }
                  ).build()
      )
   }

   override fun showErrorCardIsAssignedDialog() {
      MaterialDialog.Builder(context)
            .content(R.string.wallet_wizard_manual_input_card_is_assigned)
            .positiveText(R.string.wallet_ok)
            .onPositive { dialog, _ -> dialog.dismiss() }
            .show()
   }

   override fun getPresenter() = viewPresenter
}
