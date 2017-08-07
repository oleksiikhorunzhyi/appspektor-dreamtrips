package com.worldventures.dreamtrips.modules.tripsimages.presenter;

import android.support.v4.app.FragmentManager;
import android.text.TextUtils;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.navigation.router.Router;
import com.worldventures.dreamtrips.core.navigation.wrapper.NavigationWrapperFactory;
import com.worldventures.dreamtrips.core.utils.LocaleHelper;
import com.worldventures.dreamtrips.modules.common.model.FlagData;
import com.worldventures.dreamtrips.modules.common.model.ShareType;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.common.presenter.delegate.FlagDelegate;
import com.worldventures.dreamtrips.modules.common.view.custom.tagview.viewgroup.newio.model.PhotoTag;
import com.worldventures.dreamtrips.modules.feed.bundle.CommentsBundle;
import com.worldventures.dreamtrips.modules.feed.model.FeedEntity;
import com.worldventures.dreamtrips.modules.feed.presenter.FeedEntityHolder;
import com.worldventures.dreamtrips.modules.feed.presenter.delegate.FeedEntityHolderDelegate;
import com.worldventures.dreamtrips.modules.feed.service.FeedInteractor;
import com.worldventures.dreamtrips.modules.feed.service.TranslationFeedInteractor;
import com.worldventures.dreamtrips.modules.feed.service.command.ChangeFeedEntityLikedStatusCommand;
import com.worldventures.dreamtrips.modules.feed.service.command.GetFeedEntityCommand;
import com.worldventures.dreamtrips.modules.feed.view.cell.Flaggable;
import com.worldventures.dreamtrips.modules.flags.service.FlagsInteractor;
import com.worldventures.dreamtrips.modules.profile.bundle.UserBundle;
import com.worldventures.dreamtrips.modules.tripsimages.model.Photo;
import com.worldventures.dreamtrips.modules.tripsimages.service.TripImagesInteractor;
import com.worldventures.dreamtrips.modules.tripsimages.service.analytics.TripImageDeleteAnalyticsEvent;
import com.worldventures.dreamtrips.modules.tripsimages.service.analytics.TripImageEditAnalyticsEvent;
import com.worldventures.dreamtrips.modules.tripsimages.service.analytics.TripImageFlagAnalyticsEvent;
import com.worldventures.dreamtrips.modules.tripsimages.service.analytics.TripImageShareAnalyticsEvent;
import com.worldventures.dreamtrips.modules.tripsimages.service.command.DeletePhotoCommand;
import com.worldventures.dreamtrips.modules.tripsimages.service.command.DeletePhotoTagsCommand;
import com.worldventures.dreamtrips.modules.tripsimages.service.command.DownloadImageCommand;
import com.worldventures.dreamtrips.modules.tripsimages.service.command.TranslatePhotoCommand;
import com.worldventures.dreamtrips.modules.tripsimages.view.args.EditPhotoBundle;

import java.io.IOException;
import java.util.Collections;

import javax.inject.Inject;

import io.techery.janet.helper.ActionStateSubscriber;

public class FullscreenPhotoPresenter extends Presenter<FullscreenPhotoPresenter.View> implements FeedEntityHolder {

   @Inject Router router;
   @Inject FragmentManager fm;
   @Inject FeedInteractor feedInteractor;
   @Inject FlagsInteractor flagsInteractor;
   @Inject TripImagesInteractor tripImagesInteractor;
   @Inject FeedEntityHolderDelegate feedEntityHolderDelegate;
   @Inject TranslationFeedInteractor translationFeedInteractor;
   private FlagDelegate flagDelegate;

   private Photo photo;

   public FullscreenPhotoPresenter(Photo photo) {
      this.photo = photo;
   }

   @Override
   public void onInjected() {
      super.onInjected();
      flagDelegate = new FlagDelegate(flagsInteractor);
   }

   @Override
   public void onViewTaken() {
      super.onViewTaken();
      setupTranslationState();
      subscribeToTranslation();
      feedEntityHolderDelegate.subscribeToUpdates(this, bindViewToMainComposer(), this::handleError);
      view.setPhoto(photo);
      loadEntity();
   }

   private void setupTranslationState() {
      boolean ownPost = photo.getOwner() != null &&
            photo.getOwner().getId() == appSessionHolder.get().get().getUser().getId();
      boolean emptyPostText = TextUtils.isEmpty(photo.getTitle());
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
            .createObservable(new GetFeedEntityCommand(photo.getUid(), com.worldventures.dreamtrips.modules.feed.model.FeedEntityHolder.Type.PHOTO))
            .compose(bindViewToMainComposer())
            .subscribe(new ActionStateSubscriber<GetFeedEntityCommand>()
                  .onSuccess(getFeedEntityCommand -> updateFeedEntity(getFeedEntityCommand.getResult()))
                  .onFail(this::handleError));
   }

   public void shouldSyncTags() {
      view.syncTagViewGroupWithGlobalState(photo);
   }

   public void onUserClicked() {
      view.openUser(new UserBundle(photo.getOwner()));
   }

   public void onDeleteAction() {
      tripImagesInteractor.deletePhotoPipe().send(new DeletePhotoCommand(photo));
   }

