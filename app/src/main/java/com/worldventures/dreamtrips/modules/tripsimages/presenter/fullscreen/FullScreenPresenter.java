package com.worldventures.dreamtrips.modules.tripsimages.presenter.fullscreen;

import com.worldventures.dreamtrips.core.flow.util.Utils;
import com.worldventures.dreamtrips.core.rx.RxView;
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.common.model.ShareType;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.feed.view.cell.Flaggable;
import com.worldventures.dreamtrips.modules.profile.bundle.UserBundle;
import com.worldventures.dreamtrips.modules.tripsimages.model.IFullScreenObject;
import com.worldventures.dreamtrips.modules.tripsimages.model.Inspiration;
import com.worldventures.dreamtrips.modules.tripsimages.model.TripImagesType;
import com.worldventures.dreamtrips.modules.tripsimages.service.TripImagesInteractor;
import com.worldventures.dreamtrips.modules.tripsimages.service.analytics.TripImageShareAnalyticsEvent;
import com.worldventures.dreamtrips.modules.tripsimages.service.command.DownloadImageCommand;

import java.io.IOException;

import javax.inject.Inject;

import io.techery.janet.helper.ActionStateSubscriber;

public abstract class FullScreenPresenter<T extends IFullScreenObject, PRESENTER_VIEW extends FullScreenPresenter.View> extends Presenter<PRESENTER_VIEW> {

   protected TripImagesType type;
   protected T photo;
   @Inject TripImagesInteractor tripImagesInteractor;


   public FullScreenPresenter(T photo, TripImagesType type) {
      this.photo = photo;
      this.type = type;
   }

   @Override
   public void takeView(PRESENTER_VIEW view) {
      super.takeView(view);
      setupActualViewState();
      TrackingHelper.view(type, String.valueOf(photo.getFSId()), getAccountUserId());
   }

   public void onEdit() { }

   public void onLikeAction() { }

   public void onFlagAction(Flaggable flaggable) { }

   public void onCommentsAction() { }

   public void onLikesAction() { }

   public void onUserClicked() {
      User user = photo.getUser();
      if (user != null) view.openUser(new UserBundle(user));
   }

   public final void setupActualViewState() {
      view.setContent(photo);
   }

   public void sendFlagAction(int flagReasonId, String reason) { }

   public void onDeleteAction() { }

   public void onShareAction() {
      if (!isConnected()) {
         reportNoConnectionWithOfflineErrorPipe();
         return;
      }

      analyticsInteractor.analyticsActionPipe().send(new TripImageShareAnalyticsEvent(photo.getFSId()));
      view.onShowShareOptions();
   }

   public void onShareOptionChosen(@ShareType String type) {
      if (!isConnected()) {
         reportNoConnection();
         return;
      }
      if (type.equals(ShareType.EXTERNAL_STORAGE)) {
         if (view.isVisibleOnScreen()) {
            tripImagesInteractor.downloadImageActionPipe()
                  .createObservable(new DownloadImageCommand(photo.getFSImage().getUrl()))
                  .compose(bindViewToMainComposer())
                  .subscribe(new ActionStateSubscriber<DownloadImageCommand>()
                        .onFail(this::handleError));
         }
      } else {
         view.openShare(photo.getFSImage().getUrl(), photo.getFSShareText(), type);
      }
      if (photo instanceof Inspiration) {
         TrackingHelper.insprShare(photo.getFSId(), type);
      }
   }

   public void onCouldNotLoadImage(Throwable e) {
      // Avoid showing offline error when there is connection.
      // This can happen if server is not responding for instance.
      if (Utils.isConnected(context) && e instanceof IOException) {
         e = new Exception("Could not load image");
      }
      handleError(e);
   }

   public interface View extends RxView {

      void openUser(UserBundle bundle);

      void onShowShareOptions();

      void openShare(String imageUrl, String text, @ShareType String type);

      <T extends IFullScreenObject> void setContent(T photo);
   }
}
