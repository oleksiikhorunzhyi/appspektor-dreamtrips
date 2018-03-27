package com.worldventures.dreamtrips.social.ui.infopages.presenter

import com.worldventures.core.modules.auth.api.command.LoginCommand
import com.worldventures.core.modules.auth.api.command.LogoutCommand
import com.worldventures.core.modules.auth.service.AuthInteractor
import com.worldventures.dreamtrips.social.ui.infopages.service.analytics.OtaViewedAction
import com.worldventures.dreamtrips.social.ui.membership.service.analytics.EnrollMerchantViewedAction
import io.techery.janet.helper.ActionStateSubscriber
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject

open class AuthorizedStaticInfoPresenter<T : AuthorizedStaticInfoPresenter.View> : WebViewFragmentPresenter<T>() {

   @Inject lateinit var authInteractor: AuthInteractor

   override fun load() {
      doWithAuth { super.load() }
   }

   override fun reload() {
      doWithAuth { super.reload() }
   }

   override fun initUrl() = ""

   private fun doWithAuth(updateAction: () -> Unit) {
      appSessionHolder.get().get().apply {
         if (lastUpdate() ?: 0 > LIFE_DURATION) {
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

   open fun sendAnalyticsEnrollMemberViewedAction() {
      analyticsInteractor.analyticsActionPipe().send(EnrollMerchantViewedAction(appSessionHolder.get().get().username()))
   }

   fun sendAnalyticsOtaViewedAction() {
      analyticsInteractor.analyticsActionPipe().send(OtaViewedAction())
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
      private val LIFE_DURATION: Long get() = System.currentTimeMillis() - TimeUnit.MINUTES.toMillis(30)
   }
}
