package com.worldventures.dreamtrips.social.ui.infopages.presenter

import com.worldventures.dreamtrips.core.utils.AnalyticsInteractorProxy
import com.worldventures.dreamtrips.social.ui.infopages.service.analytics.ViewBookTravelAnalytics
import com.worldventures.dreamtrips.social.ui.infopages.view.fragment.staticcontent.StaticInfoFragment

import javax.inject.Inject

class OtaPresenter : AuthorizedStaticInfoPresenter<AuthorizedStaticInfoPresenter.View>() {

   @Inject lateinit var analyticsInteractorProxy: AnalyticsInteractorProxy

   override fun initUrl() = provider.otaPageUrl ?: ""

   override fun getAdditionalHeaders() = mapOf(AUTHORIZATION_HEADER_KEY to getAuthToken())

   override fun pageLoaded(url: String) {
      super.pageLoaded(url)
      if (StaticInfoFragment.BLANK_PAGE == url) {
         analyticsInteractorProxy.sendCommonAnalytic(ViewBookTravelAnalytics())
      }
   }
}
