package com.worldventures.dreamtrips.social.ui.infopages.presenter

import android.location.Location

import com.worldventures.dreamtrips.social.ui.infopages.util.PermissionLocationDelegate
import com.worldventures.dreamtrips.social.ui.membership.service.analytics.EnrollMemberViewedAction
import com.worldventures.dreamtrips.social.ui.util.PermissionUIComponent

import javax.inject.Inject

class EnrollMemberPresenter : AuthorizedStaticInfoPresenter<EnrollMemberPresenter.View>() {

   @Inject lateinit var permissionLocationDelegate: PermissionLocationDelegate

   override fun initUrl() = provider.enrollMemberUrl ?: ""

   override fun takeView(view: View) {
      permissionLocationDelegate.setNeedRationalAction(view::showPermissionExplanationText)
      super.takeView(view)
      url = provider.enrollMemberUrl
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
      location?.let { url = provider.getEnrollWithLocation(it.latitude, it.longitude) }
   }

   fun recheckPermissionAccepted(recheckAccepted: Boolean) {
      permissionLocationDelegate.recheckPermissionAccepted(recheckAccepted, bindView<Any>())
   }

   fun sendAnalyticsEnrollViewedAction() {
      analyticsInteractor.analyticsActionPipe().send(EnrollMemberViewedAction(appSessionHolder.get().get().username()))
   }

   interface View : AuthorizedStaticInfoPresenter.View, PermissionUIComponent

}
