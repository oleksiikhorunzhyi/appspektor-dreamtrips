package com.worldventures.dreamtrips.social.ui.infopages.presenter

class PreviewTemplatePresenter(val link: String) : WebViewFragmentPresenter<WebViewFragmentPresenter.View>() {

   override fun initUrl() = link
}
