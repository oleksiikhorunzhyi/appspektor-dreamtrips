package com.worldventures.dreamtrips.social.ui.infopages.presenter

import com.worldventures.core.modules.infopages.StaticPageProvider
import com.worldventures.core.service.NewDreamTripsHttpService
import com.worldventures.dreamtrips.core.rx.RxView
import com.worldventures.dreamtrips.core.utils.HeaderProvider
import com.worldventures.dreamtrips.modules.common.command.OfflineErrorCommand
import com.worldventures.dreamtrips.modules.common.presenter.Presenter
import com.worldventures.dreamtrips.modules.common.view.connection_overlay.ConnectionState
import javax.inject.Inject

abstract class WebViewFragmentPresenter<T : WebViewFragmentPresenter.View> : Presenter<T>() {

   @Inject lateinit var headerProvider: HeaderProvider
   @Inject lateinit var provider: StaticPageProvider
   protected lateinit var url: String
   private var inErrorState: Boolean = false

   override fun onInjected() {
      super.onInjected()
      url = initUrl()
   }

   protected abstract fun initUrl(): String

   override fun takeView(view: T) {
      super.takeView(view)
      load()
   }

   fun getAuthToken(): String {
      return NewDreamTripsHttpService.getAuthorizationHeader(appSessionHolder.get().get().apiToken())
   }

   override fun onResume() {
      super.onResume()
      if (inErrorState) load()
   }

   fun noInternetConnection() {
      connectionStatePublishSubject.onNext(ConnectionState.DISCONNECTED)
      offlineErrorInteractor.offlineErrorCommandPipe().send(OfflineErrorCommand())
   }

   protected open fun load() {
      view.load(url, headerProvider.standardWebViewHeaders.apply { putAll(getAdditionalHeaders()) })
   }

   protected open fun reload() {
      view.reload(url, headerProvider.standardWebViewHeaders.apply { putAll(getAdditionalHeaders()) })
   }

   protected open fun getAdditionalHeaders() = mapOf<String, String>()

   fun onReload() {
      reload()
   }

   open fun pageLoaded(url: String) {
      // TODO Check if view is still attached.
      // To improve this and remove check we need to refactor our StaticInfoFragment,
      // saving its state and detecting if page was already loaded
      if (view != null) {
         view.hideLoadingProgress()
      }
   }

   open fun onPageStartLoad(url: String) {

   }

   fun setInErrorState(inErrorState: Boolean) {
      this.inErrorState = inErrorState
   }

   interface View : RxView {

      fun load(localizedUrl: String, headers: Map<String, String>?)

      fun reload(localizedUrl: String, headers: Map<String, String>?)

      fun setRefreshing(refreshing: Boolean)

      fun showError(code: Int)

      fun hideLoadingProgress()
   }

   companion object {
      const val AUTHORIZATION_HEADER_KEY = "Authorization"
   }
}
