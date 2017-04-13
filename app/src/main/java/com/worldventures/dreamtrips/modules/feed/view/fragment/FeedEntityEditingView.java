package com.worldventures.dreamtrips.modules.feed.view.fragment;

import com.worldventures.dreamtrips.modules.bucketlist.model.BucketItem;
import com.worldventures.dreamtrips.modules.feed.model.TextualPost;
import com.worldventures.dreamtrips.modules.tripsimages.model.Photo;

public interface FeedEntityEditingView {

   void openEditTextualPost(TextualPost textualPost);

   void openEditPhoto(Photo photo);

   void openEditBucketItem(BucketItem bucketItem, BucketItem.BucketType type);
}
