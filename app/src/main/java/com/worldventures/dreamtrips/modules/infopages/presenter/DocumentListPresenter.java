package com.worldventures.dreamtrips.modules.infopages.presenter;

import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsInteractor;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.infopages.model.Document;
import com.worldventures.dreamtrips.modules.infopages.service.DocumentsInteractor;
import com.worldventures.dreamtrips.modules.infopages.service.analytics.ViewDocumentsTabAnalyticAction;
import com.worldventures.dreamtrips.modules.infopages.service.command.GetDocumentsCommand;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import io.techery.janet.helper.ActionStateSubscriber;
import rx.Observable;

public class DocumentListPresenter extends Presenter<DocumentListPresenter.View> {

   private static final long DEBOUNCE_VISIBILITY_CHANGE = 600;

   @Inject DocumentsInteractor documentsInteractor;
   @Inject AnalyticsInteractor analyticsInteractor;

   @Override
   public void takeView(View view) {
      super.takeView(view);

      observeDocumentsChanges();

      refreshDocuments();
      listenViewVisibilityChanges();
   }

   private void listenViewVisibilityChanges() {
      view.visibilityStream()
            .debounce(DEBOUNCE_VISIBILITY_CHANGE, TimeUnit.MILLISECONDS)
            .compose(bindViewToMainComposer())
            .filter(Boolean::booleanValue)
            .filter(value -> view.getUserVisibleHint())
            .subscribe(o -> analyticsInteractor.analyticsActionPipe().send(new ViewDocumentsTabAnalyticAction()));
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

      Observable<Boolean> visibilityStream();

      boolean getUserVisibleHint();
   }
}
