package com.worldventures.dreamtrips.modules.tripsimages.presenter.fullscreen;

import android.support.v4.app.FragmentManager;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.navigation.router.Router;
import com.worldventures.dreamtrips.core.navigation.wrapper.NavigationWrapperFactory;
import com.worldventures.dreamtrips.core.utils.events.EntityLikedEvent;
import com.worldventures.dreamtrips.core.utils.events.PhotoDeletedEvent;
import com.worldventures.dreamtrips.modules.common.model.FlagData;
import com.worldventures.dreamtrips.modules.common.presenter.delegate.FlagDelegate;
import com.worldventures.dreamtrips.modules.common.view.custom.tagview.viewgroup.newio.model.PhotoTag;
import com.worldventures.dreamtrips.modules.feed.bundle.CommentsBundle;
import com.worldventures.dreamtrips.modules.feed.event.FeedEntityChangedEvent;
import com.worldventures.dreamtrips.modules.feed.event.FeedEntityCommentedEvent;
import com.worldventures.dreamtrips.modules.feed.event.FeedEntityDeletedEvent;
import com.worldventures.dreamtrips.modules.feed.manager.FeedEntityManager;
import com.worldventures.dreamtrips.modules.feed.model.FeedEntity;
import com.worldventures.dreamtrips.modules.feed.model.FeedEntityHolder;
import com.worldventures.dreamtrips.modules.feed.service.FeedInteractor;
import com.worldventures.dreamtrips.modules.feed.service.command.GetFeedEntityCommand;
import com.worldventures.dreamtrips.modules.feed.view.cell.Flaggable;
import com.worldventures.dreamtrips.modules.flags.service.FlagsInteractor;
import com.worldventures.dreamtrips.modules.tripsimages.bundle.EditPhotoBundle;
import com.worldventures.dreamtrips.modules.tripsimages.model.Photo;
import com.worldventures.dreamtrips.modules.tripsimages.model.TripImagesType;
import com.worldventures.dreamtrips.modules.tripsimages.service.TripImagesInteractor;
import com.worldventures.dreamtrips.modules.tripsimages.service.analytics.TripImageDeleteAnalyticsEvent;
import com.worldventures.dreamtrips.modules.tripsimages.service.analytics.TripImageEditAnalyticsEvent;
import com.worldventures.dreamtrips.modules.tripsimages.service.analytics.TripImageFlagAnalyticsEvent;
import com.worldventures.dreamtrips.modules.tripsimages.service.analytics.TripImageLikedAnalyticsEvent;
import com.worldventures.dreamtrips.modules.tripsimages.service.command.DeletePhotoCommand;
import com.worldventures.dreamtrips.modules.tripsimages.service.command.DeletePhotoTagsCommand;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.techery.janet.helper.ActionStateSubscriber;

public class SocialImageFullscreenPresenter extends SocialFullScreenPresenter<Photo, SocialImageFullscreenPresenter.View> {

   @Inject FeedEntityManager entityManager;
   @Inject Router router;
   @Inject FragmentManager fm;
   @Inject FlagsInteractor flagsInteractor;
   @Inject TripImagesInteractor tripImagesInteractor;
   @Inject FeedInteractor feedInteractor;

   private FlagDelegate flagDelegate;

   public SocialImageFullscreenPresenter(Photo photo, TripImagesType type) {
      super(photo, type);
   }

   @Override
   public void takeView(View view) {
      super.takeView(view);
      apiErrorPresenter.setView(view);
      loadEntity();
   }

   @Override
   public void onInjected() {
      super.onInjected();
      entityManager.setFeedEntityManagerListener(this);
      flagDelegate = new FlagDelegate(flagsInteractor);
   }

   private void loadEntity() {
      feedInteractor.getFeedEntityPipe()
            .createObservable(new GetFeedEntityCommand(photo.getUid(), FeedEntityHolder.Type.PHOTO))
            .compose(bindViewToMainComposer())
            .subscribe(new ActionStateSubscriber<GetFeedEntityCommand>()
                  .onSuccess(getFeedEntityCommand -> {
                     Photo photoFeedEntity = (Photo) getFeedEntityCommand.getResult();
                     if (photo.getUser() != null) {
                        photo.syncLikeState(photoFeedEntity);
                        photo.setCommentsCount(photoFeedEntity.getCommentsCount());
                        photo.setComments(photoFeedEntity.getComments());
                        photo.setPhotoTags(photoFeedEntity.getPhotoTags());
                     } else {
                        photo = photoFeedEntity;
                        view.showContentWrapper();
                     }
                     setupActualViewState();
                  })
                  .onFail(this::handleError));
   }

