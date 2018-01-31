package com.worldventures.dreamtrips.social.ui.infopages.presenter

import android.location.Location

import com.worldventures.core.modules.infopages.StaticPageProvider
import com.worldventures.dreamtrips.social.ui.infopages.util.PermissionLocationDelegate
import com.worldventures.dreamtrips.social.ui.util.PermissionUIComponent

import javax.inject.Inject

class EnrollMemberPresenter(url: String) : AuthorizedStaticInfoPresenter<EnrollMemberPresenter.View>(url) {

   @Inject lateinit var staticPageProvider: StaticPageProvider
   @Inject lateinit var permissionLocationDelegate: PermissionLocationDelegate

   override fun takeView(view: View) {
      permissionLocationDelegate.setNeedRationalAction(view::showPermissionExplanationText)
      super.takeView(view)
   }

   public override fun load() {
      permissionLocationDelegate.apply {
         setLocationObtainedAction {
            updateUrlWithLocation(it)
            super.load()
         }

         requestPermission(true, bindView<Any>())
      }
   }

   override fun reload() {
      permissionLocationDelegate.apply {
         setLocationObtainedAction {
            updateUrlWithLocation(it)
            super.reload()
         }
         requestPermission(true, bindView<Any>())
      }
   }

   private fun updateUrlWithLocation(location: Location?) {
      location?.let { url = staticPageProvider.getEnrollWithLocation(it.latitude, it.longitude) }
   }

   fun recheckPermissionAccepted(recheckAccepted: Boolean) {
      permissionLocationDelegate.recheckPermissionAccepted(recheckAccepted, bindView<Any>())
   }

   interface View : AuthorizedStaticInfoPresenter.View, PermissionUIComponent

}
