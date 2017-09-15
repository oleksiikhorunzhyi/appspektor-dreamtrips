package com.worldventures.dreamtrips.social.domain;

import com.worldventures.dreamtrips.social.ui.bucketlist.model.BucketItem;

import java.util.List;

public interface SocialSnappyStorage {

   void saveBucketList(List<BucketItem> items, int userId);

   List<BucketItem> readBucketList(int userId);

   void saveOpenBucketTabType(String type);

   String getOpenBucketTabType();


}
