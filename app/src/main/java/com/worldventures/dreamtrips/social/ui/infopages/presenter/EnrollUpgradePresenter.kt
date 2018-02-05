package com.worldventures.dreamtrips.social.ui.infopages.presenter

class EnrollUpgradePresenter : AuthorizedStaticInfoPresenter<AuthorizedStaticInfoPresenter.View>() {

   override fun initUrl() = provider.enrollUpgradeUrl ?: ""

   override fun getAdditionalHeaders() = mapOf(Pair(AUTHORIZATION_HEADER_KEY, getAuthToken()))
}
