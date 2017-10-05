package com.worldventures.dreamtrips.social.ui.tripsimages.presenter;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.core.modules.picker.model.MediaPickerAttachment;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.rx.composer.IoToMainComposer;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.config.service.AppConfigurationInteractor;
import com.worldventures.dreamtrips.modules.config.service.command.ConfigurationCommand;
import com.worldventures.dreamtrips.modules.trips.service.analytics.UploadTripImageAnalyticAction;
import com.worldventures.dreamtrips.social.ui.background_uploading.model.PostCompoundOperationModel;
import com.worldventures.dreamtrips.social.ui.background_uploading.service.CompoundOperationsInteractor;
import com.worldventures.dreamtrips.social.ui.background_uploading.service.command.CompoundOperationsCommand;
import com.worldventures.dreamtrips.social.ui.feed.model.FeedEntity;
import com.worldventures.dreamtrips.social.ui.feed.model.TextualPost;
import com.worldventures.dreamtrips.social.ui.feed.model.uploading.UploadingPostsList;
import com.worldventures.dreamtrips.social.ui.feed.model.video.Video;
import com.worldventures.dreamtrips.social.ui.feed.presenter.FeedEntityHolder;
import com.worldventures.dreamtrips.social.ui.feed.presenter.UploadingListenerPresenter;
import com.worldventures.dreamtrips.social.ui.feed.presenter.delegate.FeedEntityHolderDelegate;
import com.worldventures.dreamtrips.social.ui.feed.presenter.delegate.UploadingPresenterDelegate;
import com.worldventures.dreamtrips.social.ui.feed.service.PostsInteractor;
import com.worldventures.dreamtrips.social.ui.feed.service.command.PostCreatedCommand;
import com.worldventures.dreamtrips.social.ui.tripsimages.model.BaseMediaEntity;
import com.worldventures.dreamtrips.social.ui.tripsimages.model.Photo;
import com.worldventures.dreamtrips.social.ui.tripsimages.model.PhotoMediaEntity;
import com.worldventures.dreamtrips.social.ui.tripsimages.model.VideoMediaEntity;
import com.worldventures.dreamtrips.social.ui.tripsimages.service.TripImageArgsFilterFunc;
import com.worldventures.dreamtrips.social.ui.tripsimages.service.TripImagesInteractor;
import com.worldventures.dreamtrips.social.ui.tripsimages.service.command.BaseMediaCommand;
import com.worldventures.dreamtrips.social.ui.tripsimages.service.command.CheckVideoProcessingStatusCommand;
import com.worldventures.dreamtrips.social.ui.tripsimages.service.command.MemberImagesAddedCommand;
import com.worldventures.dreamtrips.social.ui.tripsimages.service.command.TripImagesCommandFactory;
import com.worldventures.dreamtrips.social.ui.tripsimages.view.args.TripImagesArgs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import icepick.State;
import io.techery.janet.helper.ActionStateSubscriber;
import rx.Observable;

public class TripImagesPresenter extends Presenter<TripImagesPresenter.View> implements UploadingListenerPresenter, FeedEntityHolder {

   private static final int VISIBLE_THRESHOLD = 15;

   @Inject TripImagesInteractor tripImagesInteractor;
   @Inject PostsInteractor postsInteractor;
   @Inject CompoundOperationsInteractor compoundOperationsInteractor;
   @Inject UploadingPresenterDelegate uploadingPresenterDelegate;
   @Inject TripImagesCommandFactory tripImagesCommandFactory;
   @Inject FeedEntityHolderDelegate feedEntityHolderDelegate;
   @Inject AppConfigurationInteractor appConfigurationInteractor;

   boolean memberImagesAreRefreshing;
   int previousScrolledTotal = 0;
   boolean loading = true;
   boolean lastPageReached = false;

   TripImagesArgs tripImagesArgs;

   @State ArrayList<BaseMediaEntity> currentItems;
   List<PostCompoundOperationModel> compoundOperationModels;

   public TripImagesPresenter(TripImagesArgs tripImagesArgs) {
      this.tripImagesArgs = tripImagesArgs;
   }

   @Override
   public void onViewTaken() {
      super.onViewTaken();
      initItems();
      updateItemsInView();
      initCreateMediaFlow();
      subscribeToTripImages();
      subscribeToPhotoDeletedEvents();
      subscribeToErrorUpdates();
      subscribeToNewItems();
      refreshImages();
      feedEntityHolderDelegate.subscribeToUpdates(this, bindViewToMainComposer(), this::handleError);
   }

   public void reload() {
      view.hideNewImagesButton();
      refreshImages();
   }