   public void deleteTag(PhotoTag tag) {
      tripImagesInteractor.deletePhotoTagsPipe()
            .createObservable(new DeletePhotoTagsCommand(photo.getUid(), Collections.singletonList(tag.getUser()
                  .getId())))
            .compose(bindViewToMainComposer())
            .subscribe(new ActionStateSubscriber<DeletePhotoTagsCommand>()
                  .onSuccess(deletePhotoTagsCommand -> {
                     photo.getPhotoTags().remove(tag);
                     photo.setPhotoTagsCount(photo.getPhotoTags().size());
                  })
                  .onFail(this::handleError));
   }

   public void sendFlagAction(int flagReasonId, String reason) {
      flagDelegate.flagItem(new FlagData(photo.getUid(), flagReasonId, reason), view, this::handleError);
   }

   public void onCommentsAction() {
      new NavigationWrapperFactory().componentOrDialogNavigationWrapper(router, fm, view)
            .navigate(Route.COMMENTS, new CommentsBundle(photo, false, true));
   }

   public void onLikeAction() {
      feedInteractor.changeFeedEntityLikedStatusPipe().send(new ChangeFeedEntityLikedStatusCommand(photo));
   }

   public void onEdit() {
      if (view != null) {
         analyticsInteractor.analyticsActionPipe().send(new TripImageEditAnalyticsEvent(photo.getUid()));
         view.editPhoto(new EditPhotoBundle(photo));
      }
   }

   public void onFlagAction(Flaggable flaggable) {
      analyticsInteractor.analyticsActionPipe().send(new TripImageFlagAnalyticsEvent(photo.getUid()));
      view.showFlagProgress();
      flagDelegate.loadFlags(flaggable, (command, throwable) -> {
         view.hideFlagProgress();
         handleError(command, throwable);
      });
   }

   public void onDelete() {
      analyticsInteractor.analyticsActionPipe().send(new TripImageDeleteAnalyticsEvent(photo.getUid()));
   }

   public void onTranslateClicked() {
      translationFeedInteractor.translatePhotoPipe().send(new TranslatePhotoCommand(photo));
   }

   private void subscribeToTranslation() {
      translationFeedInteractor.translatePhotoPipe()
            .observe()
            .filter(commandState -> commandState.action.getPhoto().equals(photo))
            .compose(bindViewToMainComposer())
            .subscribe(new ActionStateSubscriber<TranslatePhotoCommand>()
                  .onStart(command -> view.showTranslationInProgress())
                  .onSuccess(command -> view.showTranslation(command.getResult(), command.getPhoto().getLanguage()))
                  .onFail((action, throwable) -> {
                     handleError(action, throwable);
                     view.showTranslationButton();
                  }));
   }

   @Override
   public void updateFeedEntity(FeedEntity updatedFeedEntity) {
      if (!photo.equals(updatedFeedEntity)) return;
      Photo photoFeedEntity = (Photo) updatedFeedEntity;
      if (photo.getOwner() != null) {
         photo.syncLikeState(photoFeedEntity);
         photo.setCommentsCount(photoFeedEntity.getCommentsCount());
         photo.setComments(photoFeedEntity.getComments());
         photo.setPhotoTags(photoFeedEntity.getPhotoTags());
         photo.setPhotoTagsCount(photoFeedEntity.getPhotoTagsCount());
      } else {
         photo = photoFeedEntity;
         view.showContentWrapper();
      }
      view.setPhoto(photo);
      setupTranslationState();
   }

   @Override
   public void deleteFeedEntity(FeedEntity deletedFeedEntity) {
      if (deletedFeedEntity.getUid().equals(photo.getUid())) {
         view.informUser(context.getString(R.string.photo_deleted));
         view.back();
      }
   }

   public void onShareAction() {
      if (!isConnected()) {
         reportNoConnectionWithOfflineErrorPipe(new IOException());
         return;
      }

      analyticsInteractor.analyticsActionPipe().send(new TripImageShareAnalyticsEvent(photo.getUid()));
      view.onShowShareOptions();
   }

   public void onShareOptionChosen(@ShareType String type) {
      if (type.equals(ShareType.EXTERNAL_STORAGE)) {
         if (view.isVisibleOnScreen()) {
            tripImagesInteractor.downloadImageActionPipe()
                  .createObservable(new DownloadImageCommand(photo.getImagePath()))
                  .compose(bindViewToMainComposer())
                  .subscribe(new ActionStateSubscriber<DownloadImageCommand>()
                        .onFail(this::handleError));
         }
      } else {
         view.openShare(photo.getImagePath(), photo.getTitle(), type);
      }
   }

   public interface View extends Presenter.View, FlagDelegate.View {
      void setPhoto(Photo photo);

      void openUser(UserBundle bundle);

      void onShowShareOptions();

      void openShare(String imageUrl, String text, @ShareType String type);

      void syncTagViewGroupWithGlobalState(Photo photo);

      void editPhoto(EditPhotoBundle bundle);

      void showFlagProgress();

      void hideFlagProgress();

      void showContentWrapper();

      void showTranslationButton();

      void hideTranslationButton();

      void showTranslation(String translation, String languageFrom);

      void showTranslationInProgress();

      void back();
   }
}
