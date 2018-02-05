package com.worldventures.dreamtrips.social.ui.infopages.presenter

import com.worldventures.core.modules.infopages.model.Document
import com.worldventures.dreamtrips.social.ui.infopages.service.analytics.ViewDocumentAnalyticAction

class DocumentPresenter(private val document: Document?, private val analyticsAction: String?) : WebViewFragmentPresenter<DocumentPresenter.View>() {

   override fun initUrl() = document?.url ?: ""

   override fun pageLoaded(url: String) {
      super.pageLoaded(url)
      analyticsInteractor.analyticsActionPipe().send(ViewDocumentAnalyticAction(analyticsAction, accountUserId))
   }

   interface View : WebViewFragmentPresenter.View

}