   public void onItemClick(BaseMediaEntity entity) {
      view.openFullscreen(lastPageReached, currentItems.indexOf(entity));
   }

   public void scrolled(int visibleItemCount, int totalItemCount, int firstVisibleItem) {
      if (totalItemCount > previousScrolledTotal) {
         loading = false;
         previousScrolledTotal = totalItemCount;
      }
      if (!lastPageReached && !loading && currentItems.size() > 0
            && (totalItemCount - visibleItemCount) <= (firstVisibleItem + VISIBLE_THRESHOLD)) {
         loadNext();
      }
   }

   public void addPhotoClicked() {
      appConfigurationInteractor.configurationCommandActionPipe()
            .createObservableResult(new ConfigurationCommand())
            .compose(new IoToMainComposer<>())
            .map(configurationCommand -> configurationCommand.getResult()
                  .getVideoRequirement()
                  .getVideoMaxLength())
            .subscribe(length -> {
               view.openPicker(length);
               trackUploadAnalyticEvent();
            });
   }

   private void trackUploadAnalyticEvent() {
      UploadTripImageAnalyticAction action = tripImagesArgs.getRoute() == Route.ACCOUNT_IMAGES ?
            UploadTripImageAnalyticAction.fromMyImages() : UploadTripImageAnalyticAction.fromMemberImages();

      analyticsInteractor.analyticsActionPipe().send(action);
   }

   public void pickedAttachments(MediaPickerAttachment mediaAttachment) {
      view.openCreatePhoto(mediaAttachment);
   }

   void initItems() {
      if (currentItems == null) currentItems = new ArrayList<>();
   }

   void initCreateMediaFlow() {
      if (tripImagesArgs.getRoute() != Route.MEMBERS_IMAGES && tripImagesArgs.getUserId() != getAccount().getId()) {
         view.hideCreateImageButton();
      } else {
         subscribeToBackgroundUploadingOperations();
      }
   }

   void refreshImages() {
      tripImagesInteractor.baseTripImagesCommandActionPipe()
            .send(tripImagesCommandFactory.provideCommand(tripImagesArgs));
   }

   void loadNext() {
      loading = true;
      tripImagesInteractor.baseTripImagesCommandActionPipe()
            .send(tripImagesCommandFactory.provideLoadMoreCommand(tripImagesArgs, currentItems));
   }

   void subscribeToTripImages() {
      tripImagesInteractor.baseTripImagesCommandActionPipe()
            .observe()
            .filter(new TripImageArgsFilterFunc(tripImagesArgs))
            .compose(bindViewToMainComposer())
            .subscribe(new ActionStateSubscriber<BaseMediaCommand>()
                  .onStart(command -> {
                     loading = true;
                     view.showLoading();
                  })
                  .onSuccess(this::itemsUpdated)
                  .onFail((baseTripImagesCommand, throwable) -> {
                     loading = false;
                     view.finishLoading();
                     handleError(baseTripImagesCommand, throwable);
                  })
            );
   }

   void itemsUpdated(BaseMediaCommand baseMediaCommand) {
      loading = false;
      lastPageReached = baseMediaCommand.lastPageReached();
      view.finishLoading();
      if (baseMediaCommand.isReload()) currentItems.clear();
      currentItems.addAll(baseMediaCommand.getItems());
      updateItemsInView();
      tripImagesInteractor.checkVideoProcessingStatusPipe().send(new CheckVideoProcessingStatusCommand(currentItems));
   }

   void subscribeToPhotoDeletedEvents() {
      tripImagesInteractor.deletePhotoPipe()
            .observeSuccess()
            .compose(bindViewToMainComposer())
            .subscribe(deletePhotoCommand -> {
               for (int i = 0; i < currentItems.size(); i++) {
                  BaseMediaEntity mediaEntity = currentItems.get(i);
                  if (mediaEntity.getItem().getUid().equals(deletePhotoCommand.getResult().getUid())) {
                     currentItems.remove(i);
                  }
                  updateItemsInView();
               }
            });
   }

   void subscribeToErrorUpdates() {
      offlineErrorInteractor.offlineErrorCommandPipe()
            .observeSuccess()
            .compose(bindViewToMainComposer())
            .subscribe(command -> reportNoConnection());
   }

   void subscribeToNewItems() {
      postsInteractor.postCreatedPipe()
            .observeSuccess()
            .compose(bindViewToMainComposer())
            .map(PostCreatedCommand::getResult)
            .subscribe(this::onFeedItemAdded);
   }

