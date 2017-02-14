package com.worldventures.dreamtrips.modules.infopages.presenter;

import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsInteractor;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.infopages.model.Document;
import com.worldventures.dreamtrips.modules.infopages.service.DocumentsInteractor;
import com.worldventures.dreamtrips.modules.infopages.service.analytics.ViewDocumentsTabAnalyticAction;
import com.worldventures.dreamtrips.modules.infopages.service.command.GetDocumentsCommand;

import java.util.List;

import javax.inject.Inject;

import io.techery.janet.helper.ActionStateSubscriber;

public class DocumentListPresenter extends Presenter<DocumentListPresenter.View> {

   @Inject DocumentsInteractor documentsInteractor;
   @Inject AnalyticsInteractor analyticsInteractor;

   @Override
   public void takeView(View view) {
      super.takeView(view);

      observeDocumentsChanges();

      refreshDocuments();
   }

   public void onSelectedFromPager() {
      analyticsInteractor.analyticsActionPipe().send(new ViewDocumentsTabAnalyticAction());
   }

   public void refreshDocuments() {
      documentsInteractor.getDocumentsActionPipe().send(new GetDocumentsCommand(true));
   }

   public void loadNextDocuments() {
      documentsInteractor.getDocumentsActionPipe().send(new GetDocumentsCommand());
   }

   private void observeDocumentsChanges() {
      documentsInteractor.getDocumentsActionPipe()
            .observe()
            .compose(bindViewToMainComposer())
            .subscribe(new ActionStateSubscriber<GetDocumentsCommand>()
                  .onStart(this::onDocumentsReloadingStarted)
                  .onSuccess(this::onDocumentsLoadSuccess)
                  .onFail(this::onDocumentsLoadError));
   }

   private void onDocumentsReloadingStarted(GetDocumentsCommand command) {
      if (command.isRefreshCommand()) {
         view.showProgress();

         List<Document> items = command.items();
         if (view.isAdapterEmpty() && items != null && !items.isEmpty()) view.setDocumentList(items);
      }
   }

   private void onDocumentsLoadSuccess(GetDocumentsCommand command) {
      view.updateLoadingStatus(command.isNoMoreElements());
      view.setDocumentList(command.items());
      view.hideProgress();
   }

   private void onDocumentsLoadError(GetDocumentsCommand command, Throwable error) {
      view.setDocumentList(command.items());
      view.hideProgress();

      handleError(command, error);
   }

   public interface View extends Presenter.View {

      boolean isAdapterEmpty();

      void setDocumentList(List<Document> documentList);

      void showProgress();

      void hideProgress();

      void updateLoadingStatus(boolean noMoreElements);
   }
}
