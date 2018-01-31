package com.worldventures.dreamtrips.social.ui.infopages.presenter

import com.worldventures.core.modules.infopages.service.command.GetDocumentsCommand
import com.worldventures.dreamtrips.social.ui.infopages.service.analytics.ViewLegalDocumentsAnalyticAction

class LegalTermsPresenter : DocumentListPresenter() {

   override fun takeView(view: View?) {
      super.takeView(view)
      subscribeToErrorUpdates()
   }

   /**
    * We show single common connection overlay over the tabs content.
    * Subscribe to offline errors to be able to handle those happened in tabs and show it.
    */
   private fun subscribeToErrorUpdates() {
      offlineErrorInteractor.offlineErrorCommandPipe()
            .observeSuccess()
            .compose(bindViewToMainComposer())
            .subscribe { reportNoConnection() }
   }

   override fun track() {
      analyticsInteractor.analyticsActionPipe().send(ViewLegalDocumentsAnalyticAction())
   }

   override fun getAnalyticsSectionName() = LEGAL_DOCUMENTS_ANALYTICS_SECTION_NAME

   override fun getDocumentType() = GetDocumentsCommand.DocumentType.LEGAL

   companion object {
      private const val LEGAL_DOCUMENTS_ANALYTICS_SECTION_NAME = "Legal Documents"
   }
}
