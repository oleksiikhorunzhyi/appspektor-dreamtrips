package com.worldventures.dreamtrips.social.ui.infopages.presenter

import com.worldventures.dreamtrips.core.utils.AnalyticsInteractorProxy
import com.worldventures.dreamtrips.social.ui.infopages.service.analytics.ViewBookTravelAnalytics
import com.worldventures.dreamtrips.social.ui.infopages.view.fragment.staticcontent.StaticInfoFragment

import javax.inject.Inject

class OtaPresenter(url: String) : AuthorizedStaticInfoPresenter<AuthorizedStaticInfoPresenter.View>(url) {

   @Inject lateinit var analyticsInteractorProxy: AnalyticsInteractorProxy

   override fun pageLoaded(url: String) {
      super.pageLoaded(url)
      if (StaticInfoFragment.BLANK_PAGE == url) {
         analyticsInteractorProxy.sendCommonAnalytic(ViewBookTravelAnalytics())
      }
   }
}
