package com.worldventures.dreamtrips.social.ui.infopages.presenter

import com.worldventures.dreamtrips.modules.dtl.bundle.MerchantIdBundle
import com.worldventures.dreamtrips.modules.dtl.helper.MerchantStaticPageProvider

import javax.inject.Inject

class EnrollMerchantPresenter(url: String, private val merchantBundle: MerchantIdBundle)
   : AuthorizedStaticInfoPresenter<AuthorizedStaticInfoPresenter.View>(url) {

   @Inject lateinit var provider: MerchantStaticPageProvider

   override fun onLoginSuccess() {
      url = provider.getEnrollMerchantUrl(merchantBundle)
      super.onLoginSuccess()
   }
}
