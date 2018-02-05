package com.worldventures.dreamtrips.social.ui.infopages.presenter

class WVAdvantagePresenter : AuthorizedStaticInfoPresenter<AuthorizedStaticInfoPresenter.View>() {

   override fun initUrl() = provider.wvAdvantageUrl ?: ""

   override fun getAdditionalHeaders() = super.getAdditionalHeaders().toMutableMap().apply {
      put(AUTHORIZATION_HEADER_KEY, getLegacyAuthTokenBase64().replace("\n", ""))
      headerProvider.applicationIdentifierHeader.also {
         put(it.name, it.value)
      }
   }
}