   void onFeedItemAdded(TextualPost textualPost) {
      if (!textualPost.getAttachments().isEmpty()) {
         List<BaseMediaEntity> mediaEntities = Queryable.from(textualPost.getAttachments())
               .map(this::fromFeedEntityHolder)
               .filter(item -> item != null)
               .filter(mediaEntity -> !currentItems.contains(mediaEntity))
               .toList();
         boolean allPhotosHavePublishAt = Queryable.from(mediaEntities)
               .count(element -> element.getItem().getCreatedAt() == null) == 0;
         if (allPhotosHavePublishAt) {
            Collections.sort(mediaEntities, (p1, p2) -> p1.getItem()
                  .getCreatedAt()
                  .before(p2.getItem().getCreatedAt()) ? 1 : -1);
         } else {
            Collections.reverse(mediaEntities);
         }

         tripImagesInteractor.memberImagesAddedCommandPipe()
               .send(new MemberImagesAddedCommand(tripImagesArgs, mediaEntities));
         currentItems.addAll(0, mediaEntities);
         updateItemsInView();
         view.scrollToTop();
      }
   }

   void updateItemsInView() {
      List items = new ArrayList();
      if (compoundOperationModels != null && !compoundOperationModels.isEmpty()) {
         items.add(new UploadingPostsList(compoundOperationModels));
      }
      items.addAll(currentItems);
      view.updateItems(items);
   }

   private BaseMediaEntity fromFeedEntityHolder(com.worldventures.dreamtrips.social.ui.feed.model.FeedEntityHolder feedEntityHolder) {
      switch (feedEntityHolder.getType()) {
         case PHOTO:
            Photo photo = (Photo) feedEntityHolder.getItem();
            PhotoMediaEntity mediaEntity = new PhotoMediaEntity();
            photo.setOwner(getAccount());
            mediaEntity.setItem(photo);
            return mediaEntity;
         case VIDEO:
            Video video = (Video) feedEntityHolder.getItem();
            VideoMediaEntity videoMediaEntity = new VideoMediaEntity();
            video.setOwner(getAccount());
            videoMediaEntity.setItem(video);
            return videoMediaEntity;
         default:
            return null;
      }
   }

   ///////////////////////////////////////////////////////////////////////////
   // Uploading handling
   ///////////////////////////////////////////////////////////////////////////

   void subscribeToBackgroundUploadingOperations() {
      compoundOperationsInteractor.compoundOperationsPipe()
            .observeWithReplay()
            .compose(bindViewToMainComposer())
            .subscribe(new ActionStateSubscriber<CompoundOperationsCommand>()
                  .onSuccess(compoundOperationsCommand -> {
                     compoundOperationModels = Queryable.from(compoundOperationsCommand.getResult())
                           .cast(PostCompoundOperationModel.class).toList();
                     updateItemsInView();
                  }));
   }

   @Override
   public void onUploadResume(PostCompoundOperationModel compoundOperationModel) {
      uploadingPresenterDelegate.onUploadResume(compoundOperationModel);
   }

   @Override
   public void onUploadPaused(PostCompoundOperationModel compoundOperationModel) {
      uploadingPresenterDelegate.onUploadPaused(compoundOperationModel);
   }

   @Override
   public void onUploadRetry(PostCompoundOperationModel compoundOperationModel) {
      uploadingPresenterDelegate.onUploadRetry(compoundOperationModel);
   }

   @Override
   public void onUploadCancel(PostCompoundOperationModel compoundOperationModel) {
      uploadingPresenterDelegate.onUploadCancel(compoundOperationModel);
   }

   @Override
   public void updateFeedEntity(FeedEntity updatedFeedEntity) {
      Observable.from(currentItems)
            .filter(mediaEntity -> mediaEntity.getItem().getUid().equals(updatedFeedEntity.getUid()))
            .doOnNext(mediaEntity -> mediaEntity.setItem(updatedFeedEntity))
            .compose(bindViewToMainComposer())
            .subscribe();
   }

   @Override
   public void deleteFeedEntity(FeedEntity deletedFeedEntity) {
      currentItems = (ArrayList<BaseMediaEntity>) Queryable.from(currentItems)
            .filter(mediaEntity -> !mediaEntity.getItem().getUid().equals(deletedFeedEntity.getUid()))
            .toList();
      updateItemsInView();
   }

   public interface View extends Presenter.View {
      void scrollToTop();

      void openFullscreen(boolean lastPageReached, int index);

      void updateItems(List items);

      void showLoading();

      void finishLoading();

      void openPicker(int durationLimit);

      void showNewImagesButton(String newImagesCountString);

      void hideNewImagesButton();

      void hideCreateImageButton();

      void openCreatePhoto(MediaPickerAttachment mediaAttachment);
   }
}
