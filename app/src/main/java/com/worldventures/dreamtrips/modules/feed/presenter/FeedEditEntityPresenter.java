package com.worldventures.dreamtrips.modules.feed.presenter;

import com.worldventures.dreamtrips.modules.bucketlist.model.BucketItem;
import com.worldventures.dreamtrips.modules.feed.model.TextualPost;
import com.worldventures.dreamtrips.modules.tripsimages.model.Photo;

public interface FeedEditEntityPresenter {

   void onEditTextualPost(TextualPost textualPost);

   void onDeleteTextualPost(TextualPost textualPost);

   void onEditPhoto(Photo photo);

   void onDeletePhoto(Photo photo);

   void onEditBucketItem(BucketItem bucketItem, BucketItem.BucketType type);

   void onDeleteBucketItem(BucketItem bucketItem);
}
