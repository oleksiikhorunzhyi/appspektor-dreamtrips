package com.worldventures.dreamtrips.social.ui.infopages.presenter

import com.worldventures.core.modules.auth.api.command.LoginCommand
import com.worldventures.core.modules.auth.api.command.LogoutCommand
import com.worldventures.core.modules.auth.service.AuthInteractor
import com.worldventures.core.utils.ProjectTextUtils
import io.techery.janet.helper.ActionStateSubscriber
import timber.log.Timber
import javax.inject.Inject

open class AuthorizedStaticInfoPresenter<T : AuthorizedStaticInfoPresenter.View>(url: String) : WebViewFragmentPresenter<T>(url) {

   @Inject lateinit var authInteractor: AuthInteractor

   override fun load() {
      doWithAuth { super.load() }
   }

   override fun reload() {
      doWithAuth { super.reload() }
   }

   fun getLegacyAuthTokenBase64(): String {
      return appSessionHolder.get().get().username() + ":" + appSessionHolder.get()
            .get().legacyApiToken().let { "Basic " + ProjectTextUtils.convertToBase64(it) }
   }

   private fun doWithAuth(updateAction: () -> Unit) {
      appSessionHolder.get().get().apply {
         if (lastUpdate() ?: 0 > System.currentTimeMillis() - LIFE_DURATION) {
            updateAction.invoke()
         } else {
            view.setRefreshing(true)
            reLogin()
         }
      }
   }

   fun reLogin() {
      authInteractor.loginActionPipe()
            .createObservable(LoginCommand())
            .compose(bindUntilPauseIoToMainComposer())
            .subscribe(ActionStateSubscriber<LoginCommand>().onSuccess { onLoginSuccess() }
                  .onFail { _, throwable -> onLoginFail(throwable) })
   }

   protected open fun onLoginSuccess() {
      view.setRefreshing(false)
      reload()
   }

   private fun onLoginFail(throwable: Throwable) {
      Timber.e(throwable, "Can't login during WebView loading")
      authInteractor.logoutPipe().send(LogoutCommand())
      view.setRefreshing(false)
   }

   interface View : WebViewFragmentPresenter.View

   companion object {
      private const val LIFE_DURATION = 30 * 1000 // min * millis
   }
}
