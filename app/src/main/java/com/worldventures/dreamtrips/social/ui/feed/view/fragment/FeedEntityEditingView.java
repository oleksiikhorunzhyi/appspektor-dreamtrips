package com.worldventures.dreamtrips.social.ui.feed.view.fragment;

import com.worldventures.dreamtrips.social.ui.bucketlist.model.BucketItem;
import com.worldventures.dreamtrips.social.ui.feed.model.TextualPost;
import com.worldventures.dreamtrips.social.ui.tripsimages.model.Photo;

public interface FeedEntityEditingView {

   void openEditTextualPost(TextualPost textualPost);

   void openEditPhoto(Photo photo);

   void openEditBucketItem(BucketItem bucketItem, BucketItem.BucketType type);
}
