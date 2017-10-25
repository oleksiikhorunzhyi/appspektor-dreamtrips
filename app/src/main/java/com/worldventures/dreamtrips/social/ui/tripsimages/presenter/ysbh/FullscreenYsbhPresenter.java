package com.worldventures.dreamtrips.social.ui.tripsimages.presenter.ysbh;

import com.worldventures.core.model.ShareType;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.social.ui.tripsimages.model.YSBHPhoto;
import com.worldventures.dreamtrips.social.ui.tripsimages.service.TripImagesInteractor;
import com.worldventures.dreamtrips.social.ui.tripsimages.service.command.DownloadImageCommand;

import java.io.IOException;

import javax.inject.Inject;

import io.techery.janet.helper.ActionStateSubscriber;

public class FullscreenYsbhPresenter extends Presenter<FullscreenYsbhPresenter.View> {

   @Inject TripImagesInteractor tripImagesInteractor;

   private YSBHPhoto ysbhPhoto;

   public FullscreenYsbhPresenter(YSBHPhoto ysbhPhoto) {
      this.ysbhPhoto = ysbhPhoto;
   }

   @Override
   public void onViewTaken() {
      super.onViewTaken();
      view.setPhoto(ysbhPhoto);
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
               .createObservable(new DownloadImageCommand(ysbhPhoto.getUrl()))
               .compose(bindViewToMainComposer())
               .subscribe(new ActionStateSubscriber<DownloadImageCommand>()
                     .onFail(this::handleError));
      } else {
         view.openShare(ysbhPhoto.getUrl(), ysbhPhoto.getTitle(), type);
      }
   }

   public interface View extends Presenter.View {
      void setPhoto(YSBHPhoto photo);

      void openShare(String url, String shareText, @ShareType String type);

      void onShowShareOptions();
   }
}
