package com.worldventures.wallet.ui.wizard.termsandconditionals.impl

import android.animation.LayoutTransition
import android.annotation.TargetApi
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.TextView
import com.worldventures.wallet.R
import com.worldventures.wallet.service.command.http.FetchSmartCardAgreementsCommand
import com.worldventures.wallet.ui.common.base.WalletBaseController
import com.worldventures.wallet.ui.common.helper2.progress.ViewProgressView
import com.worldventures.wallet.ui.wizard.termsandconditionals.AgreementMode
import com.worldventures.wallet.ui.wizard.termsandconditionals.WizardTermsPresenter
import com.worldventures.wallet.ui.wizard.termsandconditionals.WizardTermsScreen
import io.techery.janet.operationsubscriber.view.ComposableOperationView
import io.techery.janet.operationsubscriber.view.OperationView
import rx.functions.Action0
import timber.log.Timber
import javax.inject.Inject

private const val PARAM_KEY_AGREEMENTS_MODE = "key_agreements_mode"
private const val STATE_KEY_CONTENT_LOADED = "WizardTermsScreenImpl#STATE_KEY_CONTENT_LOADED"

private const val CLEAR_WEB_VIEW_URL = "about:blank"
private const val SUCCESS_PAGE_LOADING_PROGRESS = 100

class WizardTermsScreenImpl(args: Bundle) : WalletBaseController<WizardTermsScreen, WizardTermsPresenter>(args), WizardTermsScreen {

   @Inject lateinit var screenPresenter: WizardTermsPresenter
   private var contentLoaded: Boolean = false

   private lateinit var webView: WebView
   private lateinit var agreeBtn: Button
   private lateinit var pb: View
   private lateinit var errorView: View
   private lateinit var btnRetryFetch: Button

   @Suppress("UnsafeCast")
   override val agreementMode: AgreementMode
      get() = args.getSerializable(PARAM_KEY_AGREEMENTS_MODE) as AgreementMode

