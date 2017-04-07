package com.worldventures.dreamtrips.modules.infopages.presenter;

import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsInteractor;
import com.worldventures.dreamtrips.modules.infopages.model.Document;
import com.worldventures.dreamtrips.modules.infopages.service.analytics.ViewDocumentAnalyticAction;

import javax.inject.Inject;

public class DocumentPresenter extends WebViewFragmentPresenter<DocumentPresenter.View> {

   @Inject AnalyticsInteractor analyticsInteractor;

   private String analyticsAction;

   public DocumentPresenter(Document document, String analyticsAction) {
      super(document.getUrl());
      this.analyticsAction = analyticsAction;
   }

   @Override
   public void pageLoaded(String url) {
      super.pageLoaded(url);
      analyticsInteractor.analyticsActionPipe().send(new ViewDocumentAnalyticAction(analyticsAction,
            getAccountUserId()));
   }

   public interface View extends WebViewFragmentPresenter.View {

   }

}
