package com.worldventures.dreamtrips.modules.feed.presenter;

import com.worldventures.dreamtrips.modules.feed.model.FeedItem;
import com.worldventures.dreamtrips.modules.feed.view.cell.Flaggable;

public interface FeedActionHandlerPresenter {

   void onLikeItem(FeedItem feedItem);

   void onCommentItem(FeedItem feedItem);

   void onDownloadImage(String url);

   void onLoadFlags(Flaggable flaggableView);

   void onFlagItem(FeedItem feedItem, int flagReasonId, String reason);
}
