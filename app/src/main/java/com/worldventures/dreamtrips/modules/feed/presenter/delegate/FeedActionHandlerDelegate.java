package com.worldventures.dreamtrips.modules.feed.presenter.delegate;

import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketItem;
import com.worldventures.dreamtrips.modules.bucketlist.service.BucketInteractor;
import com.worldventures.dreamtrips.modules.bucketlist.service.command.DeleteBucketItemCommand;
import com.worldventures.dreamtrips.modules.common.model.FlagData;
import com.worldventures.dreamtrips.modules.common.presenter.delegate.FlagDelegate;
import com.worldventures.dreamtrips.modules.feed.model.FeedEntityHolder;
import com.worldventures.dreamtrips.modules.feed.model.FeedItem;
import com.worldventures.dreamtrips.modules.feed.model.TextualPost;
import com.worldventures.dreamtrips.modules.feed.service.FeedInteractor;
import com.worldventures.dreamtrips.modules.feed.service.PostsInteractor;
import com.worldventures.dreamtrips.modules.feed.service.command.ChangeFeedEntityLikedStatusCommand;
import com.worldventures.dreamtrips.modules.feed.service.command.DeletePostCommand;
import com.worldventures.dreamtrips.modules.feed.view.cell.Flaggable;
import com.worldventures.dreamtrips.modules.feed.view.fragment.FeedEntityEditingView;
import com.worldventures.dreamtrips.modules.flags.service.FlagsInteractor;
import com.worldventures.dreamtrips.modules.tripsimages.model.Photo;
import com.worldventures.dreamtrips.modules.tripsimages.service.TripImagesInteractor;
import com.worldventures.dreamtrips.modules.tripsimages.service.command.DeletePhotoCommand;
import com.worldventures.dreamtrips.modules.tripsimages.service.command.DownloadImageCommand;

import io.techery.janet.Command;
import io.techery.janet.helper.ActionStateSubscriber;
import rx.Observable;
import rx.functions.Action2;

public class FeedActionHandlerDelegate {

   private FeedInteractor feedInteractor;
   private FlagDelegate flagDelegate;
   private TripImagesInteractor tripImagesInteractor;
   private PostsInteractor postsInteractor;
   private BucketInteractor bucketInteractor;
   private FeedEntityEditingView feedEntityEditingView;

   public FeedActionHandlerDelegate(FeedInteractor feedInteractor, FlagsInteractor flagsInteractor,
         TripImagesInteractor tripImagesInteractor, PostsInteractor postsInteractor, BucketInteractor bucketInteractor) {
      this.feedInteractor = feedInteractor;
      this.flagDelegate = new FlagDelegate(flagsInteractor);
      this.tripImagesInteractor = tripImagesInteractor;
      this.postsInteractor = postsInteractor;
      this.bucketInteractor = bucketInteractor;
   }

   public void setFeedEntityEditingView(FeedEntityEditingView feedEntityEditingView) {
      this.feedEntityEditingView = feedEntityEditingView;
   }

   public void onLikeItem(FeedItem feedItem) {
      feedInteractor.changeFeedEntityLikedStatusPipe()
            .send(new ChangeFeedEntityLikedStatusCommand(feedItem.getItem()));

      String id = feedItem.getItem().getUid();
      FeedEntityHolder.Type type = feedItem.getType();
      if (type != FeedEntityHolder.Type.UNDEFINED && !feedItem.getItem().isLiked()) {
         //send this event only if user likes item, if dislikes - skip
         TrackingHelper.sendActionItemFeed(TrackingHelper.ATTRIBUTE_LIKE, id, type);
      }
   }

   public void onLoadFlags(Flaggable flaggableView, Action2<Command, Throwable> errorAction) {
      flagDelegate.loadFlags(flaggableView, errorAction);
   }

   public void onFlagItem(String uid, int flagReasonId, String reason, FlagDelegate.View view,
         Action2<Command, Throwable> errorAction) {
      flagDelegate.flagItem(new FlagData(uid, flagReasonId, reason), view, errorAction);
   }

   public void onDownloadImage(String url, Observable.Transformer stopper, Action2<Command, Throwable> errorAction) {
      tripImagesInteractor.downloadImageActionPipe()
            .createObservable(new DownloadImageCommand(url))
            .compose(stopper)
            .subscribe(new ActionStateSubscriber<DownloadImageCommand>()
                  .onFail(errorAction::call));
   }

   public void onEditTextualPost(TextualPost textualPost) {
      feedEntityEditingView.openEditTextualPost(textualPost);
   }

   public void onDeleteTextualPost(TextualPost textualPost) {
      postsInteractor.deletePostPipe().send(new DeletePostCommand(textualPost.getUid()));
   }

   public void onEditPhoto(Photo photo) {
      feedEntityEditingView.openEditPhoto(photo);
   }

   public void onDeletePhoto(Photo photo) {
      tripImagesInteractor.deletePhotoPipe().send(new DeletePhotoCommand(photo.getUid()));
   }

   public void onEditBucketItem(BucketItem bucketItem, BucketItem.BucketType bucketType) {
      feedEntityEditingView.openEditBucketItem(bucketItem, bucketType);
   }

   public void onDeleteBucketItem(BucketItem bucketItem) {
      bucketInteractor.deleteItemPipe().send(new DeleteBucketItemCommand(bucketItem.getUid()));
   }
}
