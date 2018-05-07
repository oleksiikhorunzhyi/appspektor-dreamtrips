package com.worldventures.dreamtrips.social.ui.infopages.presenter

import com.worldventures.core.modules.infopages.service.command.GetDocumentsCommand
import com.worldventures.dreamtrips.social.ui.infopages.service.analytics.ViewHelpDocumentsTabAnalyticAction

class HelpDocumentListPresenter : DocumentListPresenter() {

   override fun track() {
      analyticsInteractor.analyticsActionPipe().send(ViewHelpDocumentsTabAnalyticAction())
   }

   override fun getAnalyticsSectionName() = HELP_DOCUMENTS_ANALYTICS_SECTION_NAME

   override fun getDocumentType() = GetDocumentsCommand.DocumentType.HELP

   companion object {
      private const val HELP_DOCUMENTS_ANALYTICS_SECTION_NAME = "Help:Documents"
   }
}
