package com.worldventures.wallet.ui.wizard.termsandconditionals.impl

import android.animation.LayoutTransition
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.TextView
import com.afollestad.materialdialogs.MaterialDialog
import com.worldventures.wallet.R
import com.worldventures.wallet.service.command.http.FetchTermsAndConditionsCommand
import com.worldventures.wallet.ui.common.base.WalletBaseController
import com.worldventures.wallet.ui.common.helper2.progress.ViewProgressView
import com.worldventures.wallet.ui.wizard.termsandconditionals.AgreementMode
import com.worldventures.wallet.ui.wizard.termsandconditionals.WizardTermsPresenter
import com.worldventures.wallet.ui.wizard.termsandconditionals.WizardTermsScreen
import io.techery.janet.operationsubscriber.view.ComposableOperationView
import io.techery.janet.operationsubscriber.view.OperationView
import javax.inject.Inject

private const val PARAM_KEY_AGREEMENTS_MODE = "key_agreements_mode"

class WizardTermsScreenImpl(args: Bundle) : WalletBaseController<WizardTermsScreen, WizardTermsPresenter>(args), WizardTermsScreen {

   @Inject lateinit var screenPresenter: WizardTermsPresenter

   private lateinit var termsView: WebView
   private lateinit var agreeBtn: Button
   private lateinit var pb: View
   private var errorDialog: MaterialDialog? = null

   override fun onFinishInflate(view: View) {
      super.onFinishInflate(view)
      val toolbar = view.findViewById<Toolbar>(R.id.toolbar)
      toolbar.setTitle(if (agreementMode == AgreementMode.TAC)
         R.string.wallet_terms_and_conditions_header else
         R.string.wallet_affidavit_header )
      toolbar.setNavigationOnClickListener { presenter.onBack() }
      val userAgreementViewGroup = view.findViewById<ViewGroup>(R.id.container_layout_agreement)
      userAgreementViewGroup.layoutTransition = LayoutTransition()
      val userAgreementSplash = view.findViewById<TextView>(R.id.txt_accept_agreement)
      val userAgreementSplashTail = if (agreementMode == AgreementMode.TAC)
         R.string.wallet_wizard_splash_accept_tail_agreement else
         R.string.wallet_wizard_splash_accept_tail_affidavit
      userAgreementSplash.text = getString(R.string.wallet_wizard_splash_accept_head, getString(userAgreementSplashTail))
      agreeBtn = view.findViewById(R.id.wallet_wizard_terms_and_conditions_agree_btn)
      agreeBtn.visibility = GONE
      agreeBtn.setOnClickListener { presenter.acceptTermsPressed() }
      pb = view.findViewById(R.id.pb)
      termsView = view.findViewById(R.id.termsView)
      termsView.webViewClient = object : WebViewClient() {
         override fun onPageFinished(view: WebView, url: String) {
            if (view.progress == 100) {
               pb.visibility = GONE
               if (agreementMode == AgreementMode.AFFIDAVIT && url != view.originalUrl) {
                  agreeBtn.visibility = VISIBLE
               } else if (agreementMode == AgreementMode.TAC) {
                  agreeBtn.visibility = VISIBLE
               }
            }
         }

         override fun onReceivedError(view: WebView, request: WebResourceRequest, error: WebResourceError) {
            super.onReceivedError(view, request, error)
            pb.visibility = GONE
            view.visibility = INVISIBLE
            showLoadTermsError()
         }

         override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {
            view.visibility = VISIBLE
         }

         override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
            if (url.startsWith("mailto:")) {
               val emailIntent = Intent(Intent.ACTION_SENDTO, Uri.parse(url))
               context.startActivity(Intent.createChooser(emailIntent, getString(R.string.wallet_email_app_choose_dialog_title)))
               view.reload()
               return true
            }
            return super.shouldOverrideUrlLoading(view, url)
         }
      }
   }

   override fun inflateView(layoutInflater: LayoutInflater, viewGroup: ViewGroup): View {
      return layoutInflater.inflate(R.layout.screen_wallet_wizard_termsandconditions, viewGroup, false)
   }

   override fun supportConnectionStatusLabel(): Boolean {
      return false
   }

   override fun supportHttpConnectionStatusLabel(): Boolean {
      return false
   }

   fun showLoadTermsError() {
      buildErrorDialog(MaterialDialog.SingleButtonCallback { _, _ -> termsView.reload() })
   }

   override fun onDetach(view: View) {
      errorDialog?.dismiss()
      errorDialog = null
      super.onDetach(view)
   }

   override fun getPresenter(): WizardTermsPresenter {
      return screenPresenter
   }

   override fun showTerms(url: String) {
      termsView.loadUrl("about:blank")
      termsView.loadUrl(url)
   }

   fun failedToLoadTerms() {
      buildErrorDialog(MaterialDialog.SingleButtonCallback { _, _ -> presenter.loadTerms() })
   }

   override fun termsOperationView(): OperationView<FetchTermsAndConditionsCommand> {

      return ComposableOperationView<FetchTermsAndConditionsCommand>(ViewProgressView(pb), null, this)
   }

   private fun buildErrorDialog(retryAction: MaterialDialog.SingleButtonCallback) {
      errorDialog = MaterialDialog.Builder(context).title(R.string.wallet_error_label)
            .content(R.string.wallet_terms_and_conditions_load_failed)
            .positiveText(R.string.wallet_retry_label)
            .onPositive(retryAction)
            .show()
   }

   override fun showError(p0: FetchTermsAndConditionsCommand?, throwable: Throwable?) {
      failedToLoadTerms()
   }

   override fun isErrorVisible(): Boolean {
      return errorDialog != null && errorDialog!!.isShowing
   }

   override fun hideError() {
      errorDialog?.dismiss()
   }

   override fun screenModule(): Any? {
      return WizardTermsScreenModule()
   }

   @Suppress("UnsafeCast")
   override val agreementMode: AgreementMode
      get() = args.getSerializable(PARAM_KEY_AGREEMENTS_MODE) as AgreementMode

   companion object {
      fun create(mode: AgreementMode): WizardTermsScreenImpl {
         val args = Bundle()
         args.putSerializable(PARAM_KEY_AGREEMENTS_MODE, mode)
         return WizardTermsScreenImpl(args)
      }
   }
}
