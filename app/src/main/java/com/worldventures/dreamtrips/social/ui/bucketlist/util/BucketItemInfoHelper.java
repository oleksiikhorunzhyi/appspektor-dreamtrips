package com.worldventures.dreamtrips.social.ui.bucketlist.util;

import com.worldventures.dreamtrips.social.ui.bucketlist.model.BucketItem;

public interface BucketItemInfoHelper {

   String getMediumResUrl(BucketItem bucketItem);

   String getHighResUrl(BucketItem bucketItem);

   String getPlace(BucketItem bucketItem);

   String getTime(BucketItem bucketItem);
}
