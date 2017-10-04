package com.worldventures.dreamtrips.social.ui.feed.presenter;

import com.worldventures.dreamtrips.social.ui.feed.model.FeedEntity;
import com.worldventures.dreamtrips.social.ui.feed.model.FeedItem;
import com.worldventures.dreamtrips.social.ui.feed.view.cell.Flaggable;

public interface FeedActionHandlerPresenter {

   void onLikeItem(FeedItem feedItem);

   void onCommentItem(FeedItem feedItem);

   void onDownloadImage(String url);

   void onLoadFlags(Flaggable flaggableView);

   void onFlagItem(String uid, int flagReasonId, String reason);

   void onTranslateFeedEntity(FeedEntity translatableItem);

   void onShowOriginal(FeedEntity translatableItem);
}
