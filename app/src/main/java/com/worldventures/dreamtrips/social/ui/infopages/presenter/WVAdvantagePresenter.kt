package com.worldventures.dreamtrips.social.ui.infopages.presenter

import com.worldventures.core.utils.ProjectTextUtils

class WVAdvantagePresenter : AuthorizedStaticInfoPresenter<AuthorizedStaticInfoPresenter.View>() {

   override fun initUrl() = provider.wvAdvantageUrl ?: ""

   override fun getAdditionalHeaders() = super.getAdditionalHeaders().toMutableMap().apply {
      put(AUTHORIZATION_HEADER_KEY, getLegacyAuthTokenBase64().replace("\n", ""))
      headerProvider.applicationIdentifierHeader.also {
         put(it.name, it.value)
      }
   }

   private fun getLegacyAuthTokenBase64(): String {
      return "Basic " + ProjectTextUtils.convertToBase64(appSessionHolder.get().get().username() + ":" + appSessionHolder.get()
            .get().legacyApiToken())
   }
}
