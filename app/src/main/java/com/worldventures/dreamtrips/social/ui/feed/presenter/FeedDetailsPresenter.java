package com.worldventures.dreamtrips.social.ui.feed.presenter;

import com.worldventures.dreamtrips.modules.trips.command.GetTripDetailsCommand;
import com.worldventures.dreamtrips.modules.trips.service.TripsInteractor;
import com.worldventures.dreamtrips.social.ui.bucketlist.model.BucketItem;
import com.worldventures.dreamtrips.social.ui.bucketlist.service.BucketInteractor;
import com.worldventures.dreamtrips.social.ui.bucketlist.service.action.UpdateBucketItemCommand;
import com.worldventures.dreamtrips.social.ui.feed.model.FeedEntity;
import com.worldventures.dreamtrips.social.ui.feed.model.FeedItem;
import com.worldventures.dreamtrips.social.ui.feed.model.TextualPost;
import com.worldventures.dreamtrips.social.ui.feed.model.TripFeedItem;
import com.worldventures.dreamtrips.social.ui.feed.model.video.Video;
import com.worldventures.dreamtrips.social.ui.feed.presenter.delegate.FeedActionHandlerDelegate;
import com.worldventures.dreamtrips.social.ui.feed.presenter.delegate.FeedEntityHolderDelegate;
import com.worldventures.dreamtrips.social.ui.feed.service.FeedInteractor;
import com.worldventures.dreamtrips.social.ui.feed.service.command.ChangeFeedEntityLikedStatusCommand;
import com.worldventures.dreamtrips.social.ui.feed.service.command.GetFeedEntityCommand;
import com.worldventures.dreamtrips.social.ui.feed.view.fragment.FeedEntityEditingView;
import com.worldventures.dreamtrips.social.ui.tripsimages.model.Photo;
import com.worldventures.dreamtrips.social.ui.tripsimages.service.command.DeleteVideoCommand;

import javax.inject.Inject;

import icepick.State;
import io.techery.janet.helper.ActionStateSubscriber;

