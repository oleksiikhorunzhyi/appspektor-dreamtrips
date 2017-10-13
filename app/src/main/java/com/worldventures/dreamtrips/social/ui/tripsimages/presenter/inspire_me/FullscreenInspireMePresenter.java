package com.worldventures.dreamtrips.social.ui.tripsimages.presenter.inspire_me;

import com.worldventures.core.model.ShareType;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.trips.service.analytics.ShareInspirationImageAnalyticAction;
import com.worldventures.dreamtrips.social.ui.tripsimages.model.Inspiration;
import com.worldventures.dreamtrips.social.ui.tripsimages.service.delegate.DownloadImageDelegate;

import java.io.IOException;

import javax.inject.Inject;

public class FullscreenInspireMePresenter extends Presenter<FullscreenInspireMePresenter.View> {

   @Inject DownloadImageDelegate downloadImageDelegate;

   private Inspiration inspiration;

   public FullscreenInspireMePresenter(Inspiration inspiration) {
      this.inspiration = inspiration;
   }

   @Override
   public void onViewTaken() {
      super.onViewTaken();
      view.setPhoto(inspiration);
   }

   public void onShareAction() {
      if (!isConnected()) {
         reportNoConnectionWithOfflineErrorPipe(new IOException());
         return;
      }

      view.onShowShareOptions();
   }

   public void onShareOptionChosen(@ShareType String type) {
      if (type.equals(ShareType.EXTERNAL_STORAGE)) {
         downloadImageDelegate.downloadImage(inspiration.getUrl(), bindView(), this::handleError);
      } else {
         view.openShare(inspiration.getUrl(), inspiration.getQuote() + " - " + inspiration.getAuthor(), type);
      }
      analyticsInteractor.analyticsActionPipe().send(new ShareInspirationImageAnalyticAction());
   }

   public interface View extends Presenter.View {
      void setPhoto(Inspiration photo);

      void openShare(String url, String shareText, @ShareType String type);

      void onShowShareOptions();
   }
}
