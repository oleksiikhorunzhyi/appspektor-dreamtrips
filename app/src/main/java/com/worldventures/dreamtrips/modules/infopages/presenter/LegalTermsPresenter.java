package com.worldventures.dreamtrips.modules.infopages.presenter;

import com.worldventures.dreamtrips.modules.infopages.service.analytics.ViewLegalDocumentsAnalyticAction;
import com.worldventures.dreamtrips.modules.infopages.service.command.GetDocumentsCommand;

public class LegalTermsPresenter extends DocumentListPresenter {

   private static final String LEGAL_DOCUMENTS_ANALYTICS_SECTION_NAME = "Legal Documents";

   @Override
   public void takeView(View view) {
      super.takeView(view);
      subscribeToErrorUpdates();
   }

   /**
    * We show single common connection overlay over the tabs content.
    * Subscribe to offline errors to be able to handle those happened in tabs and show it.
    */
   private void subscribeToErrorUpdates() {
      offlineErrorInteractor.offlineErrorCommandPipe()
            .observeSuccess()
            .compose(bindViewToMainComposer())
            .subscribe(command -> reportNoConnection());
   }

   @Override
   public GetDocumentsCommand.DocumentType getDocumentType() {
      return GetDocumentsCommand.DocumentType.LEGAL;
   }

   @Override
   public void track() {
      analyticsInteractor.analyticsActionPipe().send(new ViewLegalDocumentsAnalyticAction());
   }

   @Override
   public String getAnalyticsSectionName() {
      return LEGAL_DOCUMENTS_ANALYTICS_SECTION_NAME;
   }
}
