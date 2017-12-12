package com.worldventures.wallet.ui.wizard.input.scanner.impl

import android.app.Activity
import android.support.design.widget.Snackbar
import android.support.v7.widget.Toolbar
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import com.afollestad.materialdialogs.MaterialDialog
import com.bluelinelabs.conductor.ControllerChangeHandler
import com.bluelinelabs.conductor.ControllerChangeType
import com.google.zxing.Result
import com.worldventures.core.utils.HttpErrorHandlingUtil
import com.worldventures.wallet.R
import com.worldventures.wallet.service.command.SmartCardUserCommand
import com.worldventures.wallet.service.command.http.GetSmartCardStatusCommand
import com.worldventures.wallet.ui.common.base.WalletBaseController
import com.worldventures.wallet.ui.common.helper2.error.ErrorViewFactory
import com.worldventures.wallet.ui.common.helper2.error.SimpleDialogErrorViewProvider
import com.worldventures.wallet.ui.common.helper2.error.http.HttpErrorViewProvider
import com.worldventures.wallet.ui.common.helper2.progress.SimpleDialogProgressView
import com.worldventures.wallet.ui.widget.WalletBarCodeFinder
import com.worldventures.wallet.ui.widget.WalletBarCodeScanner
import com.worldventures.wallet.ui.wizard.input.scanner.WizardScanBarcodePresenter
import com.worldventures.wallet.ui.wizard.input.scanner.WizardScanBarcodeScreen
import io.techery.janet.operationsubscriber.view.ComposableOperationView
import io.techery.janet.operationsubscriber.view.OperationView
import me.dm7.barcodescanner.zxing.ZXingScannerView
import javax.inject.Inject

@Suppress("UnsafeCallOnNullableType")
class WizardScanBarcodeScreenImpl : WalletBaseController<WizardScanBarcodeScreen, WizardScanBarcodePresenter>(),
      WizardScanBarcodeScreen, ZXingScannerView.ResultHandler {

   private var scanner: WalletBarCodeScanner? = null
   private lateinit var contentView: View

   @Inject lateinit var viewPresenter: WizardScanBarcodePresenter
   @Inject lateinit var httpErrorHandlingUtil: HttpErrorHandlingUtil

   override fun onFinishInflate(view: View) {
      super.onFinishInflate(view)
      val toolbar = view.findViewById<Toolbar>(R.id.toolbar)
      toolbar.setNavigationOnClickListener { presenter.goBack() }
      contentView = view.findViewById(R.id.content)
      val finder = view.findViewById<WalletBarCodeFinder>(R.id.scanner_view_finder)
      scanner = view.findViewById(R.id.scanner_view)
      scanner?.setBarCodeFinder(finder)
      scanner?.setResultHandler(this)
      val manualInputView = view.findViewById<View>(R.id.wallet_wizard_scan_barcode_manual_input)
      manualInputView.setOnClickListener { presenter.startManualInput() }
   }

   override fun inflateView(layoutInflater: LayoutInflater, viewGroup: ViewGroup): View =
         layoutInflater.inflate(R.layout.screen_wallet_wizard_barcode_scan, viewGroup, false)

   override fun supportConnectionStatusLabel() = false

   override fun supportHttpConnectionStatusLabel() = false

   override fun onActivityStarted(activity: Activity) {
      super.onActivityStarted(activity)
      if (isAttached) presenter.requestCamera()
   }

   override fun onActivityStopped(activity: Activity) {
      scanner?.stopCamera()
      super.onActivityStopped(activity)
   }

   override fun onDetach(view: View) {
      scanner?.stopCamera()
      super.onDetach(view)
      activity!!.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
   }

   override fun getPresenter() = viewPresenter

   override fun startCamera() {
      scanner?.startCamera()
   }

   override fun showRationaleForCamera() {
      Snackbar.make(view!!, R.string.permission_camera_rationale, Snackbar.LENGTH_SHORT).show()
   }

   override fun showDeniedForCamera() {
      Snackbar.make(view!!, R.string.no_camera_permission, Snackbar.LENGTH_SHORT).show()
   }

   override fun provideOperationFetchCardStatus(): OperationView<GetSmartCardStatusCommand> {
      return ComposableOperationView(
            SimpleDialogProgressView(context, R.string.wallet_wizard_assigning_msg, false),
            ErrorViewFactory.builder<GetSmartCardStatusCommand>()
                  .addProvider(SimpleDialogErrorViewProvider(context, NumberFormatException::class.java,
                        R.string.wallet_wizard_scan_barcode_invalid_format,
                        { presenter.retryScan() }, null) { presenter.retryScan() })
                  .addProvider(HttpErrorViewProvider(context, httpErrorHandlingUtil,
                        { command -> presenter.retry(command.barcode) }
                  ) { presenter.retryScan() })
                  .build()
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
            .content(R.string.wallet_wizard_scan_barcode_card_is_assigned)
            .positiveText(R.string.wallet_ok)
            .onPositive { _, _ -> presenter.retryScan() }
            .cancelListener { presenter.retryScan() }
            .show()
   }

   override fun reset() {
      scanner?.resumeCameraPreview(this)
   }

   override fun handleResult(result: Result) {
      presenter.checkBarcode(result.text)
   }

   override fun onChangeStarted(changeHandler: ControllerChangeHandler, changeType: ControllerChangeType) {
      super.onChangeStarted(changeHandler, changeType)
      scanner?.visibility = View.GONE
   }

   override fun onChangeEnded(changeHandler: ControllerChangeHandler, changeType: ControllerChangeType) {
      super.onChangeEnded(changeHandler, changeType)
      scanner?.visibility = View.VISIBLE
      if (changeType.isEnter) presenter.requestCamera()
   }
}
