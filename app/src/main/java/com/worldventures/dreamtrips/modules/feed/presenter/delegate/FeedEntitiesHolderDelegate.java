package com.worldventures.dreamtrips.modules.feed.presenter.delegate;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.modules.bucketlist.service.BucketInteractor;
import com.worldventures.dreamtrips.modules.bucketlist.service.action.UpdateBucketItemCommand;
import com.worldventures.dreamtrips.modules.bucketlist.service.command.DeleteBucketItemCommand;
import com.worldventures.dreamtrips.modules.feed.model.FeedEntity;
import com.worldventures.dreamtrips.modules.feed.model.FeedItem;
import com.worldventures.dreamtrips.modules.feed.presenter.FeedItemsHolder;
import com.worldventures.dreamtrips.modules.feed.service.PostsInteractor;
import com.worldventures.dreamtrips.modules.feed.service.command.DeletePostCommand;
import com.worldventures.dreamtrips.modules.feed.service.command.EditPostCommand;
import com.worldventures.dreamtrips.modules.feed.service.command.PostCreatedCommand;
import com.worldventures.dreamtrips.modules.tripsimages.service.TripImagesInteractor;
import com.worldventures.dreamtrips.modules.tripsimages.service.command.DeletePhotoCommand;
import com.worldventures.dreamtrips.modules.tripsimages.service.command.EditPhotoWithTagsCommand;

import java.util.ArrayList;
import java.util.List;

import io.techery.janet.Command;
import io.techery.janet.helper.ActionStateSubscriber;
import rx.Observable;
import rx.functions.Action2;

public class FeedEntitiesHolderDelegate {

   private TripImagesInteractor tripImagesInteractor;
   private PostsInteractor postsInteractor;
   private BucketInteractor bucketInteractor;

   public FeedEntitiesHolderDelegate(TripImagesInteractor tripImagesInteractor, PostsInteractor postsInteractor,
         BucketInteractor bucketInteractor) {
      this.tripImagesInteractor = tripImagesInteractor;
      this.postsInteractor = postsInteractor;
      this.bucketInteractor = bucketInteractor;
   }

   public void subscribeToUpdates(FeedItemsHolder feedItemsHolder, Observable.Transformer stopper,
         Action2<Command, Throwable> errorAction) {
      postsInteractor.postCreatedPipe()
            .observe()
            .compose(bind(stopper))
            .subscribe(new ActionStateSubscriber<PostCreatedCommand>()
                  .onSuccess(createPostCommand -> feedItemsHolder.addFeedItem(createPostCommand.getFeedItem()))
                  .onFail(errorAction::call));

      postsInteractor.getEditPostPipe()
            .observe()
            .compose(bind(stopper))
            .subscribe(new ActionStateSubscriber<EditPostCommand>()
                  .onSuccess(editPostCommand -> feedItemsHolder.updateFeedEntity(editPostCommand.getResult()))
                  .onFail(errorAction::call));

      postsInteractor.deletePostPipe()
            .observeWithReplay()
            .compose(bind(stopper))
            .subscribe(new ActionStateSubscriber<DeletePostCommand>()
                  .onSuccess(deletePostCommand -> feedItemsHolder.deleteFeedEntity(deletePostCommand.getUid()))
                  .onFail(errorAction::call));

      tripImagesInteractor.editPhotoWithTagsCommandActionPipe()
            .observeWithReplay()
            .compose(bind(stopper))
            .subscribe(new ActionStateSubscriber<EditPhotoWithTagsCommand>()
                  .onSuccess(deletePhotoCommand -> feedItemsHolder.updateFeedEntity(deletePhotoCommand.getResult()))
                  .onFail(errorAction::call));

      tripImagesInteractor.deletePhotoPipe()
            .observeWithReplay()
            .compose(bind(stopper))
            .subscribe(new ActionStateSubscriber<DeletePhotoCommand>()
                  .onSuccess(deletePhotoCommand -> feedItemsHolder.deleteFeedEntity(deletePhotoCommand.getUid()))
                  .onFail(errorAction::call));

      bucketInteractor.updatePipe().observe()
            .compose(bind(stopper))
            .subscribe(new ActionStateSubscriber<UpdateBucketItemCommand>()
                  .onSuccess(updateBucketItemCommand -> feedItemsHolder.updateFeedEntity(updateBucketItemCommand.getResult()))
                  .onFail(errorAction::call));

      bucketInteractor.deleteItemPipe()
            .observeWithReplay()
            .compose(bind(stopper))
            .subscribe(new ActionStateSubscriber<DeleteBucketItemCommand>()
                  .onSuccess(deleteItemCommand -> feedItemsHolder.deleteFeedEntity(deleteItemCommand.getBucketItemUid()))
                  .onFail(errorAction::call));
   }

   protected <T> Observable.Transformer<T, T> bind(Observable.Transformer stopper) {
      return input -> input.compose(stopper);
   }

   public void updateFeedItemInList(ArrayList<FeedItem> feedItems, FeedEntity updatedFeedEntity) {
      Queryable.from(feedItems).forEachR(item -> {
         if (item.getItem() != null && item.getItem().equals(updatedFeedEntity)) {
            FeedEntity feedEntity = updatedFeedEntity;
            if (feedEntity.getOwner() == null) {
               feedEntity.setOwner(item.getItem().getOwner());
            }
            item.setItem(feedEntity);
         }
      });
   }

   public void deleteFeedItemInList(List<FeedItem> feedItems, String feedItemUid) {
      List<FeedItem> filteredItems = Queryable.from(feedItems)
            .filter(element -> !element.getItem().getUid().equals(feedItemUid))
            .toList();

      feedItems.clear();
      feedItems.addAll(filteredItems);
   }
}
