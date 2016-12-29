package com.worldventures.dreamtrips.modules.feed.view.cell.delegate;

import com.worldventures.dreamtrips.modules.feed.model.FeedItem;
import com.worldventures.dreamtrips.modules.feed.presenter.FeedActionHandlerPresenter;
import com.worldventures.dreamtrips.modules.feed.view.cell.Flaggable;
import com.worldventures.dreamtrips.modules.feed.view.cell.base.BaseFeedCell;

public class FeedCellDelegate<P extends FeedActionHandlerPresenter, T extends FeedItem> implements BaseFeedCell.FeedCellDelegate<T> {

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
}
