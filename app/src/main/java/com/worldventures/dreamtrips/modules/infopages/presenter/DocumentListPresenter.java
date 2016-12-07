package com.worldventures.dreamtrips.modules.infopages.presenter;

import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.infopages.model.Document;
import com.worldventures.dreamtrips.modules.infopages.service.DocumentsInteractor;
import com.worldventures.dreamtrips.modules.infopages.service.command.GetDocumentsCommand;

import java.util.List;

import javax.inject.Inject;

import io.techery.janet.helper.ActionStateSubscriber;

public class DocumentListPresenter extends Presenter<DocumentListPresenter.View> {

   @Inject DocumentsInteractor documentsInteractor;

   @Override
   public void takeView(View view) {
      super.takeView(view);

      observeDocuments();

      getDocuments();
   }

   public void getDocuments() {
      documentsInteractor.getDocumentsPipe().send(new GetDocumentsCommand());
   }

   private void observeDocuments() {
      documentsInteractor.getDocumentsPipe()
            .observe()
            .compose(bindViewToMainComposer())
            .subscribe(new ActionStateSubscriber<GetDocumentsCommand>()
                  .onStart(this::onDocumentsLoadingStarted)
                  .onSuccess(this::onDocumentsLoadSuccess)
                  .onFail(this::onDocumentsLoadError));
   }

   private void onDocumentsLoadingStarted(GetDocumentsCommand command) {
      List<Document> items = command.items();

      if (items == null || items.isEmpty()) {
         view.showProgress();
      } else {
         view.setDocumentList(items);
      }
   }

   private void onDocumentsLoadSuccess(GetDocumentsCommand command) {
      view.setDocumentList(command.items());
      view.hideProgress();
   }

   private void onDocumentsLoadError(GetDocumentsCommand command, Throwable error) {
      view.setDocumentList(command.items());

      handleError(command, error);

      view.hideProgress();
   }

   public interface View extends Presenter.View {

      void setDocumentList(List<Document> documentList);

      void showProgress();

      void hideProgress();
   }
}
