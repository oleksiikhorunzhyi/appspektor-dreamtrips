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

      getDocuments();
   }

   private void getDocuments() {
      documentsInteractor.getDocumentsPipe()
            .createObservable(new GetDocumentsCommand())
            .compose(bindViewToMainComposer())
            .subscribe(new ActionStateSubscriber<GetDocumentsCommand>()
                  .onStart(action -> view.showProgress())
                  .onSuccess(action -> {
                     view.hideProgress();
                     view.setDocumentList(action.getResult());
                  })
                  .onFail((action, error) -> {
                     view.hideProgress();
                     handleError(action, error);
                  }));
   }

   public interface View extends Presenter.View {

      void setDocumentList(List<Document> documentList);

      void showProgress();

      void hideProgress();
   }
}
