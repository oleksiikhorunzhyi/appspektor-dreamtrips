package com.worldventures.dreamtrips.social.ui.feed.view.cell.delegate;

import com.worldventures.dreamtrips.social.ui.bucketlist.model.BucketItem;
import com.worldventures.dreamtrips.social.ui.feed.model.FeedEntity;
import com.worldventures.dreamtrips.social.ui.feed.model.FeedItem;
import com.worldventures.dreamtrips.social.ui.feed.model.TextualPost;
import com.worldventures.dreamtrips.social.ui.feed.model.video.Video;
import com.worldventures.dreamtrips.social.ui.feed.presenter.FeedActionHandlerPresenter;
import com.worldventures.dreamtrips.social.ui.feed.presenter.FeedEditEntityPresenter;
import com.worldventures.dreamtrips.social.ui.feed.view.cell.Flaggable;
import com.worldventures.dreamtrips.social.ui.feed.view.cell.base.BaseFeedCell;
import com.worldventures.dreamtrips.social.ui.tripsimages.model.Photo;

import rx.functions.Action1;

public class FeedCellDelegate<P extends FeedActionHandlerPresenter & FeedEditEntityPresenter,
      T extends FeedItem> implements BaseFeedCell.FeedCellDelegate<T> {

   private final P presenter;
   private Action1<T> onEntityShownInCellAction;

   public FeedCellDelegate(P presenter) {
      this.presenter = presenter;
   }

   public void setOnEntityShownInCellAction(Action1<T> onEntityShownInCellAction) {
      this.onEntityShownInCellAction = onEntityShownInCellAction;
   }

   @Override
   public void onEntityShownInCell(T feedItem) {
      if (onEntityShownInCellAction != null) {
         onEntityShownInCellAction.call(feedItem);
      }
   }

   @Override
   public void onLikeItem(T feedItem) {
      presenter.onLikeItem(feedItem);
   }

   @Override
   public void onCommentItem(T feedItem) {
      presenter.onCommentItem(feedItem);
   }

   @Override
   public void onDownloadImage(String url) {
      presenter.onDownloadImage(url);
   }

   @Override
   public void onLoadFlags(Flaggable flaggable) {
      presenter.onLoadFlags(flaggable);
   }

   @Override
   public void onFlagChosen(FeedItem feedItem, int flagReasonId, String reason) {
      presenter.onFlagItem(feedItem.getItem().getUid(), flagReasonId, reason);
   }

   @Override
   public void onCellClicked(T model) {
      //do nothing
   }

   @Override
   public void onEditTextualPost(TextualPost textualPost) {
      presenter.onEditTextualPost(textualPost);
   }

   @Override
   public void onDeleteTextualPost(TextualPost textualPost) {
      presenter.onDeleteTextualPost(textualPost);
   }

   @Override
   public void onDeleteVideo(Video video) {
      presenter.onDeleteVideo(video);
   }

   @Override
   public void onTranslateItem(FeedEntity feedEntity) {
      presenter.onTranslateFeedEntity(feedEntity);
   }

   @Override
   public void onShowOriginal(FeedEntity feedEntity) {
      presenter.onShowOriginal(feedEntity);
   }

   @Override
   public void onEditPhoto(Photo photo) {
      presenter.onEditPhoto(photo);
   }

   @Override
   public void onDeletePhoto(Photo photo) {
      presenter.onDeletePhoto(photo);
   }

   @Override
   public void onEditBucketItem(BucketItem bucketItem, BucketItem.BucketType type) {
      presenter.onEditBucketItem(bucketItem, type);
   }

   @Override
   public void onDeleteBucketItem(BucketItem bucketItem) {
      presenter.onDeleteBucketItem(bucketItem);
   }
}
