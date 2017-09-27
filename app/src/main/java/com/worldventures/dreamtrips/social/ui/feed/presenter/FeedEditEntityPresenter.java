package com.worldventures.dreamtrips.social.ui.feed.presenter;

import com.worldventures.dreamtrips.social.ui.bucketlist.model.BucketItem;
import com.worldventures.dreamtrips.social.ui.feed.model.TextualPost;
import com.worldventures.dreamtrips.social.ui.feed.model.video.Video;
import com.worldventures.dreamtrips.social.ui.tripsimages.model.Photo;

public interface FeedEditEntityPresenter {

   void onEditTextualPost(TextualPost textualPost);

   void onDeleteTextualPost(TextualPost textualPost);

   void onDeleteVideo(Video video);

   void onEditPhoto(Photo photo);

   void onDeletePhoto(Photo photo);

   void onEditBucketItem(BucketItem bucketItem, BucketItem.BucketType type);

   void onDeleteBucketItem(BucketItem bucketItem);
}