public abstract class FeedDetailsPresenter<V extends FeedDetailsPresenter.View> extends BaseCommentPresenter<V>
      implements FeedEditEntityPresenter, FeedEntityHolder {

   @Inject FeedEntityHolderDelegate feedEntityHolderDelegate;
   @Inject FeedActionHandlerDelegate feedActionHandlerDelegate;
   @Inject TripsInteractor tripsInteractor;
   @Inject BucketInteractor bucketInteractor;
   @Inject FeedInteractor feedInteractor;

   @State FeedItem feedItem;

   public FeedDetailsPresenter(FeedItem feedItem) {
      super(feedItem.getItem());
      this.feedItem = feedItem;
   }

   @Override
   public void takeView(V view) {
      super.takeView(view);
      feedActionHandlerDelegate.setFeedEntityEditingView(view);
      feedEntityHolderDelegate.subscribeToUpdates(this, bindViewToMainComposer(), this::handleError);
      view.setFeedItem(feedItem);
      subscribeToLikesChanges();
      subscribeForTripsDetails();
      subscribeToBucketDetailsUpdates();
      loadFullEventInfo();
   }

   //todo until Trip becomes as all normal entities
   public boolean isTrip() {
      return feedItem instanceof TripFeedItem;
   }

   @Override
   protected boolean isNeedCheckCommentsWhenStart() {
      return isTrip();
   }

   private void loadFullEventInfo() {
      //TODO trip details is requested from other place, all this hierarchy should be refactored
      if (!isTrip()) {
         feedInteractor.getFeedEntityPipe()
               .createObservable(new GetFeedEntityCommand(feedEntity.getUid(), feedItem.getType()))
               .compose(bindViewToMainComposer())
               .subscribe(new ActionStateSubscriber<GetFeedEntityCommand>()
                     .onSuccess(getFeedEntityCommand -> updateFullEventInfo(getFeedEntityCommand.getResult()))
                     .onFail(this::handleError));
      }
   }

   protected void updateFullEventInfo(FeedEntity updatedFeedEntity) {
      if (!feedEntity.equals(updatedFeedEntity)) {
         return;
      }
      feedEntity = updatedFeedEntity;
      feedEntity.setComments(null);
      feedItem.setItem(feedEntity);
      checkCommentsAndLikesToLoad();
      refreshFeedItem();
   }

   private void subscribeForTripsDetails() {
      tripsInteractor.detailsPipe()
            .observe()
            .compose(bindViewToMainComposer())
            .subscribe(new ActionStateSubscriber<GetTripDetailsCommand>()
                  .onSuccess(command -> updateFullEventInfo(command.getResult()))
                  .onFail(this::handleError));
   }

   private void subscribeToBucketDetailsUpdates() {
      bucketInteractor.updatePipe()
            .observe()
            .compose(bindViewToMainComposer())
            .subscribe(new ActionStateSubscriber<UpdateBucketItemCommand>()
                  .onSuccess(updateBucketItemCommand -> updateFullEventInfo(updateBucketItemCommand.getResult()))
                  .onFail((updateBucketItemCommand, throwable) -> {
                     if (feedEntity.getUid().equals(updateBucketItemCommand.getBucketItemId())) {
                        handleError(updateBucketItemCommand, throwable);
                     }
                  }));
   }

   @Override
   public void updateFeedEntity(FeedEntity updatedFeedEntity) {
      if (updatedFeedEntity.equals(feedItem.getItem())) {
         feedItem.setItem(updatedFeedEntity);
         feedEntity = updatedFeedEntity;
         refreshFeedItem();
      }
   }

   @Override
   public void deleteFeedEntity(FeedEntity deletedFeedEntity) {
      if (feedEntity.equals(deletedFeedEntity)) {
         back();
      }
   }

   private void refreshFeedItem() {
      view.updateFeedItem(feedItem);
   }

   @Override
   public void onLikeItem(FeedItem feedItem) {
      feedActionHandlerDelegate.onLikeItem(feedItem);
   }

   private void subscribeToLikesChanges() {
      feedInteractor.changeFeedEntityLikedStatusPipe()
            .observe()
            .compose(bindViewToMainComposer())
            .subscribe(new ActionStateSubscriber<ChangeFeedEntityLikedStatusCommand>()
                  .onSuccess(command -> onItemLiked(command.getResult()))
                  .onFail(this::handleError));
   }

   private void onItemLiked(FeedEntity updatedFeedEntity) {
      if (updatedFeedEntity.equals(feedItem.getItem())) {
         feedEntity.setLikesCount(updatedFeedEntity.getLikesCount());
         feedEntity.setLiked(updatedFeedEntity.isLiked());
         refreshFeedItem();
      }
   }

   @Override
   public void onEditTextualPost(TextualPost textualPost) {
      feedActionHandlerDelegate.onEditTextualPost(textualPost);
   }

   @Override
   public void onDeleteTextualPost(TextualPost textualPost) {
      feedActionHandlerDelegate.onDeleteTextualPost(textualPost);
   }

   @Override
   public void onDeleteVideo(Video video) {
      feedInteractor.deleteVideoPipe().send(new DeleteVideoCommand(video));
   }

   @Override
   public void onEditPhoto(Photo photo) {
      feedActionHandlerDelegate.onEditPhoto(photo);
   }

   @Override
   public void onDeletePhoto(Photo photo) {
      feedActionHandlerDelegate.onDeletePhoto(photo);
   }

   @Override
   public void onEditBucketItem(BucketItem bucketItem, BucketItem.BucketType type) {
      feedActionHandlerDelegate.onEditBucketItem(bucketItem, type);
   }

   @Override
   public void onDeleteBucketItem(BucketItem bucketItem) {
      feedActionHandlerDelegate.onDeleteBucketItem(bucketItem);
   }

   public interface View extends BaseCommentPresenter.View, FeedEntityEditingView {

      void setFeedItem(FeedItem feedItem);

      void updateFeedItem(FeedItem feedItem);
   }
}
