package com.worldventures.dreamtrips.modules.feed.view.cell.delegate;

import com.worldventures.dreamtrips.modules.bucketlist.model.BucketItem;
import com.worldventures.dreamtrips.modules.feed.model.FeedEntity;
import com.worldventures.dreamtrips.modules.feed.model.FeedItem;
import com.worldventures.dreamtrips.modules.feed.model.TextualPost;
import com.worldventures.dreamtrips.modules.feed.presenter.FeedActionHandlerPresenter;
import com.worldventures.dreamtrips.modules.feed.presenter.FeedEditEntityPresenter;
import com.worldventures.dreamtrips.modules.feed.view.cell.Flaggable;
import com.worldventures.dreamtrips.modules.feed.view.cell.base.BaseFeedCell;
import com.worldventures.dreamtrips.modules.tripsimages.model.Photo;

public class FeedCellDelegate<P extends FeedActionHandlerPresenter & FeedEditEntityPresenter,
      T extends FeedItem> implements BaseFeedCell.FeedCellDelegate<T> {

   private P presenter;

   public FeedCellDelegate(P presenter) {
      this.presenter = presenter;
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
      presenter.onFlagItem(feedItem, flagReasonId, reason);
   }

   @Override
   public void onCellClicked(T model) {

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
