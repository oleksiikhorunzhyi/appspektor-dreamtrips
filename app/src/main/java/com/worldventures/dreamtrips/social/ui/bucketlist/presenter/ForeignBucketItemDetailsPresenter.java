package com.worldventures.dreamtrips.social.ui.bucketlist.presenter;

import com.worldventures.dreamtrips.social.ui.bucketlist.model.BucketItem;

public class ForeignBucketItemDetailsPresenter extends BucketItemDetailsPresenter {

   public ForeignBucketItemDetailsPresenter(BucketItem.BucketType type, BucketItem bucketItem, int ownerId) {
      super(type, bucketItem, ownerId);
   }
}
