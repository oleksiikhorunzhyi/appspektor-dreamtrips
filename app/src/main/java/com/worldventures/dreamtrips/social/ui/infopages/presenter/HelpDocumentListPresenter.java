package com.worldventures.dreamtrips.social.ui.infopages.presenter;

import com.worldventures.core.modules.infopages.service.command.GetDocumentsCommand;
import com.worldventures.dreamtrips.social.ui.infopages.service.analytics.ViewHelpDocumentsTabAnalyticAction;

public class HelpDocumentListPresenter extends DocumentListPresenter {

   private static final String HELP_DOCUMENTS_ANALYTICS_SECTION_NAME = "Help:Documents";

   @Override
   public GetDocumentsCommand.DocumentType getDocumentType() {
      return GetDocumentsCommand.DocumentType.HELP;
   }

   @Override
   public void track() {
      analyticsInteractor.analyticsActionPipe().send(new ViewHelpDocumentsTabAnalyticAction());
   }

   @Override
   public String getAnalyticsSectionName() {
      return HELP_DOCUMENTS_ANALYTICS_SECTION_NAME;
   }
}
