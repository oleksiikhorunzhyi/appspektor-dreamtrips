package com.worldventures.dreamtrips.modules.tripsimages.presenter.inspire_me;

import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.common.model.ShareType;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.tripsimages.model.Inspiration;
import com.worldventures.dreamtrips.modules.tripsimages.service.TripImagesInteractor;
import com.worldventures.dreamtrips.modules.tripsimages.service.command.DownloadImageCommand;

import java.io.IOException;

import javax.inject.Inject;

import io.techery.janet.helper.ActionStateSubscriber;

public class FullscreenInspireMePresenter extends Presenter<FullscreenInspireMePresenter.View> {

   @Inject TripImagesInteractor tripImagesInteractor;

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
         tripImagesInteractor.downloadImageActionPipe()
               .createObservable(new DownloadImageCommand(inspiration.getUrl()))
               .compose(bindViewToMainComposer())
               .subscribe(new ActionStateSubscriber<DownloadImageCommand>()
                     .onFail(this::handleError));
      } else {
         view.openShare(inspiration.getUrl(), inspiration.getQuote() + " - " + inspiration.getAuthor(), type);
      }
      TrackingHelper.insprShare(inspiration.getId(), type);
   }

   public interface View extends Presenter.View {
      void setPhoto(Inspiration photo);

      void openShare(String url, String shareText, @ShareType String type);

      void onShowShareOptions();
   }
}