   override fun onFinishInflate(view: View) {
      super.onFinishInflate(view)
      val toolbar: Toolbar = view.findViewById(R.id.toolbar)
      toolbar.setTitle(if (agreementMode == AgreementMode.TAC)
         R.string.wallet_terms_and_conditions_header else
         R.string.wallet_affidavit_header)
      toolbar.setNavigationOnClickListener { presenter.onBack() }
      val userAgreementViewGroup = view.findViewById<ViewGroup>(R.id.container_layout_agreement)
      userAgreementViewGroup.layoutTransition = LayoutTransition()
      val userAgreementSplash = view.findViewById<TextView>(R.id.txt_accept_agreement)
      val userAgreementSplashTail = if (agreementMode == AgreementMode.TAC)
         R.string.wallet_wizard_splash_accept_tail_agreement else
         R.string.wallet_wizard_splash_accept_tail_affidavit
      userAgreementSplash.text = getString(R.string.wallet_wizard_splash_accept_head, getString(userAgreementSplashTail))
      val tvAgreementFetchError = view.findViewById<TextView>(R.id.tv_agreements_fetch_error)
      tvAgreementFetchError.text = getString(R.string.wallet_wizard_agreements_load_failed, getString(userAgreementSplashTail))
      agreeBtn = view.findViewById(R.id.wallet_wizard_terms_and_conditions_agree_btn)
      agreeBtn.visibility = GONE
      agreeBtn.setOnClickListener { presenter.acceptTermsPressed() }
      pb = view.findViewById(R.id.pb)
      errorView = view.findViewById(R.id.agreements_error_view)
      btnRetryFetch = view.findViewById(R.id.btn_retry_agreements_fetch)
      webView = view.findViewById(R.id.webView)

      webView.webViewClient = object : WebViewClient() {

         override fun onPageFinished(view: WebView, url: String) {
            if (CLEAR_WEB_VIEW_URL == url || view.progress != SUCCESS_PAGE_LOADING_PROGRESS) return

            contentLoaded = true
            pb.visibility = GONE
            agreeBtn.visibility = VISIBLE
         }

         @TargetApi(Build.VERSION_CODES.M)
         override fun onReceivedError(view: WebView, request: WebResourceRequest, error: WebResourceError) {
            handleError(view, request.url.path)
         }

         @Suppress("OverridingDeprecatedMember", "DEPRECATION")
         override fun onReceivedError(view: WebView, errorCode: Int, description: String?, failingUrl: String) {
            handleError(view, failingUrl)
         }

         private fun handleError(view: WebView, failingUrl: String) {
            Timber.d("Page wasn't loaded: %s", failingUrl)
            if (view.originalUrl == failingUrl) {
               pb.visibility = GONE
               showLoadTermsError()
            }
         }

         override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {
            Timber.d("Start page loading: %s", url)
         }

         @TargetApi(Build.VERSION_CODES.N)
         override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest) =
               overrideUrlLoading(view, request.url.toString())

         @Suppress("OverridingDeprecatedMember", "DEPRECATION")
         override fun shouldOverrideUrlLoading(view: WebView, url: String) =
               overrideUrlLoading(view, url)

         private fun overrideUrlLoading(view: WebView, url: String): Boolean {
            if (url.startsWith("mailto:")) {
               val emailIntent = Intent(Intent.ACTION_SENDTO, Uri.parse(url))
               context.startActivity(Intent.createChooser(emailIntent, getString(R.string.wallet_email_app_choose_dialog_title)))
               view.reload()
               return true
            }
            return false
         }
      }
   }

   override fun inflateView(layoutInflater: LayoutInflater, viewGroup: ViewGroup): View
         = layoutInflater.inflate(R.layout.screen_wallet_wizard_termsandconditions, viewGroup, false)

   override fun supportConnectionStatusLabel() = false

   override fun supportHttpConnectionStatusLabel() = false

   fun showLoadTermsError() {
      configureErrorView(Action0 { webView.reload() })
   }

   override fun onAttach(view: View) {
      super.onAttach(view)
      if (!contentLoaded) {
         webView.loadUrl(CLEAR_WEB_VIEW_URL)
         presenter.loadTerms()
      }
   }

   override fun getPresenter(): WizardTermsPresenter = screenPresenter

   override fun showTerms(url: String) {
      webView.loadUrl(url)
   }

   private fun failedToLoadTerms() {
      configureErrorView(Action0 { presenter.loadTerms() })
   }

   override fun termsOperationView(): OperationView<FetchSmartCardAgreementsCommand> {
      return ComposableOperationView<FetchSmartCardAgreementsCommand>(ViewProgressView(pb), null, this)
   }

   private fun configureErrorView(retryAction: Action0) {
      errorView.visibility = VISIBLE
      btnRetryFetch.setOnClickListener { retryAction.call() }
   }

   override fun showError(p0: FetchSmartCardAgreementsCommand?, throwable: Throwable?) {
      failedToLoadTerms()
   }

   override fun isErrorVisible() = errorView.visibility == VISIBLE

   override fun hideError() {
      errorView.visibility = GONE
   }

   override fun onSaveViewState(view: View, outState: Bundle) {
      super.onSaveViewState(view, outState)
      outState.putBoolean(STATE_KEY_CONTENT_LOADED, contentLoaded)
      if (contentLoaded) {
         webView.saveState(outState)
      }
   }

   override fun onRestoreViewState(view: View, savedViewState: Bundle) {
      super.onRestoreViewState(view, savedViewState)
      contentLoaded = savedViewState.getBoolean(STATE_KEY_CONTENT_LOADED, false)
      if (contentLoaded) {
         webView.restoreState(savedViewState)
      }
   }

   override fun onDestroyView(view: View) {
      super.onDestroyView(view)
      webView.webViewClient = null
//      webView.destroy()
   }

   override fun screenModule() = WizardTermsScreenModule()

   companion object {
      fun create(mode: AgreementMode): WizardTermsScreenImpl {
         val args = Bundle()
         args.putSerializable(PARAM_KEY_AGREEMENTS_MODE, mode)
         return WizardTermsScreenImpl(args)
      }
   }
}
