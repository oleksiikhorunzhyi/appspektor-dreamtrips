package com.worldventures.dreamtrips.social.ui.infopages.presenter

import android.location.Location

import com.worldventures.core.modules.infopages.StaticPageProvider
import com.worldventures.dreamtrips.social.ui.infopages.util.PermissionLocationDelegate
import com.worldventures.dreamtrips.social.ui.util.PermissionUIComponent

import javax.inject.Inject

class EnrollRepPresenter(url: String) : AuthorizedStaticInfoPresenter<EnrollRepPresenter.View>(url) {

   @Inject lateinit var staticPageProvider: StaticPageProvider
   @Inject lateinit var permissionLocationDelegate: PermissionLocationDelegate

   override fun takeView(view: EnrollRepPresenter.View) {
      permissionLocationDelegate.setNeedRationalAction(view::showPermissionExplanationText)
      super.takeView(view)
   }

   override fun load() {
      permissionLocationDelegate.setLocationObtainedAction {
         updateUrlWithLocation(it)
         super.load()
      }
      permissionLocationDelegate.requestPermission(true, bindView<Any>())
   }

   override fun reload() {
      permissionLocationDelegate.setLocationObtainedAction {
         updateUrlWithLocation(it)
         super.reload()
      }
      permissionLocationDelegate.requestPermission(true, bindView<Any>())
   }

   private fun updateUrlWithLocation(location: Location?) {
      location?.let { url = staticPageProvider.getEnrollWithLocation(it.latitude, it.longitude) }
   }

   fun recheckPermissionAccepted(recheckAccepted: Boolean) {
      permissionLocationDelegate.recheckPermissionAccepted(recheckAccepted, bindView<Any>())
   }

   interface View : AuthorizedStaticInfoPresenter.View, PermissionUIComponent

}
