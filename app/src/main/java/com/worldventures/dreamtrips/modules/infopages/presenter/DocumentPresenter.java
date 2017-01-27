package com.worldventures.dreamtrips.modules.infopages.presenter;

import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsInteractor;
import com.worldventures.dreamtrips.modules.infopages.service.analytics.ViewDocumentAnalyticAction;

import javax.inject.Inject;

public class DocumentPresenter extends WebViewFragmentPresenter<DocumentPresenter.View> {

   @Inject AnalyticsInteractor analyticsInteractor;

   public DocumentPresenter(String url) {
      super(url);
   }

   public void onWebPageLoaded() {
      String document = url.substring(url.lastIndexOf("/") + 1);
      analyticsInteractor.analyticsActionPipe().send(new ViewDocumentAnalyticAction(document));
   }

   public interface View extends WebViewFragmentPresenter.View {

   }

}
