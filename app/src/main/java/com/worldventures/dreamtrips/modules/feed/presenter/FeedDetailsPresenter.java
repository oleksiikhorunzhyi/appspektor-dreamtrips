package com.worldventures.dreamtrips.modules.feed.presenter;

import com.worldventures.dreamtrips.modules.bucketlist.model.BucketItem;
import com.worldventures.dreamtrips.modules.bucketlist.service.BucketInteractor;
import com.worldventures.dreamtrips.modules.bucketlist.service.action.UpdateBucketItemCommand;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.feed.event.FeedEntityChangedEvent;
import com.worldventures.dreamtrips.modules.feed.event.FeedEntityCommentedEvent;
import com.worldventures.dreamtrips.modules.feed.model.FeedEntity;
import com.worldventures.dreamtrips.modules.feed.model.FeedItem;
import com.worldventures.dreamtrips.modules.feed.model.TextualPost;
import com.worldventures.dreamtrips.modules.feed.model.TripFeedItem;
import com.worldventures.dreamtrips.modules.feed.presenter.delegate.FeedActionHandlerDelegate;
import com.worldventures.dreamtrips.modules.feed.presenter.delegate.FeedEntityHolderDelegate;
import com.worldventures.dreamtrips.modules.feed.service.FeedInteractor;
import com.worldventures.dreamtrips.modules.feed.service.command.ChangeFeedEntityLikedStatusCommand;
import com.worldventures.dreamtrips.modules.feed.service.command.GetFeedEntityCommand;
import com.worldventures.dreamtrips.modules.feed.view.fragment.FeedEntityEditingView;
import com.worldventures.dreamtrips.modules.trips.command.GetTripDetailsCommand;
import com.worldventures.dreamtrips.modules.trips.service.TripsInteractor;
import com.worldventures.dreamtrips.modules.tripsimages.model.Photo;

import javax.inject.Inject;

import io.techery.janet.helper.ActionStateSubscriber;

public abstract class FeedDetailsPresenter<V extends FeedDetailsPresenter.View> extends BaseCommentPresenter<V>
   implements FeedEditEntityPresenter, FeedEntityHolder {

   protected FeedItem feedItem;

   @Inject FeedEntityHolderDelegate feedEntityHolderDelegate;
   @Inject FeedActionHandlerDelegate feedActionHandlerDelegate;
   @Inject TripsInteractor tripsInteractor;
   @Inject BucketInteractor bucketInteractor;
   @Inject FeedInteractor feedInteractor;

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
      if (!isTrip())
         feedInteractor.getFeedEntityPipe()
               .createObservable(new GetFeedEntityCommand(feedEntity.getUid(), feedItem.getType()))
               .compose(bindViewToMainComposer())
               .subscribe(new ActionStateSubscriber<GetFeedEntityCommand>()
                     .onSuccess(getFeedEntityCommand -> updateFullEventInfo(getFeedEntityCommand.getResult()))
                     .onFail(this::handleError));
   }

   protected void updateFullEventInfo(FeedEntity updatedFeedEntity) {
      if (!updatedFeedEntity.getUid().equals(feedEntity.getUid())) return;
      feedEntity = updatedFeedEntity;
      feedEntity.setComments(null);
      feedItem.setItem(feedEntity);
      eventBus.post(new FeedEntityChangedEvent(feedEntity));
      checkCommentsAndLikesToLoad();
      refreshFeedItems();
      view.showAdditionalInfo(feedEntity.getOwner());
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
                  .onFail((updateBucketItemCommand, throwable) -> {
                     if (feedEntity.getUid().equals(updateBucketItemCommand.getBucketItemId())) {
                        handleError(updateBucketItemCommand, throwable);
                     }
                  }));
   }

   public void onEventMainThread(FeedEntityChangedEvent event) {
      updateFeedEntity(event.getFeedEntity());
   }

   @Override
   public void updateFeedEntity(FeedEntity updatedFeedEntity) {
      if (updatedFeedEntity.equals(feedItem.getItem())) {
         feedItem.setItem(updatedFeedEntity);
         feedEntity = updatedFeedEntity;
         refreshFeedItems();
      }
   }

   @Override
   public void deleteFeedEntity(FeedEntity deletedFeedEntity) {
      if (feedEntity.equals(deletedFeedEntity)) {
         back();
      }
   }

   public void refreshFeedItems() {
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
                  .onSuccess(this::likeStatusChanged)
                  .onFail(this::handleError));
   }

   private void likeStatusChanged(ChangeFeedEntityLikedStatusCommand command) {
      feedEntity.syncLikeState(command.getResult());
      eventBus.post(new FeedEntityChangedEvent(feedEntity));
   }

   public void onEvent(FeedEntityCommentedEvent event) {
      if (event.getFeedEntity().equals(feedItem.getItem())) {
         feedItem.setItem(event.getFeedEntity());
         feedEntity = event.getFeedEntity();
         refreshFeedItems();
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

      void showAdditionalInfo(User user);
   }
}
