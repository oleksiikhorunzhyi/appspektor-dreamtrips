package com.worldventures.dreamtrips.social.ui.infopages.presenter

import android.location.Location

import com.worldventures.core.modules.infopages.StaticPageProvider
import com.worldventures.dreamtrips.social.ui.infopages.util.PermissionLocationDelegate
import com.worldventures.dreamtrips.social.ui.util.PermissionUIComponent

import javax.inject.Inject

class EnrollRepPresenter(url: String) : AuthorizedStaticInfoPresenter<EnrollRepPresenter.View>(url) {

   @field:Inject lateinit var staticPageProvider: StaticPageProvider
   @field:Inject lateinit var permissionLocationDelegate: PermissionLocationDelegate

   override fun takeView(view: EnrollRepPresenter.View) {
      permissionLocationDelegate.setNeedRationalAction({ view.showPermissionExplanationText(it) })
      super.takeView(view)
   }

   override fun load() {
      permissionLocationDelegate.setLocationObtainedAction { location ->
         updateUrlWithLocation(location)
         super.load()
      }
      permissionLocationDelegate.requestPermission(true, bindView<Any>())
   }

   override fun reload() {
      permissionLocationDelegate.setLocationObtainedAction { location ->
         updateUrlWithLocation(location)
         super.reload()
      }
      permissionLocationDelegate.requestPermission(true, bindView<Any>())
   }

   private fun updateUrlWithLocation(location: Location?) {
      if (location == null) return
      url = staticPageProvider.getEnrollRepWithLocation(location.latitude, location.longitude)
   }

   fun recheckPermissionAccepted(recheckAccepted: Boolean) {
      permissionLocationDelegate.recheckPermissionAccepted(recheckAccepted, bindView<Any>())
   }

   interface View : AuthorizedStaticInfoPresenter.View, PermissionUIComponent

}
