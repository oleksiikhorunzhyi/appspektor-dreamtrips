package com.worldventures.dreamtrips.modules.tripsimages.presenter;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.rx.composer.NonNullFilter;
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.background_uploading.model.PostCompoundOperationModel;
import com.worldventures.dreamtrips.modules.background_uploading.service.CompoundOperationsInteractor;
import com.worldventures.dreamtrips.modules.background_uploading.service.command.CompoundOperationsCommand;
import com.worldventures.dreamtrips.modules.common.model.MediaPickerAttachment;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.feed.model.FeedEntity;
import com.worldventures.dreamtrips.modules.feed.model.FeedItem;
import com.worldventures.dreamtrips.modules.feed.model.TextualPost;
import com.worldventures.dreamtrips.modules.feed.model.uploading.UploadingPostsList;
import com.worldventures.dreamtrips.modules.feed.model.video.Video;
import com.worldventures.dreamtrips.modules.feed.presenter.FeedEntityHolder;
import com.worldventures.dreamtrips.modules.feed.presenter.UploadingListenerPresenter;
import com.worldventures.dreamtrips.modules.feed.presenter.delegate.FeedEntityHolderDelegate;
import com.worldventures.dreamtrips.modules.feed.presenter.delegate.UploadingPresenterDelegate;
import com.worldventures.dreamtrips.modules.feed.service.PostsInteractor;
import com.worldventures.dreamtrips.modules.feed.service.command.PostCreatedCommand;
import com.worldventures.dreamtrips.modules.tripsimages.model.BaseMediaEntity;
import com.worldventures.dreamtrips.modules.tripsimages.model.Photo;
import com.worldventures.dreamtrips.modules.tripsimages.model.PhotoMediaEntity;
import com.worldventures.dreamtrips.modules.tripsimages.model.VideoMediaEntity;
import com.worldventures.dreamtrips.modules.tripsimages.service.TripImageArgsFilterFunc;
import com.worldventures.dreamtrips.modules.tripsimages.service.TripImagesInteractor;
import com.worldventures.dreamtrips.modules.tripsimages.service.command.BaseMediaCommand;
import com.worldventures.dreamtrips.modules.tripsimages.service.command.TripImagesCommandFactory;
import com.worldventures.dreamtrips.modules.tripsimages.view.args.TripImagesArgs;

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
      if (currentItems == null) currentItems = new ArrayList<>();
      updateItemsInView();
      if (shouldHideCreateFlow()) {
         view.hideCreateImageButton();
      } else {
         subscribeToBackgroundUploadingOperations();
      }
      subscribeToTripImages();
      subscribeToPhotoDeletedEvents();
      subscribeToErrorUpdates();
      subscribeToNewItems();
      refreshImages();
      feedEntityHolderDelegate.subscribeToUpdates(this, bindViewToMainComposer(), this::handleError);
   }

   private boolean shouldHideCreateFlow() {
      return tripImagesArgs.getRoute() != Route.MEMBERS_IMAGES &&
            tripImagesArgs.getUserId() != appSessionHolder.get().get().getUser().getId();
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
      view.openPicker();
      if (tripImagesArgs.getRoute() == Route.ACCOUNT_IMAGES) {
         TrackingHelper.uploadTripImagePhoto(TrackingHelper.ACTION_MY_IMAGES);
      } else {
         TrackingHelper.uploadTripImagePhoto(TrackingHelper.ACTION_MEMBER_IMAGES);
      }
   }

   void refreshImages() {
      tripImagesInteractor.baseTripImagesCommandActionPipe()
            .send(tripImagesCommandFactory.provideCommand(tripImagesArgs));
   }

   void loadNext() {
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
      currentItems.addAll(baseMediaCommand.getResult());
      updateItemsInView();
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
            .map(PostCreatedCommand::getFeedItem)
            .subscribe(this::onFeedItemAdded);
   }

   void onFeedItemAdded(FeedItem feedItem) {
      if (feedItem.getItem() instanceof Photo || feedItem.getItem() instanceof Video) {
         BaseMediaEntity mediaEntity = fromFeedEntityHolder(feedItem);
         if (!currentItems.contains(mediaEntity)) {
            currentItems.add(0, mediaEntity);
         }
      } else if (feedItem.getItem() instanceof TextualPost && ((TextualPost) feedItem
            .getItem()).getAttachments().size() > 0) {
         List<BaseMediaEntity> mediaEntities = Queryable.from(((TextualPost) feedItem.getItem()).getAttachments())
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

         currentItems.addAll(0, mediaEntities);
      }
      updateItemsInView();
   }

   public void pickedAttachments(MediaPickerAttachment mediaAttachment) {
      if (view.isVisibleOnScreen()) { //cause neighbour tab also catches this event
         view.openCreatePhoto(mediaAttachment);
      }
   }

   private BaseMediaEntity fromFeedEntityHolder(com.worldventures.dreamtrips.modules.feed.model.FeedEntityHolder feedEntityHolder) {
      switch (feedEntityHolder.getType()) {
         case PHOTO:
            Photo photo = (Photo) feedEntityHolder.getItem();
            PhotoMediaEntity mediaEntity = new PhotoMediaEntity();
            mediaEntity.setItem(photo);
            return mediaEntity;
         case VIDEO:
            Video video = (Video) feedEntityHolder.getItem();
            VideoMediaEntity videoMediaEntity = new VideoMediaEntity();
            videoMediaEntity.setItem(video);
            return videoMediaEntity;
         default:
            return null;
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
            .filter(mediaEntity -> mediaEntity.getItem().getUid().equals(deletedFeedEntity.getUid()))
            .toList();
      updateItemsInView();
   }

   public interface View extends Presenter.View {
      void openFullscreen(boolean lastPageReached, int index);

      void updateItems(List items);

      void showLoading();

      void finishLoading();

      void openPicker();

      void showNewImagesButton(String newImagesCountString);

      void hideNewImagesButton();

      void hideCreateImageButton();

      void openCreatePhoto(MediaPickerAttachment mediaAttachment);
   }
}
