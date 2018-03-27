package com.worldventures.dreamtrips.social.ui.infopages.presenter

import com.worldventures.dreamtrips.modules.dtl.bundle.MerchantIdBundle
import com.worldventures.dreamtrips.modules.dtl.helper.MerchantStaticPageProvider

import javax.inject.Inject

class EnrollMerchantPresenter(private val merchantBundle: MerchantIdBundle?)
   : AuthorizedStaticInfoPresenter<AuthorizedStaticInfoPresenter.View>() {

   @Inject lateinit var merChantProvider: MerchantStaticPageProvider

   override fun initUrl() = merChantProvider.getEnrollMerchantUrl(merchantBundle) ?: ""

   override fun onLoginSuccess() {
      url = merChantProvider.getEnrollMerchantUrl(merchantBundle)
      super.onLoginSuccess()
   }
}
