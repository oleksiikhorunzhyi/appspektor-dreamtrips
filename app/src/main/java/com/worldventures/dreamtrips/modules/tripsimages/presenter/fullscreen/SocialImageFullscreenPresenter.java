package com.worldventures.dreamtrips.modules.tripsimages.presenter.fullscreen;

import android.support.v4.app.FragmentManager;
import android.text.TextUtils;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.navigation.router.Router;
import com.worldventures.dreamtrips.core.navigation.wrapper.NavigationWrapperFactory;
import com.worldventures.dreamtrips.core.utils.LocaleHelper;
import com.worldventures.dreamtrips.modules.common.model.FlagData;
import com.worldventures.dreamtrips.modules.common.presenter.delegate.FlagDelegate;
import com.worldventures.dreamtrips.modules.common.view.custom.tagview.viewgroup.newio.model.PhotoTag;
import com.worldventures.dreamtrips.modules.feed.bundle.CommentsBundle;
import com.worldventures.dreamtrips.modules.feed.event.FeedEntityChangedEvent;
import com.worldventures.dreamtrips.modules.feed.event.FeedEntityCommentedEvent;
import com.worldventures.dreamtrips.modules.feed.model.FeedEntity;
import com.worldventures.dreamtrips.modules.feed.model.FeedEntityHolder;
import com.worldventures.dreamtrips.modules.feed.service.FeedInteractor;
import com.worldventures.dreamtrips.modules.feed.service.TranslationFeedInteractor;
import com.worldventures.dreamtrips.modules.feed.service.command.ChangeFeedEntityLikedStatusCommand;
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
import com.worldventures.dreamtrips.modules.tripsimages.service.command.TranslatePhotoCommand;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.techery.janet.helper.ActionStateSubscriber;

public class SocialImageFullscreenPresenter extends SocialFullScreenPresenter<Photo, SocialImageFullscreenPresenter.View> {

   @Inject Router router;
   @Inject FragmentManager fm;
   @Inject FlagsInteractor flagsInteractor;
   @Inject TripImagesInteractor tripImagesInteractor;
   @Inject FeedInteractor feedInteractor;
   @Inject TranslationFeedInteractor translationFeedInteractor;

   private FlagDelegate flagDelegate;

   public SocialImageFullscreenPresenter(Photo photo, TripImagesType type) {
      super(photo, type);
   }

   @Override
   public void takeView(View view) {
      super.takeView(view);
      setupTranslationState();
      subscribeToTranlsation();
      subscribeToLikesChanges();
      loadEntity();
   }

   @Override
   public void onInjected() {
      super.onInjected();
      flagDelegate = new FlagDelegate(flagsInteractor);
   }

   private void setupTranslationState() {
      boolean ownPost = photo.getOwner() != null &&
            photo.getOwner().getId() == appSessionHolder.get().get().getUser().getId();
      boolean emptyPostText = TextUtils.isEmpty(photo.getFSDescription());
      boolean ownLanguage = LocaleHelper.isOwnLanguage(appSessionHolder, photo.getLanguage());
      boolean emptyPostLanguage = TextUtils.isEmpty(photo.getLanguage());
      if (ownPost || emptyPostText || ownLanguage || emptyPostLanguage) {
         view.hideTranslationButton();
      } else {
         view.showTranslationButton();
      }
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
                        photo.setPhotoTagsCount(photoFeedEntity.getPhotoTagsCount());
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
            .createObservable(new DeletePhotoCommand(photo.getUid()))
            .compose(bindViewToMainComposer())
            .subscribe(new ActionStateSubscriber<DeletePhotoCommand>()
                  .onSuccess(deletePhotoCommand -> view.informUser(context.getString(R.string.photo_deleted)))
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
      feedInteractor.changeFeedEntityLikedStatusPipe()
            .send(new ChangeFeedEntityLikedStatusCommand(photo));
   }

   private void subscribeToLikesChanges() {
      feedInteractor.changeFeedEntityLikedStatusPipe()
            .observe()
            .compose(bindViewToMainComposer())
            .subscribe(new ActionStateSubscriber<ChangeFeedEntityLikedStatusCommand>()
                  .onSuccess(this::itemLiked)
                  .onFail(this::handleError));
   }

   private void itemLiked(ChangeFeedEntityLikedStatusCommand command) {
      photo.syncLikeState(command.getResult());
      view.setContent(photo);

      if (command.getResult().isLiked()) {
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

   public void onTranslateClicked() {
      translationFeedInteractor.translatePhotoPipe().send(new TranslatePhotoCommand(photo));
   }

   private void subscribeToTranlsation() {
      translationFeedInteractor.translatePhotoPipe()
            .observeWithReplay()
            .filter(commandState -> commandState.action.getPhoto().equals(photo))
            .compose(bindViewToMainComposer())
            .subscribe(new ActionStateSubscriber<TranslatePhotoCommand>()
                  .onStart(command -> view.showTranslationInProgress())
                  .onSuccess(command -> view.showTranslation(command.getResult(), command.getPhoto().getLanguage()))
                  .onFail(this::photoTranslationError));
   }

   private void photoTranslationError(TranslatePhotoCommand action, Throwable e) {
      handleError(action, e);
      view.showTranslationButton();
   }

   public interface View extends SocialFullScreenPresenter.View, FlagDelegate.View {
      void showTranslationButton();

      void hideTranslationButton();

      void showTranslation(String translation, String languageFrom);

      void showTranslationInProgress();

      void showProgress();

      void hideProgress();

      void showContentWrapper();

      void openEdit(EditPhotoBundle bundle);
   }
}