   @Override
   public void onDeleteAction() {
      tripImagesInteractor.deletePhotoPipe()
            .createObservable(new DeletePhotoCommand(photo.getFSId()))
            .compose(bindViewToMainComposer())
            .subscribe(new ActionStateSubscriber<DeletePhotoCommand>()
                  .onSuccess(deletePhotoCommand -> {
                     view.informUser(context.getString(R.string.photo_deleted));
                     eventBus.postSticky(new PhotoDeletedEvent(photo.getFSId()));
                     eventBus.postSticky(new FeedEntityDeletedEvent(photo));
                  })
                  .onFail(this::handleError));
   }

   public void deleteTag(PhotoTag tag) {
      List<Integer> userIds = new ArrayList<>();
      userIds.add(tag.getUser().getId());

      tripImagesInteractor.deletePhotoTagsPipe()
            .createObservable(new DeletePhotoTagsCommand(photo.getFSId(), userIds))
            .compose(bindViewToMainComposer())
            .subscribe(new ActionStateSubscriber<DeletePhotoTagsCommand>()
                  .onSuccess(deletePhotoTagsCommand -> {
                     photo.getPhotoTags().remove(tag);
                     photo.setPhotoTagsCount(photo.getPhotoTags().size());
                  })
                  .onFail(this::handleError));
   }

   @Override
   public void sendFlagAction(int flagReasonId, String reason) {
      flagDelegate.flagItem(new FlagData(photo.getUid(), flagReasonId, reason), view, this::handleError);
   }

   @Override
   public void onLikeAction() {
      if (!photo.isLiked()) {
         entityManager.like(photo);
      } else {
         entityManager.unlike(photo);
      }
   }

   public void onEvent(EntityLikedEvent event) {
      photo.syncLikeState(event.getFeedEntity());
      view.setContent(photo);

      if (event.getFeedEntity().isLiked()) {
         analyticsInteractor.analyticsActionPipe().send(new TripImageLikedAnalyticsEvent(photo.getFSId()));
      }
   }

   @Override
   public void onCommentsAction() {
      new NavigationWrapperFactory().componentOrDialogNavigationWrapper(router, fm, view)
            .navigate(Route.COMMENTS, new CommentsBundle(photo, false, true));
   }

   @Override
   public void onLikesAction() {
      onCommentsAction();
   }

   @Override
   public void onEdit() {
      if (view != null) {
         analyticsInteractor.analyticsActionPipe().send(new TripImageEditAnalyticsEvent(photo.getFSId()));
         view.openEdit(new EditPhotoBundle(photo));
      }
   }

   @Override
   public void onFlagAction(Flaggable flaggable) {
      analyticsInteractor.analyticsActionPipe().send(new TripImageFlagAnalyticsEvent(photo.getFSId()));
      view.showProgress();
      flagDelegate.loadFlags(flaggable, (command, throwable) -> {
         view.hideProgress();
         handleError(command, throwable);
      });
   }

   public void onDelete() {
      analyticsInteractor.analyticsActionPipe().send(new TripImageDeleteAnalyticsEvent(photo.getFSId()));
   }

   public void onEvent(FeedEntityChangedEvent event) {
      updatePhoto(event.getFeedEntity());
   }

   public void onEvent(FeedEntityCommentedEvent event) {
      updatePhoto(event.getFeedEntity());
   }

   private void updatePhoto(FeedEntity feedEntity) {
      if (feedEntity instanceof Photo) {
         Photo temp = (Photo) feedEntity;
         if (photo.equals(temp)) {
            this.photo = temp;
            setupActualViewState();
         }
      }
   }

   public Photo getPhoto() {
      return photo;
   }

   public interface View extends SocialFullScreenPresenter.View, FlagDelegate.View {

      void showProgress();

      void hideProgress();

      void showContentWrapper();

      void openEdit(EditPhotoBundle bundle);
   }
}
