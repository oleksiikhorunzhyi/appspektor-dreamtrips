package com.worldventures.dreamtrips.social.ui.infopages.presenter

import com.worldventures.core.service.NewDreamTripsHttpService
import com.worldventures.dreamtrips.core.rx.RxView
import com.worldventures.dreamtrips.modules.common.presenter.Presenter
import com.worldventures.dreamtrips.modules.common.view.connection_overlay.ConnectionState

open class WebViewFragmentPresenter<T : WebViewFragmentPresenter.View>(protected var url: String) : Presenter<T>() {

   private var inErrorState: Boolean = false

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
   }

   protected open fun load() {
      view.load(url)
   }

   protected open fun reload() {
      view.reload(url)
   }

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

   fun setInErrorState(inErrorState: Boolean) {
      this.inErrorState = inErrorState
   }

   interface View : RxView {

      fun load(localizedUrl: String)

      fun reload(localizedUrl: String)

      fun setRefreshing(refreshing: Boolean)

      fun showError(code: Int)

      fun hideLoadingProgress()
   }
}
